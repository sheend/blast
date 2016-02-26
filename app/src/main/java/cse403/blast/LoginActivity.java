package cse403.blast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphRequestAsyncTask;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import cse403.blast.Data.Constants;
import cse403.blast.Data.FacebookManager;
import cse403.blast.Model.User;


/**
 * A login screen that redirects user to our third party authentication service (Facebook)
 */
public class LoginActivity extends FragmentActivity {

    public final String TAG = "LoginActivity";
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextView message;
    private User userInfo;
    private SharedPreferences preferenceSettings;
    private SharedPreferences.Editor preferenceEditor;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        message = (TextView) findViewById(R.id.message);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "onSuccess");
                AccessToken token = loginResult.getAccessToken();
                FacebookManager fbManager = FacebookManager.getInstance();

                if(Profile.getCurrentProfile() == null) {
                    Log.i(TAG, "profile is null");
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            Log.v("facebook - profile", profile2.getFirstName());
                            mProfileTracker.stopTracking();
                        }
                    };
                    mProfileTracker.startTracking();
                }

                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                fbManager.setToken(token);
                AccessToken.setCurrentAccessToken(token);
                fbManager.saveSession(getApplicationContext());
                startActivity(i);
                finish();
            }


            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel");
                message.setText("Facebook login cancelled");
            }

            @Override
            public void onError(FacebookException e) {
                Log.i(TAG, "onError");
                message.setText("Facebook login failed: " + e.getMessage());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }

    /*private void addLoginUser(final LoginResult loginResult) {
        final AccessToken accessToken = loginResult.getAccessToken();
        GraphRequestAsyncTask request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject user, GraphResponse graphResponse) {
                final String fid = user.optString("id");
                final String name = user.optString("name");
                Log.i("addedNewUserTAG", "we got this user id: " + fid + " " + name);

                final Firebase ref = new Firebase(Constants.FIREBASE_URL).child("users").child(fid);

                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {
                            userInfo = new User(fid, name);

                            // Add user to DB
                            ref.setValue(userInfo);

                            Log.i("addedNewUserTAG", "we added a new user");
                        } else {
                            userInfo = dataSnapshot.getValue(User.class);
                            Log.i("noUserAddedTAG", "user already exists in db");
                        }

                        // Store the current userID in SharedPreferences
                        preferenceSettings = getSharedPreferences(Constants.SHARED_KEY, Context.MODE_PRIVATE);
                        preferenceEditor = preferenceSettings.edit();
                        preferenceEditor.putString("userid", fid);
                        preferenceEditor.putString("name", name);

                        // Store the current User object in SharedPreferences
                        Gson gson = new Gson();
                        String json = gson.toJson(userInfo);
                        Log.i("LoginActivity", "JSON: " + json);
                        preferenceEditor.putString("MyUser", json);

                        preferenceEditor.commit();
                    }

                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Log.d(TAG, "error: " + firebaseError.getMessage());
                    }
                });
            }
        }).executeAsync();*/

    private void addLoginUser() {
        FacebookManager fbManager=  FacebookManager.getInstance();
        final String name = fbManager.getUserName();
        final String fid = fbManager.getUserID();

        // Store the current userID in SharedPreferences
        preferenceSettings = getSharedPreferences(Constants.SHARED_KEY, Context.MODE_PRIVATE);
        preferenceEditor = preferenceSettings.edit();
        preferenceEditor.putString("userid", fid);
        preferenceEditor.putString("name", name);

        // Store the current User object in SharedPreferences
        Gson gson = new Gson();
        String json = gson.toJson(userInfo);
        Log.i("LoginActivity", "JSON: " + json);
        preferenceEditor.putString("MyUser", json);

        preferenceEditor.commit();

        Log.i("addedNewUserTAG", "we got this user id: " + fid + " " + name);

        final Firebase ref = new Firebase(Constants.FIREBASE_URL).child("users").child(fid);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if (dataSnapshot.getValue() == null) {
                    userInfo = new User(fid, name);

                    // Add user to DB
                    ref.setValue(userInfo);

                    Log.i("addedNewUserTAG", "we added a new user");
                } else {
                    userInfo = dataSnapshot.getValue(User.class);
                    Log.i("noUserAddedTAG", "user already exists in db");
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Log.d(TAG, "error: " + firebaseError.getMessage());
            }
        });
    }

}
