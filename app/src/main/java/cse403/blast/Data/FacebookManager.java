package cse403.blast.Data;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.facebook.AccessToken;
import com.facebook.AccessTokenSource;
import com.facebook.Profile;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import cse403.blast.R;

import cse403.blast.Model.User;

/**
 * FacebookManager is a Singleton object that stores basic session data
 * To use it in an Activity, call FacebookManager.getInstance()
 *
 * Created by kevin on 2/18/16.
 */
public class FacebookManager {
    private static FacebookManager fbInstance = null;

    //Shared Preference fields
    private final String PREFS = "blastPrefs";
    private final String TOKEN_KEY = "token";
    private final String EXP_KEY = "expire";
    private final String ID_KEY = "id";
    private final String GRAPH_URL = "https://graph.facebook.com/";

    private AccessToken token;
    private Date expiration;
    private String userID;
    private User currentUser;

    /**
     * Basic constructor
     */
    private FacebookManager() {
        //clearToken();
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
     * Sets the current logged in user's Facebook ID
     */
    public void setCurrentUserID(String id) {
        userID = id;
    }

    /**
     * Sets the current logged in user
     */
    public void setCurrentUser(User user) {
        currentUser = user;
    }

    /**
     * Gets the current logged in user
     * @return The current User object
     */
    public User getCurrentUser() {
        return currentUser;
    }

    /**
     * Gets the current logged in user's Facebook ID
     * @return the current user's Facebook ID
     */
    public String getUserID() {
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

    /**
     * Retrieves the full name of the currently logged in user.
     *
     * @return String user's name
     */
    public String getUserName() {
        return Profile.getCurrentProfile().getName();
    }

    /**
     * Pulls the JSON
     * @param fbID
     * @return
     */
    private JSONObject getUser(String fbID) {
        InputStream is = null;
        try {
            is = new URL(GRAPH_URL + fbID + "?access_token=" + token.toString()).openStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(is, Charset.forName("UTF-8")));
            StringBuilder sb  = new StringBuilder();
            int cp;
            while ((cp = br.read()) != -1) {
                sb.append((char) cp);
            }
            return new JSONObject(sb.toString());
        } catch (MalformedURLException e) {
            Log.i("FB Manager", fbID + ": " + e.toString());
        } catch (IOException e) {
            Log.i("FB Manager", fbID + ": " + e.toString());
        } catch (JSONException e) {
            Log.i("FB Manager", fbID + ": " + e.toString());
        }
        return null;
    }

    /**
     * Pulls the profile picture of a given user
     *
     * @param fbID String id of the facebook user
     * @return Bitmap of the user's profile picture
     * @throws IOException if unable to connect and retrieve image
     */
    public Bitmap getFacebookProfilePicture(String fbID) throws IOException {
        URL imageURL = null;
        try {
            imageURL = new URL(GRAPH_URL +fbID + "/picture?type=large&access_token=" + token.toString());
        } catch (MalformedURLException e) {
            Log.i("FB Manager", fbID  + ": malformed url");
            return null;
        }
        Bitmap bitmap = null;
        if (imageURL != null)
            bitmap = BitmapFactory.decodeStream(imageURL.openConnection().getInputStream());
        return bitmap;
    }

    /**
     * Gets the full name of a user given their Facebook id
     *
     * @param fbID String facebook id
     * @return String full name
     */
    public String getName(String fbID) {
        String name = null;
        try {
            name = getUser(fbID).getString("name");
        } catch (JSONException e) {
            Log.i("FB Manager", fbID + ": " + e.toString());
        }
        return name;
    }


}
