package cse403.blast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import org.junit.After;
import org.junit.Test;
import java.util.Date;
import cse403.blast.Data.Constants;
import cse403.blast.Model.Event;
import cse403.blast.Model.User;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

/**
 * Created by graceqiu on 3/3/16.
 *
 * Tests that the database works for the interactions done in CreateEventActivity.
 * By testing the UI of CreateEventActivity and the database actions that are tied
 * to the UI, we ensure that CreateEventActivity behaves as correctly despite not
 * testing UI in the same tests for database.
 */
public class DBCreateActivityTest {
    private static final int TIMEOUT = 2000; // 2000ms
    private Event eventToTest = new Event();
    private Firebase ref = new Firebase(Constants.FIREBASE_URL); // reference to root node

    @After
    public void cleanUpDB() {
        Firebase eventRef = ref.child("events").child(eventToTest.getId());
        eventRef.removeValue();
    }

    @Test(timeout = TIMEOUT)
    public void testCreateEvent(){
        //************* Setup *************
        User testUser = new User("createTestId", "create name");
        Event testEvent = new Event(testUser.getFacebookID(), "testTitle", "testDesc", "testLoc", 100, new Date(0));
        testUser.createEvent(testEvent);
        // Generate unique ID for event, creates reference to events node in JSON, then appends event
        Firebase eventRef = ref.child("events"); // will always add to the db
        Firebase newEventRef = eventRef.push(); // will always generate the unique id

        //************* Add event to DB *************
        newEventRef.setValue(testEvent);

        //************* Grab event from DB *************
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
        //*********** test that each field matches expected value ************
        assertEquals(eventToTest.getTitle(), "testTitle");
        assertEquals(eventToTest.getOwner(), testUser);
        assertEquals(eventToTest.getDesc(), "testDesc");
        assertEquals(eventToTest.getLocation(), "testLoc");
        assertEquals(eventToTest.getLimit(), 100);
        assertEquals(eventToTest.getEventTime(), new Date(0));
    }

    @Test(timeout = TIMEOUT)
    public void testModifyEvent(){
        //************* Setup *************
        User testUser = new User("createTestId", "create name");
        Event testEvent = new Event(testUser.getFacebookID(), "testTitle", "testDesc", "testLoc", 100, new Date(0));
        testUser.createEvent(testEvent);
        // Generate unique ID for event, creates reference to events node in JSON, then appends event
        Firebase eventRef = ref.child("events"); // will always add to the db
        Firebase newEventRef = eventRef.push(); // will always generate the unique id

        //************* Add event to DB *************
        newEventRef.setValue(testEvent);

        //***** Modify event from DB ******************
        // Grab ID for recently added event
        String eventId = newEventRef.getKey();
        // Get the reference to the event node in Firebase
        Firebase eventsRef = ref.child("events").child(eventId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    eventToTest = snapshot.getValue(Event.class);
                } catch (Exception e) {
                    assertNotNull(eventToTest);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        // modify fields and resend to DB
        eventToTest.changeLimit(10);
        eventToTest.changeDesc("new description");
        eventToTest.changeTime(new Date(500));
        eventsRef.setValue(eventToTest);

        //*********** test that each field matches expected value ************
        assertEquals(eventToTest.getTitle(), "testTitle");
        assertEquals(eventToTest.getOwner(), testUser);
        assertEquals(eventToTest.getDesc(), "new description");
        assertEquals(eventToTest.getLocation(), "testLoc");
        assertEquals(eventToTest.getLimit(), 10);
        assertEquals(eventToTest.getEventTime(), new Date(500));
    }

    @Test(timeout = TIMEOUT)
    public void testDeleteEvent(){
        //************* Setup *************
        User testUser = new User("createTestId", "create name");
        Event testEvent = new Event(testUser.getFacebookID(), "testTitle", "testDesc", "testLoc", 100, new Date(0));
        testUser.createEvent(testEvent);
        // Generate unique ID for event, creates reference to events node in JSON, then appends event
        Firebase eventRef = ref.child("events"); // will always add to the db
        Firebase newEventRef = eventRef.push(); // will always generate the unique id

        //************* Add event to DB *************
        newEventRef.setValue(testEvent);

        //******* Delete event **************
        // Grab ID for recently added event
        String eventId = newEventRef.getKey();
        // Get the reference to the event node in Firebase
        Firebase eventsRef = ref.child("events").child(eventId);
        eventsRef.removeValue();

        //********* Test that it is deleted **************
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    eventToTest = snapshot.getValue(Event.class);
                } catch (Exception e) {
                    assertNull(eventToTest);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }
}