package com.atlanticcity.app.Fragments;

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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.GridSpacingItemDecoration;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 */
public class InvitesSent extends Fragment {

    View view;
    LinearLayout no_layout,invites_sent_layout;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    TextView invite_sent_text;
    String referral_sent_count,referral_accept_count;
    public String TAG = "InvitesSent";
   // RecyclerView sent_rv;
    public InvitesSent() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_invites_sent, container, false);
        viewInitializer();
        return view;
    }

    void viewInitializer(){
      /*  sent_rv = view.findViewById(R.id.sent_rv);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 1);
        sent_rv.setLayoutManager(gridLayoutManager1);
        sent_rv.setItemAnimator(new DefaultItemAnimator());
        sent_rv.setNestedScrollingEnabled(false);
        sent_rv.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));*/
        no_layout = view.findViewById(R.id.no_layout);
        helper = new ConnectionHelper(getActivity());
        isInternet = helper.isConnectingToInternet();

        invites_sent_layout = view.findViewById(R.id.accepted_layout);
        invite_sent_text = view.findViewById(R.id.invitation_text);
        helper = new ConnectionHelper(getActivity());
        isInternet = helper.isConnectingToInternet();

    }


    private int dpToPx(int dp) {
        Resources r = getResources();
        return Math.round(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()));
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
                                    referral_sent_count = detailObj.optString("referral_sent_count");
                                    referral_accept_count = detailObj.optString("referral_accept_count");
                                    if (referral_sent_count.equalsIgnoreCase("0")){
                                        no_layout.setVisibility(View.VISIBLE);
                                        invites_sent_layout.setVisibility(View.GONE);
                                    }else {
                                        no_layout.setVisibility(View.GONE);
                                        invites_sent_layout.setVisibility(View.VISIBLE);
                                        invite_sent_text.setText("You have sent invites to\n"+referral_sent_count+"/10 friends.");
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

    @Override
    public void onResume() {
        super.onResume();
        if(isInternet){
            getProfile();
        }else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }
    }
}
