package cse403.blast.Model;

import android.support.test.runner.AndroidJUnit4;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.HashSet;

/**
 * Created by Kristi on 2/19/2016.
 */
@RunWith(AndroidJUnit4.class)
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
