package com.atlanticcity.app.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;


import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.firebase.auth.FirebaseAuth;
import com.atlanticcity.app.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class FbFriendsList extends AppCompatActivity {

    AccessToken token;
    private CallbackManager callbackManager;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fb_friends_list);
        callbackManager = CallbackManager.Factory.create();
        mAuth = FirebaseAuth.getInstance();

       // token = access_token;

        LoginManager.getInstance().logInWithReadPermissions(FbFriendsList.this, Arrays.asList("read_custom_friendlists", "email"));

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d("CreateAccount", "facebook:onSuccess:" + loginResult);

                token = loginResult.getAccessToken();
                //  handleFacebookAccessToken(loginResult.getAccessToken());

                GraphRequest graphRequest = GraphRequest.newMeRequest(token, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject jsonObject, GraphResponse graphResponse) {
                        try {
                            JSONArray jsonArrayFriends = jsonObject.getJSONObject("friendlist").getJSONArray("data");
                            JSONObject friendlistObject = jsonArrayFriends.getJSONObject(0);
                            String friendListID = friendlistObject.getString("id");
                            myNewGraphReq(friendListID);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle param = new Bundle();
                param.putString("fields", "friendlist");
                graphRequest.setParameters(param);
                graphRequest.executeAsync();
            }

            @Override
            public void onCancel() {
                Log.d("CreateAccount", "facebook:onCancel");
                // ...
            }

            @Override
            public void onError(FacebookException error) {
                Log.d("CreateAccount", "facebook:onError", error);
                // ...
            }
        });

   //     AccessToken token = AccessToken.getCurrentAccessToken();

    }


    private void myNewGraphReq(String friendlistId) {
        final String graphPath = "/"+friendlistId+"/members/";
        AccessToken token = AccessToken.getCurrentAccessToken();
        GraphRequest request = new GraphRequest(token, graphPath, null, HttpMethod.GET, new GraphRequest.Callback() {
            @Override
            public void onCompleted(GraphResponse graphResponse) {
                JSONObject object = graphResponse.getJSONObject();
                try {
                    JSONArray arrayOfUsersInFriendList= object.getJSONArray("data");
                    /* Do something with the user list */
                    /* ex: get first user in list, "name" */
                    JSONObject user = arrayOfUsersInFriendList.getJSONObject(0);
                    String usersName = user.getString("name");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle param = new Bundle();
        param.putString("fields", "name");
        request.setParameters(param);
        request.executeAsync();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //  FirebaseUser currentUser = mAuth.getCurrentUser();

        try {
             FirebaseAuth.getInstance().signOut();
             LoginManager.getInstance().logOut();

           // token = FirebaseAuth.getInstance().getAccessToken(true);
         //   LoginManager.getInstance().
            mAuth.signOut();
        }catch (Exception ex){
            ex.printStackTrace();
        }

        // updateUI(currentUser);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Pass the activity result back to the Facebook SDK
        callbackManager.onActivityResult(requestCode, resultCode, data);


    }

}
