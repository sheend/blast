package cse403.blast;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.text.SimpleDateFormat;
import java.util.Date;

import cse403.blast.Model.Event;
import cse403.blast.Model.User;
import cse403.blast.Support.DateDifference;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

/**
* Created by graceqiu on 2/26/16.
 *
 * Tests the Event class. Events should be comparable,
 * can add/remove attendees, and have certain fields be modified.
*/
public class EventTest {

    private static final int TIMEOUT = 2000; // 2000ms
    //test data
    User testUser = new User("testUser", "testUser");
    String testTitle = "title";
    String testDesc = "desc";
    String testLocation = "loc";
    int testLimit = 5;
    Date testTime = new Date();

    @Test(timeout = TIMEOUT)
    public void testEmptyConstructor(){
        Event testEvent = new Event();
        assertNotNull(testEvent);
    }

    @Test(timeout = TIMEOUT)
    public void testParamConstructor(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        assertNotNull(testEvent);
    }

    @Test(timeout = TIMEOUT)
    public void testToString(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        assertEquals(testTitle, testEvent.toString());
    }

    @Test(timeout = TIMEOUT)
    public void testCompareTo(){
        Event testEvent1 = new Event("testUser1", testTitle, testDesc,testLocation, testLimit, testTime);
        Event testEvent2 = new Event("testUser2", testTitle, testDesc,testLocation, testLimit, testTime);
        assertEquals(0, testEvent1.compareTo(testEvent2));
    }

    /* Getters */

    @Test(timeout = TIMEOUT)
    public void testOwnerGetter(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        assertEquals(testUser.getName(), testEvent.getOwner());
    }

    @Test(timeout = TIMEOUT)
    public void testTitleGetter(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        assertEquals(testTitle, testEvent.getTitle());
    }

    @Test(timeout = TIMEOUT)
    public void testDescGetter(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        assertEquals(testDesc, testEvent.getDesc());
    }

    @Test(timeout = TIMEOUT)
    public void testLocationGetter(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        assertEquals(testLocation, testEvent.getLocation());
    }

    @Test(timeout = TIMEOUT)
    public void testLimitGetter(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        assertEquals(testLimit, testEvent.getLimit());
    }

    @Test(timeout = TIMEOUT)
    public void testTimeGetter(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        assertEquals(testTime, testEvent.getEventTime());
    }

    @Test(timeout = TIMEOUT)
    public void testRetrieveEventTimeString(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        SimpleDateFormat time = new SimpleDateFormat("KK:mm a");
        SimpleDateFormat day = new SimpleDateFormat("dd MMM");
        assertEquals(time.format(testTime) + " on "+ day.format(testTime), testEvent.retrieveEventTimeString());
    }

    @Test(timeout = TIMEOUT)
    public void testRetrieveTimeDifference() {
        Event testEvent = new Event("testUser", testTitle, testDesc, testLocation, testLimit, testTime);
        assertEquals(DateDifference.getDifferenceString(new Date(), testTime), testEvent.retrieveTimeDifference());
    }

    /* Compare */

    @Test(timeout = TIMEOUT)
    public void testEquals(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        Event testEvent2 = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        assertTrue(testEvent.equals(testEvent2));
    }

    @Test(timeout = TIMEOUT)
    public void testNotEquals(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        Event testEvent2 = new Event("test", "test", testDesc,testLocation, testLimit, testTime);
        assertTrue(!testEvent.equals(testEvent2));
    }

    @Test(timeout = TIMEOUT)
    public void testNotEqualsHashCode(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        Event testEvent2 = new Event("test", testTitle, testDesc,testLocation, testLimit, testTime);
        assertEquals(testEvent.hashCode(), testEvent2.hashCode());
    }

    @Test(timeout = TIMEOUT)
    public void testEqualsHashCode(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        assertEquals(testEvent.hashCode(), testEvent.hashCode());
    }

    /* Attend */

    @Test(timeout = TIMEOUT)
    public void testAttendeesContains(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        User u = new User("test", "test");
        testEvent.addAttendee(u);
        assertTrue(testEvent.getAttendees().contains(u.getFacebookID()));
    }

    @Test(timeout = TIMEOUT)
    public void testAttendeesNotContains(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        User u = new User("test", "test");
        assertTrue(!testEvent.getAttendees().contains(u.getFacebookID()));
    }

    /* Remove */
    @Test(timeout = TIMEOUT)
    public void testRemoveAttendee(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        User u = new User("test", "test");
        testEvent.addAttendee(u);
        testEvent.removeAttendee(u);
        assertTrue(!testEvent.getAttendees().contains(u.getFacebookID()));
    }

    /* Change */

    @Test(timeout = TIMEOUT)
    public void testChangeDesc(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        testEvent.changeDesc("new desc");
        assertEquals("new desc", testEvent.getDesc());
    }

    @Test(timeout = TIMEOUT)
    public void testChangeLimit(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        testEvent.changeLimit(10);
        assertEquals(10, testEvent.getLimit());
    }

    @Test(timeout = TIMEOUT)
    public void testChangeTime(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        testEvent.changeLimit(10);
        assertEquals(10, testEvent.getLimit());
    }

    @Test(timeout = TIMEOUT)
    public void testSetId(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        assertEquals("", testEvent.getId());
        String id = "asdf123";
        testEvent.setId(id);
        assertEquals("asdf123", testEvent.getId());
    }

}
