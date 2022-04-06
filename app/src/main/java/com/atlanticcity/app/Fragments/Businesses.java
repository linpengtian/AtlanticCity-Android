package com.atlanticcity.app.Fragments;


import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.AnalyticsListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.Activities.DateOfBirth;
import com.google.android.material.snackbar.Snackbar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.atlanticcity.app.Adapters.BusinessAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.BusinessModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.GridSpacingItemDecoration;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class Businesses extends Fragment {

    View view;
    RecyclerView business_rv;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    BusinessAdapter businessAdapter;
    List<BusinessModel> businessModels = new ArrayList<>();
    SwipeRefreshLayout swipeLayout;
    public String TAG = "BUSINESS";
    public Businesses() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_businesses, container, false);

        // Getting SwipeContainerLayout
        swipeLayout = view.findViewById(R.id.swipe_container);
        // Adding Listener
        swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                // Your code here

                // To keep animation for 4 seconds

            if(isInternet){
                GetBusinesses();
            }else {
                Toast.makeText(getActivity(), getActivity().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
            }


                    }


        });

        swipeLayout.setColorSchemeColors(
                getResources().getColor(R.color.colorPrimaryDark)
        );
        viewInitializer();

        return view;
    }

    void viewInitializer(){
        business_rv = view.findViewById(R.id.business_rv);
        helper = new ConnectionHelper(getActivity());
        isInternet = helper.isConnectingToInternet();
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 1);
        business_rv.setLayoutManager(gridLayoutManager1);
        business_rv.setItemAnimator(new DefaultItemAnimator());
        business_rv.setNestedScrollingEnabled(false);
        business_rv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
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

    @Override
    public void onResume() {
        super.onResume();
        Log.v("Business","OnResume Called");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.v("Business","OnStart Called");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("Business","Onpause Called");
    }

    private void GetBusinesses() {

        showDialog();
        AndroidNetworking.post(URLHelper.GET_BUSINESSES)
                .addHeaders("user-id", SharedHelper.getKey(getContext(),"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(getContext(),"auth_id"))
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()

                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        dismissDialog();
                        try {

                            JSONObject errorObj = response.getJSONObject("error");
                            if(errorObj.optString("status").equals("1")){
                                displayMessage(errorObj.optString("message"));
                            }else{
                                JSONObject responseObj = response.getJSONObject("response");
                                if(responseObj.optString("status").equals("1")){

                                    // JSONObject detail = responseObj.getJSONObject("detail");

                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<BusinessModel>>() {
                                    }.getType();
                                    businessModels = gson.fromJson(responseObj.getString("detail"), listType);
                                    businessAdapter = new BusinessAdapter(businessModels);
                                    business_rv.setAdapter(businessAdapter);
                                    businessAdapter.notifyDataSetChanged();

                                }
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                        }

                        if(swipeLayout.isRefreshing()){
                            swipeLayout.setRefreshing(false);
                        }
                    }
                    @Override
                    public void onError(ANError error) {
                        // handle error

                        dismissDialog();


                        if (error.getErrorCode() != 0) {
                            // received error from server
                            // error.getErrorCode() - the error code from server
                            // error.getErrorBody() - the error body from server
                            // error.getErrorDetail() - just an error detail
                            Log.d(TAG, "onError errorCode : " + error.getErrorCode());
                            Log.d(TAG, "onError errorBody : " + error.getErrorBody());
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                            // get parsed error object (If ApiError is your class)
                            String errorObj = error.getErrorBody();
                            try {
                                JSONObject errorJson = new JSONObject(errorObj);
                                if(errorJson.optJSONObject("error").optString("status").equals("1")){
                                    displayMessage(errorJson.optJSONObject("error").optString("message"));
                                }
                            }catch (Exception ex){
                                ex.printStackTrace();
                            }

                        } else {
                            // error.getErrorDetail() : connectionError, parseError, requestCancelledError
                            Log.d(TAG, "onError errorDetail : " + error.getErrorDetail());
                        }
                    }
                });
    }

    public void displayMessage(String toastString) {
        try {
            Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT).setAction("Action", null).show();
        }catch (Exception e){
            try{
                Toast.makeText(getActivity(),""+toastString,Toast.LENGTH_SHORT).show();
            }catch (Exception ee){
                ee.printStackTrace();
            }
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

    /*@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);

        if(isVisibleToUser){

        }
    }*/
}
