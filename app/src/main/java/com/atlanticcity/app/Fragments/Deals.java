package com.atlanticcity.app.Fragments;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.atlanticcity.app.Adapters.DealsAdapter;
import com.atlanticcity.app.Helper.ConnectionHelper;
import com.atlanticcity.app.Helper.SharedHelper;
import com.atlanticcity.app.Helper.URLHelper;
import com.atlanticcity.app.Models.DealsModel;
import com.atlanticcity.app.R;
import com.atlanticcity.app.Utils.CustomDialog;
import com.yuyakaido.android.cardstackview.CardStackLayoutManager;
import com.yuyakaido.android.cardstackview.CardStackListener;
import com.yuyakaido.android.cardstackview.CardStackView;
import com.yuyakaido.android.cardstackview.Direction;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import static com.facebook.FacebookSdk.getApplicationContext;
import static com.atlanticcity.app.Activities.SplashActivity.GlobalDealsData;

/**
 * A simple {@link Fragment} subclass.
 */
public class Deals extends Fragment implements CardStackListener {

    CardStackView card_stack_view;
    View view;
    ConnectionHelper helper;
    Boolean isInternet;
    CustomDialog customDialog;
    List<DealsModel> dealsModel = new ArrayList<>();
    DealsAdapter dealsAdapter;
    public String TAG = "DEALS";
    CardStackLayoutManager cardStackLayoutManager;
    public Deals() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_deals, container, false);


        viewInitializer();
        return view;
    }

    void viewInitializer(){
        card_stack_view = view.findViewById(R.id.card_stack_view);
        cardStackLayoutManager = new CardStackLayoutManager(getApplicationContext(), this);
        cardStackLayoutManager.setDirections(Direction.HORIZONTAL);
        card_stack_view.setLayoutManager(cardStackLayoutManager);
        cardStackLayoutManager.setCanScrollHorizontal(true);
        cardStackLayoutManager.setCanScrollVertical(false);
        card_stack_view.rewind();
        helper = new ConnectionHelper(getActivity());
        isInternet = helper.isConnectingToInternet();
        if(isInternet){
            if(GlobalDealsData.size()<=0){
                GetDeals();
            }else {
             //   dealsAdapter = new DealsAdapter(GlobalDealsData);
                card_stack_view.setAdapter(dealsAdapter);
            }

        }else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }
    }



    private void GetDeals() {
        showDialog();
        AndroidNetworking.post(URLHelper.GET_DEALS)
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
                                    Type listType = new TypeToken<List<DealsModel>>() {
                                    }.getType();
                                    dealsModel = gson.fromJson(responseObj.getString("detail"), listType);
                                    GlobalDealsData = gson.fromJson(responseObj.getString("detail"), listType);
                                 //   dealsAdapter = new DealsAdapter(dealsModel);
                                    card_stack_view.setAdapter(dealsAdapter);
                                    //    card_stack_view.notifyDataSetChanged();

                                }
                            }

                        }catch (Exception ex){
                            ex.printStackTrace();
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

    @Override
    public void onCardDragging(Direction direction, float ratio) {

    }

    @Override
    public void onCardSwiped(Direction direction) {
      //  Toast.makeText(getActivity(), "swiped", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onCardRewound() {

    }

    @Override
    public void onCardCanceled() {

    }

    @Override
    public void onCardAppeared(View view, int position) {

        try {
            if(GlobalDealsData.size()<=0){
                if(dealsModel.size()-1 == position){

                    Log.v("card_position",String.valueOf(position));
                  //  dealsAdapter = new DealsAdapter(dealsModel);
                    card_stack_view.setAdapter(dealsAdapter);
                    dealsAdapter.notifyDataSetChanged();

                }
            }else {
                if(GlobalDealsData.size()-1 == position){

                    Log.v("card_position",String.valueOf(position));
                 //   dealsAdapter = new DealsAdapter(GlobalDealsData);
                    card_stack_view.setAdapter(dealsAdapter);
                    dealsAdapter.notifyDataSetChanged();

                }
            }



        }catch (Exception ex){
            ex.printStackTrace();
        }

    }

    @Override
    public void onCardDisappeared(View view, int position) {

    }

    @Override
    public void onResume() {
        super.onResume();
        dismissDialog();
        if(isInternet){
            if(GlobalDealsData.size()<=0){
                GetDeals();
            }else {
             //   dealsAdapter = new DealsAdapter(GlobalDealsData);
                card_stack_view.setAdapter(dealsAdapter);
            }

        }else {
            Toast.makeText(getActivity(), getActivity().getString(R.string.something_went_wrong_net), Toast.LENGTH_SHORT).show();
        }
    }
}
