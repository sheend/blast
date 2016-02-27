package cse403.blast;

import org.junit.Test;

import java.util.Date;

import cse403.blast.Model.Event;
import cse403.blast.Model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by graceqiu on 2/26/16.
 */
public class UserTest {

    private static final int TIMEOUT = 2000; // 2000ms
    //test data
    String testID = "FbId";
    String testName = "Name";
    Event e = new Event("event", "title", "desc", "loc", 1, new Date(1));

    @Test(timeout = TIMEOUT)
    public void testParamConstructor(){
        User testUser = new User(testID, testName);
        assertNotNull(testUser);
        assertNotNull(testUser.getEventsCreated());
        assertNotNull(testUser.getEventsAttending());
    }

    @Test(timeout = TIMEOUT)
    public void testIDGetter(){
        User testUser = new User(testID, testName);
        assertEquals(testID, testUser.getFacebookID());
    }

    @Test(timeout = TIMEOUT)
    public void testNameGetter(){
        User testUser = new User(testID, testName);
        assertEquals(testName, testUser.getName());
    }

    @Test(timeout = TIMEOUT)
    public void testUserEquals(){
        User testUser = new User(testID, testName);
        User testUser2 = new User(testID, testName);
        assertTrue(testUser.equals(testUser2));
    }

    /* Event create */

    @Test(timeout = TIMEOUT)
    public void testEventCreate(){
        User testUser = new User(testID, testName);
        assertTrue(testUser.createEvent(e));
        assertTrue(testUser.getEventsAttending().contains(e));
    }

    /* Event cancel */

    @Test(timeout = TIMEOUT)
    public void testEventOwnedCanCancel(){
        User testUser = new User(testID, testName);
        testUser.createEvent(e);
        assertTrue(testUser.cancelEvent(e));
    }

    @Test(timeout = TIMEOUT)
    public void testEventNotOwnedCannotCancel(){
        User testUser = new User(testID, testName);
        assertTrue(!testUser.cancelEvent(e));
    }

    @Test(timeout = TIMEOUT)
    public void testEventCancelledDisappearsFromCreated(){
        User testUser = new User(testID, testName);
        testUser.createEvent(e);
        testUser.cancelEvent(e);
        assertTrue(!testUser.getEventsCreated().contains(e));
    }

    /* Event attend */

    @Test(timeout = TIMEOUT)
    public void testAttendEventNotCreated(){
        User testUser = new User(testID, testName);
        assertTrue(testUser.attendEvent(e));
        assertTrue(testUser.getEventsAttending().contains(e));
    }

    @Test(timeout = TIMEOUT)
    public void testAttendEventCreated(){
        User testUser = new User(testID, testName);
        assertTrue(!testUser.attendEvent(e));
        assertTrue(!testUser.getEventsAttending().contains(e));
    }

//    /* Event leave */
//
//    @Test(timeout = TIMEOUT)
//    public void testLeaveEventJoined(){
//        User testUser = new User(testID, testName);
//        testUser.attendEvent(e);
//        assertTrue(!testUser.getEventsCreated().contains(e));
//    }
}
