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
import com.atlanticcity.app.Adapters.FavouriteAddsAdapter;
import com.atlanticcity.app.Models.DetailBusinessModel;
import com.atlanticcity.app.Models.FavouriteAddsClass;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.atlanticcity.app.Adapters.FavouriteDealsAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.DetailModel;
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
public class FavoritesDeals extends Fragment {

    View view;
    RecyclerView favorite_deals_rv;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    List<DetailModel> detailModels ;
    FavouriteDealsAdapter favouriteDealsAdapter;
    RecyclerView favorite_adds_rv;
    List<FavouriteAddsClass> favouriteAddsClasses = new ArrayList<>();
    FavouriteAddsAdapter favouriteAddsAdapter;
    public FavoritesDeals() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_favorites_deals, container, false);
        viewInitializer();
        return view;
    }

    void viewInitializer(){
        favorite_deals_rv = view.findViewById(R.id.favorite_deals_rv);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getContext(), 1);
        favorite_deals_rv.setLayoutManager(gridLayoutManager1);
        favorite_deals_rv.setItemAnimator(new DefaultItemAnimator());
        favorite_deals_rv.setNestedScrollingEnabled(false);
        favorite_deals_rv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));

        favorite_adds_rv = view.findViewById(R.id.favorite_adds_rv);
        GridLayoutManager gridLayoutManager2 = new GridLayoutManager(getContext(), 1);
        favorite_adds_rv.setLayoutManager(gridLayoutManager2);
        favorite_adds_rv.setItemAnimator(new DefaultItemAnimator());
        favorite_adds_rv.setNestedScrollingEnabled(false);
        favorite_adds_rv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));


        helper = new ConnectionHelper(getContext());
        isInternet = helper.isConnectingToInternet();

        if(isInternet){
            GetFavouriteDeals();
        }else {
            Toast.makeText(getContext(), getResources().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
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

    public void GetFavouriteDeals() {

        showDialog();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URLHelper.GET_FAVOURITE_DEALS, new Response.Listener<String>() {
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

                           /* JSONArray detail = responseObj.getJSONArray("detail");
                            JSONObject detailOBj = detail.getJSONObject(1);*/
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<DetailModel>>() {
                            }.getType();
                            detailModels = new ArrayList<>();
                            detailModels = gson.fromJson(responseObj.getString("detail"), listType);

                            List<DetailModel> favoritesDealsModel = new ArrayList<>();

                            for(int i = 0 ; i < detailModels.size() ; i++){
                                if(detailModels.get(i).getDealsModels()!=null){
                                    favoritesDealsModel.add(detailModels.get(i));
                                }
                            }

                            favouriteDealsAdapter = new FavouriteDealsAdapter(favoritesDealsModel,FavoritesDeals.this);
                            favorite_deals_rv.setAdapter(favouriteDealsAdapter);
                            favouriteDealsAdapter.notifyDataSetChanged();
                          //  dismissDialog();


                            if(isInternet){
                                GetFavouriteAdds();
                            }else {
                                Toast.makeText(getContext(), getResources().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
                            }


                        }
                    }

                }catch (Exception ex){
                    ex.printStackTrace();
                    dismissDialog();
                }


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
                        GetFavouriteDeals();
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
         /*       headers.put("user-id",  "c49de408-92d6-4c09-8a2f-dc38786f1312");
                headers.put("auth-id", "atlantic_city_5e84c82cf2cdd");*/
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
            GetFavouriteDeals();
        }else {
            Toast.makeText(getContext(), getResources().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }
    }

    public void GetFavouriteAdds() {

     //   showDialog();
        StringRequest jsonObjectRequest = new StringRequest(Request.Method.POST, URLHelper.GET_FAVOURITE_ADDS, new Response.Listener<String>() {
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

                           /* JSONArray detail = responseObj.getJSONArray("detail");
                            JSONObject detailOBj = detail.getJSONObject(1);*/
                            Gson gson = new Gson();
                            Type listType = new TypeToken<List<FavouriteAddsClass>>() {
                            }.getType();
                            favouriteAddsClasses = gson.fromJson(responseObj.getString("detail"), listType);

                            favouriteAddsAdapter = new FavouriteAddsAdapter(favouriteAddsClasses,FavoritesDeals.this);
                            favorite_adds_rv.setAdapter(favouriteAddsAdapter);
                            favouriteAddsAdapter.notifyDataSetChanged();
                            favorite_deals_rv.setVisibility(View.VISIBLE);
                            favorite_adds_rv.setVisibility(View.VISIBLE);


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
                        GetFavouriteAdds();
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
}
