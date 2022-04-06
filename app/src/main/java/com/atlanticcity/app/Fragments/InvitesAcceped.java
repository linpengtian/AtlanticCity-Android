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
import com.atlanticcity.app.Activities.MainActivity;
import com.atlanticcity.app.Activities.ViewInvites;
import com.atlanticcity.app.Adapters.InvitesAcceptedAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.DealsModel;
import com.atlanticcity.app.Models.UserModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.atlanticcity.app.Utils.GridSpacingItemDecoration;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;


public class InvitesAcceped extends Fragment {

    View view;
   // RecyclerView accepted_rv;
    LinearLayout no_layout,accepted_layout;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;

    public String TAG = "InvitesAccepted";

    RecyclerView rvPeopleAccepted;
    List<UserModel> userModelList = new ArrayList<>();
    InvitesAcceptedAdapter invitesAcceptedAdapter;
    public InvitesAcceped() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view =  inflater.inflate(R.layout.fragment_invites_acceped, container, false);
        viewInitializer();
        return  view;
    }


    void viewInitializer(){
        rvPeopleAccepted = view.findViewById(R.id.people_accepted_rv);
        GridLayoutManager gridLayoutManager1 = new GridLayoutManager(getActivity(), 1);
        rvPeopleAccepted.setLayoutManager(gridLayoutManager1);
        rvPeopleAccepted.setItemAnimator(new DefaultItemAnimator());
        rvPeopleAccepted.setNestedScrollingEnabled(false);
        rvPeopleAccepted.addItemDecoration(new GridSpacingItemDecoration(1, dpToPx(1), true));
        no_layout = view.findViewById(R.id.no_layout);
        accepted_layout = view.findViewById(R.id.accepted_layout);

        helper = new ConnectionHelper(getActivity());
        isInternet = helper.isConnectingToInternet();
        if(isInternet){
            getProfile();
        }else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }



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
        String id = SharedHelper.getKey(getContext(),"id");
        AndroidNetworking.get(URLHelper.INVITES_ACCEPTED+id)
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

                                    //JSONObject detailObj =  responseObj.getJSONObject("detail");
                                    Gson gson = new Gson();
                                    Type listType = new TypeToken<List<UserModel>>() {
                                    }.getType();
                                    userModelList = gson.fromJson(responseObj.getString("detail"), listType);
                                    if (userModelList.size()==0 || userModelList == null){
                                        no_layout.setVisibility(View.VISIBLE);
                                        accepted_layout.setVisibility(View.GONE);
                                    }else {
                                        no_layout.setVisibility(View.GONE);
                                        accepted_layout.setVisibility(View.VISIBLE);
                                        invitesAcceptedAdapter =  new InvitesAcceptedAdapter(userModelList);
                                        rvPeopleAccepted.setAdapter(invitesAcceptedAdapter);

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
