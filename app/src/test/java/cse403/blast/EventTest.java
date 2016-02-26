package cse403.blast;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Date;

import cse403.blast.Model.Event;
import cse403.blast.Model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
* Created by graceqiu on 2/26/16.
*/
public class EventTest {

    private static final int TIMEOUT = 2000; // 2000ms
    //test data
    User testUser = new User("testid", "testid");
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
    public void testOwnerGetter(){
        Event testEvent = new Event("testUser", testTitle, testDesc,testLocation, testLimit, testTime);
        assertEquals(testUser, testEvent.getOwner());
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
}
