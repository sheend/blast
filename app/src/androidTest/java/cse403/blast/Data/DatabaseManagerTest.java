package cse403.blast.Data;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Calendar;
import java.util.Date;
import cse403.blast.Data.Constants;
import cse403.blast.Model.*;
import com.firebase.client.Firebase;

/**
 * Created by Kristi on 2/18/2016.
 */

// Tests firebase backend: add, remove, fetch functionality
public class DatabaseManagerTest {
    private static final int TIMEOUT = 2000; // 2000ms
    Date testDate = new Date();

    @Test(timeout = TIMEOUT)
    public void testDbAddAndSucessfulFetchEvent(){
        //*************setup*************
        // Get the reference to the root node in Firebase
        Firebase ref = new Firebase(Constants.FIREBASE_URL);
        User testUser = new User("1234");
        // Create event object using test data
        Event testEvent = new Event(testUser, "testTitle", "testDesc", "testLoc", 4321, testDate);
        // Generate unique ID for event, creates reference to events node in JSON, then appends event
        Firebase eventRef = ref.child("events");
        Firebase newEventRef = eventRef.push();

        //*************Add event to DB*************
        newEventRef.setValue(testEvent);

        //*************testing*************
        // Grab ID for recently added event
        String eventId = newEventRef.getKey();
        //find what it returns, fail or not based on val

        // Get the reference to the event node in Firebase
        Firebase eventsRef = new Firebase(Constants.FIREBASE_URL).child("events").child(eventId);

        ////////////////////////////////////////////

        // To do: search for way to get Event out of database with given eventId (or by using the eventsRef reference to the eventId node)

        // We need to parse the rest of the eventsRef to make sure the data matches expected
        //Event eventToTest = eventsRef.getValue(Event.class);

        ///////////////////////////////////////////
        /*
        // test that each field matches expected value
        assertEquals(eventToTest.getTitle(), "testTitle");
        assertEquals(eventToTest.getOwner(), testUser);
        assertEquals(eventToTest.getDesc(), "testDesc");
        assertEquals(eventToTest.getLocation(), "testLoc");
        assertEquals(eventToTest.getLimit(), 4321);
        assertEquals(eventToTest.getEventTime(), testDate);
        //assertEquals(eventToTest.getCreationTime(), );
        //assertEquals(eventToTest.getAttendees(), ):
        */
    }


    /** Test remove event from database ================================================= **/

    @Test(timeout = TIMEOUT)
    public void testDbRemoveAndFailedFetchEvent(){

    }
}
