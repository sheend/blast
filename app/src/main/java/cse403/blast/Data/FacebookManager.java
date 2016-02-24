package cse403.blast.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cse403.blast.R;

/**
 * FacebookManager is a Singleton object that stores basic session data
 * To use it in an Activity, call FacebookManager.getInstance()
 *
 * Created by kevin on 2/18/16.
 */
public class FacebookManager implements Serializable {
    private static FacebookManager fbInstance = null;

    //Shared Preference fields
    private final String PREFS = "blastPrefs";
    private final String TOKEN_KEY = "token";
    private final String EXP_KEY = "expire";
    private final String ID_KEY = "id";

    private AccessToken token;
    private Date expiration;
    private String userID;

    /**
     * Basic constructor
     */
    private FacebookManager() {
        clearToken();
    }

    /**
     * Provides access to the FacebookManager
     *
     * @return FacebookManager instance
     */
    public static FacebookManager getInstance() {
        if (fbInstance == null) {
            fbInstance = new FacebookManager();
        }
        return fbInstance;
    }
    /**
     * Sets the access token containing user info for the current session
     */
    public void setToken(AccessToken t) {
        token = t;
        userID = t.getUserId();
        expiration = t.getExpires();
    }

    /**
     * TODO: Actually validate session
     * Checks the current associated access token to validate the session
     * @return true if the current user info is correct and the session is valid, else false
     */
    public boolean isValidSession() {
        return token != null;
        /*if (token != null) {
            return !token.isExpired();
        }
        return false;*/
    }

    /**
     * Contacts Facebook for changes to the current access token (if any)
     * @return true if token exists and was successfully updated, else false
     */
    public boolean refreshToken() {
        if (token != null) {
            token.refreshCurrentAccessTokenAsync();
            return true;
        }
        return false;
    }

    /**
     * Removes the current associated access token
     */
    public void clearToken() {
        token = null;
        expiration = null;
        userID = null;
    }

    /**
     * Gets the current logged in user's Facebook ID
     * @return String representing id of the Facebook user, else null if there is none
     */
    public String getUser() {
        return userID;
    }

    /**
     * @param context Context of the application
     * Checks local device storage for a resume-able session.
     * If a session is found, re-initializes AccessToken and fields,
     * else clears any old data
     */
    public void getSession(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS, 0);
        String accessString = settings.getString(TOKEN_KEY, null);
        String id = settings.getString(ID_KEY, null);

        //import expxiration date
        String expires = settings.getString(EXP_KEY, "invalid_date");
        Log.i("FB MANAGER", expires);
        //parse expiration date (or try to at least)
        Date expiration =null;
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        try {
            expiration = dateFormat.parse(expires);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Date current = new Date();
        dateFormat.format(current);

        if (accessString == null || id == null || expiration == null || current == null) {
            clearToken();
        } else {
            setToken(new AccessToken(accessString, context.getString(R.string.app_id), id,
                    null, null, AccessTokenSource.FACEBOOK_APPLICATION_NATIVE,
                    expiration, current));
        }
    }

    /**
     * @param context Context of the application
     * Clears shared preferences of any previous session information
     */
    public void clearSession(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(TOKEN_KEY, null);
        editor.putString(ID_KEY, null);
        editor.putString(EXP_KEY, null);

        editor.commit();
    }

    /**
     * @param context Context of the application
     * Saves current session info to shared preferences
     */
    public void saveSession(Context context) {
        SharedPreferences settings = context.getSharedPreferences(PREFS, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(TOKEN_KEY, token.toString());
        editor.putString(ID_KEY, userID);
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        editor.putString(EXP_KEY, dateFormat.format(expiration));

        editor.commit();
    }
}
