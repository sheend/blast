package cse403.blast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
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
 * Tests that the app can retrieve the correct user (which is needed for the drawer
 * to work, which involves testing creation and deletion of users,) as well as
 * testing that the retrieved user has the correct attending and created fields.
 * By testing the UI and database separately, we can ensure that both ends
 * meet the specifications and don't need to explicitly test UI and database
 * in one test.
 */
public class DBDrawerTest {
    private static final int TIMEOUT = 2000; // 2000ms
    private User userToTest = new User();
    private Firebase ref = new Firebase(Constants.FIREBASE_URL); // reference to root node

    @Test(timeout = TIMEOUT)
    public void testCreateUser(){
        //************* Setup *************
        User testUser = new User("createTestId", "create name");
        // Generate unique ID for event, creates reference to events node in JSON, then appends event
        Firebase userRef = ref.child("users"); // will always add to the db
        Firebase newUserRef = userRef.push(); // will always generate the unique id

        //************* Add user to DB *************
        newUserRef.setValue(testUser);

        //************* Grab user from DB *************
        // Grab ID for recently added user
        String userId = newUserRef.getKey();
        // Get the reference to the user node in Firebase
        Firebase usersRef = ref.child("users").child(userId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    userToTest = snapshot.getValue(User.class);
                } catch (Exception e) {
                    assertNotNull(userToTest);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        //*********** test that each field matches expected value ************
        assertEquals(userToTest.getFacebookID(), "createTestId");
        assertEquals(userToTest.getName(), "create name");

        //******** clean up DB ***************
        userRef.child(userToTest.getFacebookID()).removeValue();
    }

    @Test(timeout = TIMEOUT)
    public void testDeleteUser(){
        //************* Setup *************
        User testUser = new User("createTestId", "create name");
        // Generate unique ID for event, creates reference to events node in JSON, then appends event
        Firebase userRef = ref.child("users"); // will always add to the db
        Firebase newUserRef = userRef.push(); // will always generate the unique id

        //************* Add user to DB *************
        newUserRef.setValue(testUser);

        //************* Remove user from DB *************
        // Grab ID for recently added user
        String userId = newUserRef.getKey();
        newUserRef.removeValue();

        //********* Test that it is deleted **************
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    userToTest = snapshot.getValue(User.class);
                } catch (Exception e) {
                    assertNull(userToTest);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
    }

    @Test(timeout = TIMEOUT)
    public void testUserAttendingList(){
        //************* Setup *************
        User testOwner = new User("createTestId", "create name");
        User testAttendee = new User("attendeeId", "attendee name");
        Event testEvent1 = new Event(testOwner.getFacebookID(), "testTitle1", "testDesc", "testLoc", 100, new Date(0));
        Event testEvent2 = new Event(testOwner.getFacebookID(), "testTitle2", "testDesc", "testLoc", 100, new Date(0));
        testAttendee.attendEvent(testEvent1);
        testAttendee.attendEvent(testEvent2);

        // Generate unique ID for user and for events
        Firebase userRef = ref.child("users"); // will always add to the db
        Firebase newUserRef1 = userRef.push(); // will always generate the unique id
        Firebase newUserRef2 = userRef.push();
        Firebase eventRef = ref.child("events");
        Firebase newEventRef1 = eventRef.push();
        Firebase newEventRef2 = eventRef.push();

        //************* Add user and events to DB *************
        newUserRef1.setValue(testOwner);
        newUserRef2.setValue(testAttendee);
        newEventRef1.setValue(testEvent1);
        newEventRef2.setValue(testEvent2);

        //************* Grab user from DB *************
        // Grab ID for recently added user
        String attendingId = newUserRef2.getKey();
        // Get the reference to the event node in Firebase
        Firebase usersId = ref.child("users").child(attendingId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    userToTest = snapshot.getValue(User.class);
                } catch (Exception e) {
                    assertNotNull(userToTest);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        //*********** test that user's attending events match expected values ************
        // Grab ID's of events
        String event1Id = newEventRef1.getKey();
        String event2Id = newEventRef2.getKey();
        assert(userToTest.getEventsAttending().contains(event1Id));
        assert(userToTest.getEventsAttending().contains(event2Id));

        //******** clean up DB ***************
        userRef.child(newUserRef1.getKey()).removeValue();
        userRef.child(newEventRef2.getKey()).removeValue();
        eventRef.child(newEventRef1.getKey()).removeValue();
        eventRef.child(newEventRef2.getKey()).removeValue();
    }

    @Test(timeout = TIMEOUT)
    public void testUserCreatedList(){
        //************* Setup *************
        User testOwner = new User("createTestId", "create name");
        Event testEvent1 = new Event(testOwner.getFacebookID(), "testTitle1", "testDesc", "testLoc", 100, new Date(0));
        Event testEvent2 = new Event(testOwner.getFacebookID(), "testTitle2", "testDesc", "testLoc", 100, new Date(0));

        // Generate unique ID for user and for events
        Firebase userRef = ref.child("users"); // will always add to the db
        Firebase newUserRef1 = userRef.push(); // will always generate the unique id
        Firebase eventRef = ref.child("events");
        Firebase newEventRef1 = eventRef.push();
        Firebase newEventRef2 = eventRef.push();

        //************* Add user and events to DB *************
        newUserRef1.setValue(testOwner);
        newEventRef1.setValue(testEvent1);
        newEventRef2.setValue(testEvent2);

        //************* Grab user from DB *************
        // Grab ID for recently added user
        String ownerId = newEventRef1.getKey();
        // Get the reference to the event node in Firebase
        Firebase usersId = ref.child("users").child(ownerId);
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                try {
                    userToTest = snapshot.getValue(User.class);
                } catch (Exception e) {
                    assertNotNull(userToTest);
                }
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });
        //*********** test that user's attending events match expected values ************
        // Grab ID's of events
        String event1Id = newEventRef1.getKey();
        String event2Id = newEventRef2.getKey();
        assert(userToTest.getEventsCreated().contains(event1Id));
        assert(userToTest.getEventsCreated().contains(event2Id));

        //******** clean up DB ***************
        userRef.child(userToTest.getFacebookID()).removeValue();
        eventRef.child(newEventRef1.getKey()).removeValue();
        eventRef.child(newEventRef2.getKey()).removeValue();
    }
}
