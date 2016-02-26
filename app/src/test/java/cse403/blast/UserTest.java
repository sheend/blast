package cse403.blast;

import org.junit.Test;

import cse403.blast.Model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * Created by graceqiu on 2/26/16.
 */
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
