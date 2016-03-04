package cse403.blast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.junit.After;
import org.junit.Test;
import java.util.Date;
import cse403.blast.Data.Constants;
import cse403.blast.Model.Event;
import cse403.blast.Model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by graceqiu on 3/3/16.
 */
public class DBMainActivityTest {
    private static final int TIMEOUT = 2000; // 2000ms
    private Firebase ref = new Firebase(Constants.FIREBASE_URL);

    @Test(timeout = TIMEOUT)
    public void testGetCorrectUser(){
        //************* Setup *************
        User testUser = new User("createTestId", "createName");
        // Generate unique ID for user, creates reference to users node in JSON, then appends user
        Firebase userRef = ref.child("users"); // will always add to the db
        Firebase newUserRef = userRef.push(); // will always generate the unique id

        //************* Add user to DB *************
        newUserRef.setValue(testUser);

        //******* Get user and check fields **************
        // Grab ID for recently added user
        String usertId = newUserRef.getKey();
        // Get the reference to the users node in Firebase
        Firebase usersRef = ref.child("users").child(usertId);

        assertEquals(testUser.getFacebookID(), "createTestId");
        assertEquals(testUser.getName(), "createName");
        assertNotNull(testUser.getEventsCreated());
        assertNotNull(testUser.getEventsAttending());
    }

    @Test(timeout = TIMEOUT)
    public void testGetFuture24HEvents(){
        //cannot test without the count after sanatizing events on front end
    }
}
