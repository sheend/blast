package cse403.blast.Model;

import android.support.test.runner.AndroidJUnit4;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kristi on 2/19/2016.
 */
@RunWith(AndroidJUnit4.class)
public class UserTest {

    private static final int TIMEOUT = 2000; // 2000ms
    //test data
    String testID = "FbId";
    String testName = "FbId";

    @Test(timeout = TIMEOUT)
    public void testParamConstructor(){
        User testUser = new User(testID, testName);
        assertNotNull(testUser);
    }

    @Test(timeout = TIMEOUT)
    public void testIDGetter(){
        User testUser = new User(testID, testName);
        assertEquals(testID, testUser.getFacebookID());
    }

    @Test(timeout = TIMEOUT)
    public void testEventsCreatedGetter(){
        User testUser = new User(testID, testName);
        assertNotNull(testUser.getEventsCreated());
    }

    @Test(timeout = TIMEOUT)
    public void testEventsAttendingGetter(){
        User testUser = new User(testID, testName);
        assertNotNull(testUser.getEventsAttending());
    }
}
