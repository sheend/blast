package cse403.blast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.Firebase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cse403.blast.Data.Constants;
import cse403.blast.Model.Event;
import cse403.blast.Model.User;

/**
 * The Detail Activity displays the information of the Event in more detail.
 */
public class DetailActivity extends AppCompatActivity {

    private final String TAG = "DetailActivity";
    private Event event;
    private User currentUser;
    private String currentUserID;
    private SharedPreferences preferenceSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Grab ID of current user from SharedPreferences file
        preferenceSettings = getSharedPreferences(Constants.SHARED_KEY, Context.MODE_PRIVATE);
        currentUserID = preferenceSettings.getString("userid", "user");
        Log.i("detailActivity", "theCurrentID is: " + currentUserID);

        // Grab User object from SharedPreferences file
        Gson gson = new Gson();
        String json = preferenceSettings.getString("MyUser", "");
        Log.i("DetailActivity", "JSON: " + json);
        currentUser = gson.fromJson(json, User.class);

        if (currentUser != null) Log.i("SUCCESS?", "YES T^T");
        Log.i("user set??", "" + currentUser.getEventsAttending());

        Intent detailIntent = getIntent();
        event = (Event) detailIntent.getSerializableExtra("event");
        TextView title = (TextView) findViewById(R.id.detail_title);
        title.setText(event.getTitle());

        // TODO: Only display the hour of the time (ie. @ 7pm)
        TextView time = (TextView) findViewById(R.id.detail_time);

        time.setText(getString(R.string.detail_time) + event.getEventTime());

        TextView desc = (TextView) findViewById(R.id.detail_desc);
        desc.setText(getString(R.string.detail_what) + event.getDesc());

        // TODO: Display the list of attendees by their Facebook profile picture after
        // TODO: integrating with Facebook
        TextView attendees = (TextView) findViewById(R.id.detail_attendees);
        Set<User> users = event.getAttendees();
        String list = "";
        for (User user: users) {
            if (event.getOwner().equals(user)) {
                list += "(Creator) ";
            }
            list += user.getFacebookID()+ ", ";
        }
        attendees.setText(getString(R.string.detail_who) + list);

        // TODO: Display location using text, but hopefully with a map
        TextView locationLabel = (TextView) findViewById(R.id.detail_location_label);
        locationLabel.setText(getString(R.string.detail_where));

        // Set appropriate text and onclick's depending on user's status
        Button button = (Button) findViewById(R.id.detail_button);
        if (currentUser.equals(event.getOwner())) { // user is owner, have option to edit
            button.setText(getString(R.string.detail_edit));
            // Go to creation page
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent createIntent = new Intent(DetailActivity.this, CreateEventActivity.class);
                    createIntent.putExtra("edit", true);
                    createIntent.putExtra("event", event);
                    startActivity(createIntent);
                }
            });
        } else if (event.getAttendees().contains(currentUser)) { // user is an attendee, have option to leave
            button.setText(getString(R.string.detail_leave));
            // Go back to main page
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainIntent = new Intent(DetailActivity.this, MainActivity.class);
                    currentUser.leaveEvent(event);
                    startActivity(mainIntent);
                }
            });
        } else { // user could potentially attend
            button.setText(R.string.detail_join);
            // Go back to main page
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainIntent = new Intent(DetailActivity.this, MainActivity.class);
                    startActivity(mainIntent);

                    Log.i(TAG, "PRE current user ID: " + currentUser.getFacebookID());
                    Log.i(TAG, "PRE current events attending: " + currentUser.getEventsAttending());
                    Log.i(TAG, "PRE current events created: " + currentUser.getEventsCreated());

                    currentUser.attendEvent(event);

                    ///// update the GSON object in SHARED PREFS TO REFLECT CHANGES IN USER!!

                    Log.i(TAG, "POST current user ID: " + currentUser.getFacebookID());
                    Log.i(TAG, "POST current events attending: " + currentUser.getEventsAttending());
                    Log.i(TAG, "POST current events created: " + currentUser.getEventsCreated());

                    //final User newUser = new User(currentUser.getFacebookID(), currentUser.getEventsCreated(), currentUser.getEventsAttending());

                    // updates user's attending
                    Firebase userRef = new Firebase(Constants.FIREBASE_URL).child("users").child(currentUser.getFacebookID()).child("eventsAttending");
                    userRef.setValue(currentUser.getEventsAttending());

                    // update event's attendees field
                    //Firebase eventRef = new Firebase(Constants.FIREBASE_URL).child("events").child()

//                    Map<String, Object> updatedList = new HashMap<String, Object>();
//                    updatedList.put("eventsAttending", currentUser.getEventsAttending());
//                    newRef.setValue(updatedList);




//                    Map<String, Object> toadd = new HashMap<>
//                    baseref.setValue(newUser);

//                    if (currentUser.getEventsAttending().size() == 0) {
//                        currentUser.setEventsAttending();
//                    }
//                    currentUser.attendEvent(event);
//
//                    final Firebase ref = new Firebase(Constants.FIREBASE_URL).child("users").child(currentUser.getFacebookID());
//                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
//                        @Override
//                        public void onDataChange(DataSnapshot dataSnapshot) {
//                            ref.setValue(newUser);
//                            Log.i("attendingEventTag", "user is attending event");
//                        }
//
//                        @Override
//                        public void onCancelled(FirebaseError firebaseError) {
//
//                        }
//                    });


                }
            });
        }
    }

}