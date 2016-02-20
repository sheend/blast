package cse403.blast.Model;

import static org.junit.Assert.*;
import org.junit.Test;
import java.io.Serializable;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Kristi on 2/19/2016.
 */
public class UserTest {

    private static final int TIMEOUT = 2000; // 2000ms
    //test data
    String testID = "FbId";

    @Test(timeout = TIMEOUT)
    public void testParamConstructor(){
        User testUser = new User(testID);
        assertNotNull(testUser);
    }

    @Test(timeout = TIMEOUT)
    public void testIDGetter(){
        User testUser = new User(testID);
        assertEquals(testID, testUser.getFacebookID());
    }

    @Test(timeout = TIMEOUT)
    public void testEventsCreatedGetter(){
        User testUser = new User(testID);
        assertNotNull(testUser.getEventsCreated());
    }

    @Test(timeout = TIMEOUT)
    public void testEventsAttendingGetter(){
        User testUser = new User(testID);
        assertNotNull(testUser.getEventsAttending());
    }
}
