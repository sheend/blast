package cse403.blast.Data;

import static org.junit.Assert.*;
import org.junit.Test;
import java.util.Calendar;
import java.util.Date;
import cse403.blast.Data.Constants;
import cse403.blast.Model.*;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

/**
 * Created by Kristi on 2/18/2016.
 */

// Tests firebase backend: add, remove, fetch functionality
public class DatabaseManagerTest {
    private static final int TIMEOUT = 2000; // 2000ms
    Date testDate = new Date();
    Event eventToTest = new Event();

    // Get the reference to the root node in Firebase
    Firebase ref = new Firebase(Constants.FIREBASE_URL);

    @Test(timeout = TIMEOUT)
    public void testDbAddAndSuccessfulFetchEvent(){
        //*************setup*************

        User testUser = new User("1234");
        // Create event object using test data
        Event testEvent = new Event(testUser, "testTitle", "testDesc", "testLoc", 4321, testDate);
        // Generate unique ID for event, creates reference to events node in JSON, then appends event
        Firebase eventRef = ref.child("events"); // will always add to the db
        Firebase newEventRef = eventRef.push(); // will always general the unique id

        //*************Add event to DB*************
        newEventRef.setValue(testEvent); // need to check event details

        //*************testing*************
        // Grab ID for recently added event
        String eventId = newEventRef.getKey();

        // Get the reference to the event node in Firebase
        Firebase eventsRef = ref.child("events").child(eventId);

        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try{
                    eventToTest = snapshot.getValue(Event.class);
                } catch (Exception e) {
                    assertNotNull(eventToTest);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        // test that each field matches expected value
        assertEquals(eventToTest.getTitle(), "testTitle");
        assertEquals(eventToTest.getOwner(), testUser);
        assertEquals(eventToTest.getDesc(), "testDesc");
        assertEquals(eventToTest.getLocation(), "testLoc");
        assertEquals(eventToTest.getLimit(), 4321);
        assertEquals(eventToTest.getEventTime(), testDate);
    }


    /** Test remove event from database ================================================= **/

    @Test(timeout = TIMEOUT)
    public void testDbRemoveAndFailedFetchEvent(){
        //not implemented for demo
    }
}
