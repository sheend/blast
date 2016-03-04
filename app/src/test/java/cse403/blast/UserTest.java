package cse403.blast;

import org.junit.Test;

import java.util.Date;

import cse403.blast.Model.Event;
import cse403.blast.Model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
 * Created by graceqiu on 2/26/16.
 *
 * Tests the User class that holds information about users on this app.
 * User behavior that needs to be tested include...
 * (1) attending:
 *      - users cannot attend their own event (they are automatically added
 *      when they create the event)
 *      - users cannot attend an event they are already attending
 * (2) leaving:
 *      - users cannot leave their own event
 *      - users cannot leave an event they were not already attending
 * (3) creating:
 *      - user should be an attendee of created event
 * (4) deleting events
 *      - users cannot cancel an event they never created
 *      - cancelled event that user created should disappear from user's created list
 */
public class UserTest {

    private static final int TIMEOUT = 2000; // 2000ms
    //test data
    String testID = "FbId";
    String testName = "Name";

    @Test(timeout = TIMEOUT)
    public void testEmptyConstructor(){
        User testUser = new User();
        assertNotNull(testUser);
        assertEquals("", testUser.getFacebookID());
        assertEquals("", testUser.getName());
        assertNotNull(testUser.getEventsCreated());
        assertNotNull(testUser.getEventsAttending());
    }

    @Test(timeout = TIMEOUT)
    public void testParamConstructor(){
        User testUser = new User(testID, testName);
        assertNotNull(testUser);
        assertEquals(testID, testUser.getFacebookID());
        assertEquals(testName, testUser.getName());
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

    @Test(timeout = TIMEOUT)
    public void testUsersHashCodesDiff(){
        User testUser = new User(testID, testName);
        User testUser2 = new User("test", "test");
        assertNotEquals(testUser.hashCode(), testUser2.hashCode());
    }

    @Test(timeout = TIMEOUT)
    public void testUserHashCodeEqual(){
        User testUser = new User(testID, testName);
        assertTrue(testUser.hashCode() == testUser.hashCode());
    }

    /* Event create */

    @Test(timeout = TIMEOUT)
    public void testEventCreateNotAddEventToAttendingListOrAddOwnerAsAttendeeToEvent(){
        User testUser = new User(testID, testName);
        Event e = new Event(testID, "title", "desc", "l", 1, new Date(1));
        testUser.createEvent(e);
        //assertTrue(!testUser.getEventsAttending().contains(e.getId()));
        assertTrue(!e.getAttendees().contains(testUser.getFacebookID()));
    }

    @Test(timeout = TIMEOUT)
    public void testEventCreatedAppearsInCreated(){
        User testUser = new User(testID, testName);
        Event e = new Event(testID, "title", "d", "loc", 1, new Date(1));
        testUser.createEvent(e);
        assertTrue(testUser.getEventsCreated().contains(e.getId()));
    }

    /* Event cancel */

    @Test(timeout = TIMEOUT)
    public void testEventOwnedCanCancel(){
        User testUser = new User(testID, testName);
        Event e = new Event("e", "t", "d", "l", 1, new Date(1));
        testUser.createEvent(e);
        testUser.cancelEvent(e);
        assertTrue(!testUser.getEventsCreated().contains(e));
    }

    @Test(timeout = TIMEOUT)
    public void testEventNotOwnedCannotCancel(){
        User testUser = new User(testID, testName);
        Event e = new Event("event", "title", "desc", "loc", 1, new Date(1));
        assertTrue(!testUser.cancelEvent(e));
    }

    @Test(timeout = TIMEOUT)
    public void testEventCancelledDisappearsFromCreated(){
        User testUser = new User(testID, testName);
        Event e = new Event("event", "title", "desc", "loc", 1, new Date(1));
        testUser.createEvent(e);
        testUser.cancelEvent(e);
        assertTrue(!testUser.getEventsCreated().contains(e));
    }

    /* Event attend */

    @Test(timeout = TIMEOUT)
    public void testAttendEventNotCreated(){
        User testUser = new User(testID, testName);
        Event e = new Event("event", "title", "desc", "loc", 1, new Date(1));
        testUser.attendEvent(e);
        assertTrue(testUser.getEventsAttending().contains(e.getId()));
    }

    @Test(timeout = TIMEOUT)
    public void testAttendEventCreated(){
        User testUser = new User(testID, testName);
        Event e = new Event("event", "title", "desc", "loc", 1, new Date(1));
        assertTrue(!testUser.attendEvent(e));
        assertTrue(!testUser.getEventsAttending().contains(e));
    }

    /* Event leave */

    @Test(timeout = TIMEOUT)
    public void testLeaveEventJoined(){
        User testUser = new User(testID, testName);
        Event e = new Event("event", "title", "desc", "loc", 1, new Date(1));
        testUser.attendEvent(e);
        testUser.leaveEvent(e);
        assertTrue(!testUser.getEventsAttending().contains(e));
    }

    @Test(timeout = TIMEOUT)
    public void testLeaveEventNotJoined(){
        User testUser = new User(testID, testName);
        Event e = new Event("event", "title", "desc", "loc", 1, new Date(1));
        assertTrue(!testUser.leaveEvent(e));
    }

    @Test(timeout = TIMEOUT)
    public void testLeaveEventOwned(){
        User testUser = new User(testID, testName);
        Event e = new Event("event", "title", "desc", "loc", 1, new Date(1));
        testUser.createEvent(e);
        assertTrue(!testUser.leaveEvent(e));
    }
}
