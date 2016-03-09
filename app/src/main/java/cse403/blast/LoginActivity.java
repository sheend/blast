package cse403.blast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

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

    /**
     * Displays the login screen that prompts the user to sign in with Facebook.
     * Redirects the user to the Main landing page of Blast upon successful login.
     * Initializes local copies of the User's FacebookID in the app's SharedPreferences
     * file.
     *
     * @param savedInstanceState The saved state of the current Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Initializes Facebook authentication services
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        // Displays login button and sets callback for button clicks
        message = (TextView) findViewById(R.id.message);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.setReadPermissions("public_profile");
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {

            private ProfileTracker mProfileTracker;

            /**
             * On successful Facebook authentication, sets current access
             * token and launches the Main Activity
             *
             * @param loginResult Object representing the result of a successful login
             */
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "onSuccess");
                final AccessToken token = loginResult.getAccessToken();
                final FacebookManager fbManager = FacebookManager.getInstance();

                if(Profile.getCurrentProfile() == null) {
                    Log.i(TAG, "profile is null");
                    mProfileTracker = new ProfileTracker() {
                        @Override
                        protected void onCurrentProfileChanged(Profile profile, Profile profile2) {
                            // profile2 is the new profile
                            Log.v("facebook - profile", profile2.getFirstName());
                            mProfileTracker.stopTracking();

                            // Redirect to the Main landing page
                            Intent i = new Intent(LoginActivity.this, MainActivity.class);
                            fbManager.setToken(token);
                            AccessToken.setCurrentAccessToken(token);
                            fbManager.saveSession(getApplicationContext());
                            startActivity(i);
                            finish();
                        }
                    };
                    mProfileTracker.startTracking();
                } else {
                    // Redirect to the Main landing page
                    Intent i = new Intent(LoginActivity.this, MainActivity.class);
                    fbManager.setToken(token);
                    AccessToken.setCurrentAccessToken(token);
                    fbManager.saveSession(getApplicationContext());
                    startActivity(i);
                    finish();
                }
            }

            /**
             * Displays an informative message to the user if login request was cancelled.
             */
            @Override
            public void onCancel() {
                Log.i(TAG, "onCancel");
                message.setText("Facebook login cancelled");
            }

            /**
             * Displays an informative message to the user if unable to complete login request.
             *
             * @param e error raised by Facebook
             */
            @Override
            public void onError(FacebookException e) {
                Log.i(TAG, "onError");
                message.setText("Facebook login failed: " + e.getMessage());
            }
        });
    }

    /**
     * Deals with metadata associated with the Activity; uses default process.
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        };
    }

    //prevents glitch of getting back into main activity after log out
    //essentially refreshes
    @Override
    public void onBackPressed() {
        Intent i = new Intent(LoginActivity.this, LoginActivity.class);
        startActivity(i);
        finish();
    }

    /**
     * Adds currently logged in User to the database of Users and update
     * the local copy of the User.
     */
    private void addLoginUser() {
        FacebookManager fbManager=  FacebookManager.getInstance();
        final String name = fbManager.getUserName();
        final String fid = fbManager.getUserID();

        // Store the current userID in SharedPreferences
        preferenceSettings = getApplicationContext().getSharedPreferences("blastPrefs", 0);
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

            /**
             * Queries for the User with given FacebookID. Adds to the database if
             * the User does not already exist. Initializes local copy of User.
             *
             * @param dataSnapshot Object representing the current state of the database
             *                     at the given reference node
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() == null) {
                    userInfo = new User(fid, name);

                    // Add user to DB
                    ref.setValue(userInfo);
                    Log.i("addedNewUserTAG", "We added a new user");
                } else {
                    userInfo = dataSnapshot.getValue(User.class);
                    Log.i("noUserAddedTAG", "User already exists in DB");
                }
            }

            /**
             * Displays an informative message to the user if unable to complete request.
             *
             * @param firebaseError error raised by Firebase
             */
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(LoginActivity.this, "Unable to connect.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "error: " + firebaseError.getMessage());
            }
        });
    }
}
