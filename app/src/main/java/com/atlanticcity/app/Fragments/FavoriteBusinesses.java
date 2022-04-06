package com.atlanticcity.app.Fragments;


import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.atlanticcity.app.Adapters.FavoriteBusinessAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.DetailBusinessModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.CustomRequestQueue;
import com.atlanticcity.app.Utils.GridSpacingItemDecoration;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class FavoriteBusinesses extends Fragment {

    View view;
    RecyclerView favorite_business_rv;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    FavoriteBusinessAdapter businessAdapter;
    List<DetailBusinessModel> detailBusinessModels = new ArrayList<>();
    public FavoriteBusinesses() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_favorite_businesses, container, false);
        viewInitializer();
        return view;
    }

    void viewInitializer(){
        favorite_business_rv = view.findViewById(R.id.favorite_business_rv);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getContext(), 1);
        favorite_business_rv.setLayoutManager(gridLayoutManager1);
        favorite_business_rv.setItemAnimator(new DefaultItemAnimator());
        favorite_business_rv.setNestedScrollingEnabled(false);
        favorite_business_rv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        helper = new ConnectionHelper(getContext());
        isInternet = helper.isConnectingToInternet();

        if(isInternet){
            GetBusinesses();
        }else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }

    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
    }



    public void displayMessage(  String toastString) {

        try{
            Toast.makeText(getContext(),""+toastString,Toast.LENGTH_SHORT).show();
        }catch (Exception ee){
            ee.printStackTrace();
        }

    }

    void dismissDialog(){
        if ((customDialog != null) && (customDialog.isShowing()))
            customDialog.dismiss();
    }

    void showDialog(){
        customDialog = new CustomDialog(getContext());
        customDialog.setCancelable(false);
        if (customDialog != null)
            customDialog.show();
    }

    public void GetBusinesses() {

        showDialog();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URLHelper.GET_FAVORITE_BUSINESS, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //  if ((customDialog != null) && (customDialog.isShowing()))
                //customDialog.dismiss();

                try {

                    JSONObject response_obj = new JSONObject(response);
                    JSONObject errorObj = response_obj.getJSONObject("error");
                    if(errorObj.optString("status").equals("1")){
                        displayMessage(errorObj.optString("message"));
                    }else{
                        JSONObject responseObj = response_obj.getJSONObject("response");
                        if(responseObj.optString("status").equals("1")){

                            // JSONObject detail = responseObj.getJSONObject("detail");

                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<DetailBusinessModel>>() {
                            }.getType();
                            detailBusinessModels = gson.fromJson(responseObj.getString("detail"), listType);
                            List<DetailBusinessModel> favoritesBusinessModel = new ArrayList<>();

                            for(int i = 0 ; i < detailBusinessModels.size() ; i++){
                                if(detailBusinessModels.get(i).getBusinessModel()!=null){
                                    favoritesBusinessModel.add(detailBusinessModels.get(i));
                                }
                            }

                            businessAdapter = new FavoriteBusinessAdapter(favoritesBusinessModel,FavoriteBusinesses.this);
                            favorite_business_rv.setAdapter(businessAdapter);
                            businessAdapter.notifyDataSetChanged();




                        }
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                }
                dismissDialog();

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                dismissDialog();

                String json = null;
                String Message;
                NetworkResponse response = error.networkResponse;

                if (response != null && response.data != null) {

                    try {
                        JSONObject errorObj = new JSONObject(new String(response.data));

                        if(errorObj.optJSONObject("error").optString("status").equals("1")){
                            displayMessage(errorObj.optJSONObject("error").optString("message"));
                        }

                    } catch (Exception e) {
                        displayMessage(getString(R.string.something_went_wrong));
                    }


                } else {
                    if (error instanceof NoConnectionError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof NetworkError) {
                        displayMessage(getString(R.string.oops_connect_your_internet));
                    } else if (error instanceof TimeoutError) {
                        GetBusinesses();
                    }
                }

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("X-Requested-With", "XMLHttpRequest");
                headers.put("user-id", SharedHelper.getKey(getContext(),"user_id"));
                headers.put("auth-id", SharedHelper.getKey(getContext(),"auth_id"));
                return headers;
            }

        };

        CustomRequestQueue.getInstance(getContext()).addToRequestQueue(jsonObjectRequest);

    }

    @Override
    public void onResume() {
        super.onResume();
        dismissDialog();
        if(isInternet){
            GetBusinesses();
        }else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }
    }
}
