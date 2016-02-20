package cse403.blast.Data;

import com.facebook.AccessToken;

import java.io.Serializable;
import java.util.Date;

/**
 * FacebookManager is a Singleton object that stores basic session data
 * To use it in an Activity, call FacebookManager.getInstance()
 *
 * Created by kevin on 2/18/16.
 */
public class FacebookManager implements Serializable {
    private static FacebookManager fbInstance = null;

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
}
