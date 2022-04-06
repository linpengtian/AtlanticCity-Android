package com.atlanticcity.app.Fragments;

import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.Activities.ClaimDealActivity;
import com.atlanticcity.app.Activities.SpinWheel;
import com.atlanticcity.app.Adapters.InvitesAcceptedAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.UserModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.GridSpacingItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 */
public class Earned extends Fragment {

    View view;

    LinearLayout no_layout;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    RelativeLayout rlInvitePrizeWon;
    String referral_accept_count;
    public String TAG = "Earned";
    Button btnClaimPrize;
    int guru_status;
    public Earned() {
        // Required empty public constructor
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_earned, container, false);
        viewInitializer();
        return view;
    }

    void viewInitializer(){
        no_layout = view.findViewById(R.id.no_layout);
        rlInvitePrizeWon = view.findViewById(R.id.rlInvitePrizeWon);
        btnClaimPrize = view.findViewById(R.id.btnClaimPrize);
        helper = new ConnectionHelper(getActivity());
        isInternet = helper.isConnectingToInternet();
        btnClaimPrize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            if(guru_status==0){
                Intent i = new Intent(getContext(), ClaimDealActivity.class);
                i.putExtra("is_dining","true");
                startActivity(i);
                getActivity().finish();
            }else {
                Toast.makeText(getContext(), "You have already claimed the prize!", Toast.LENGTH_SHORT).show();
            }

            }
        });

        if(isInternet){
            getProfile();
        }else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }
    }


    public void displayMessage(  String toastString) {
        try {
            Snackbar.make(getView(), toastString, Snackbar.LENGTH_SHORT)
                    .setAction("Action", null).show();
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

    private void getProfile() {
        showDialog();

        AndroidNetworking.post(URLHelper.GER_PROFILE)
                .addBodyParameter("user-id", SharedHelper.getKey(getContext(),"user_id"))
                .addHeaders("user-id", SharedHelper.getKey(getContext(),"user_id"))
                .addHeaders("auth-id", SharedHelper.getKey(getContext(),"auth_id"))
                .setTag("test")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        // do anything with response
                        try {
                            JSONObject errorObj = response.getJSONObject("error");
                            if(errorObj.optString("status").equals("1")){
                                displayMessage(errorObj.optString("message"));
                            }else{
                                JSONObject responseObj = response.getJSONObject("response");

                                if(responseObj.optString("status").equals("1")){

                                    JSONObject detailObj =  responseObj.getJSONObject("detail");

                                    referral_accept_count = detailObj.optString("referral_accept_count");
                                    String guruStatus = detailObj.optString("guru_status");
                                    guru_status = Integer.valueOf(guruStatus);
                                    int referralAcceptCount = Integer.valueOf(referral_accept_count);

                                    if (referralAcceptCount >= 10){
                                        no_layout.setVisibility(View.GONE);
                                        rlInvitePrizeWon.setVisibility(View.VISIBLE);
                                    }else {
                                        no_layout.setVisibility(View.VISIBLE);
                                        rlInvitePrizeWon.setVisibility(View.GONE);

                                    }
                                }
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
                        }
                        dismissDialog();

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


}
