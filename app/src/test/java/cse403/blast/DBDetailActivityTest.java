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
import cse403.blast.Data.Constants;

/**
 * Created by graceqiu on 3/3/16.
 */
public class DBDetailActivityTest {
    private static final int TIMEOUT = 2000; // 2000ms
    private Firebase ref = new Firebase(Constants.FIREBASE_URL);
    private Event eventToTest = new Event();

    @Test(timeout = TIMEOUT)
    public void testGetCorrectEvent(){
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
    public void testUserJoins(){
        //************* Setup *************
        User testUser1 = new User("createTestId", "createName");
        User testUser2 = new User("userTestId1", "userName1");
        Event testEvent = new Event(testUser1.getFacebookID(), "testTitle", "testDesc", "testLoc", 100, new Date(0));
        testUser1.createEvent(testEvent);
        // Generate unique ID for event, creates reference to events node in JSON, then appends event
        Firebase eventRef = ref.child("events"); // will always add to the db
        Firebase newEventRef = eventRef.push(); // will always generate the unique id

        //************* Add event to DB *************
        newEventRef.setValue(testEvent);

        int attendeeCount = 0;
        testUser2.attendEvent(testEvent);

        //************* Pull event from DB *************
        // Grab ID for recently added event
        String eventId = newEventRef.getKey();
        // Get the reference to the event node in Firebase
        Firebase eventsRef = ref.child("events").child(eventId).child("attendees");
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

        assertEquals(attendeeCount+1, eventToTest.getAttendees().size());
    }

    @Test(timeout = TIMEOUT)
    public void testUserLeaves(){
        //************* Setup *************
        User testUser1 = new User("createTestId", "createName");
        User testUser2 = new User("userTestId1", "userName1");
        Event testEvent = new Event(testUser1.getFacebookID(), "testTitle", "testDesc", "testLoc", 100, new Date(0));
        testUser1.createEvent(testEvent);
        // Generate unique ID for event, creates reference to events node in JSON, then appends event
        Firebase eventRef = ref.child("events"); // will always add to the db
        Firebase newEventRef = eventRef.push(); // will always generate the unique id

        //************* Add event to DB *************
        newEventRef.setValue(testEvent);

        testUser2.attendEvent(testEvent);

        //******* pull event from db **************
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
                    assertNull(eventToTest);
                }
            }
            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

        int attendeeCount = eventToTest.getAttendees().size();

        testUser2.leaveEvent(testEvent);
        //******* pull event from db and test removed attendee **************
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

        assertEquals(attendeeCount -  1, eventToTest.getAttendees().size());
    }
}
