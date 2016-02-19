package cse403.blast;

import android.content.Intent;
import android.support.v4.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import cse403.blast.Data.FacebookManager;


/**
 * A login screen that redirects user to our third party authentication service (Facebook)
 */
public class LoginActivity extends FragmentActivity {

    public final String TAG = "LoginActivity";
    private LoginButton loginButton;
    private CallbackManager callbackManager;
    private TextView message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
        setContentView(R.layout.activity_login);

        message = (TextView) findViewById(R.id.message);
        loginButton = (LoginButton) findViewById(R.id.login_button);
        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i(TAG, "onSuccess");
                AccessToken token = loginResult.getAccessToken();
                Intent i = new Intent(LoginActivity.this, MainActivity.class);
                Bundle b = new Bundle();
                b.putParcelable("fb", token);
                i.putExtras(b);
                startActivity(i);
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
}

