package cse403.blast.Data;

import com.firebase.client.Firebase;

/**
 * Created by Melissa on 2/8/2016.
 *
 * Includes one-time initialization of Firebase related code
 *
 */
public class BlastApplication extends android.app.Application {

    @Override
    public void onCreate() {
        super.onCreate();
        Firebase.setAndroidContext(this);
    }


}
