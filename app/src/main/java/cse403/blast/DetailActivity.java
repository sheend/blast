package cse403.blast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.MutableData;
import com.firebase.client.Transaction;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cse403.blast.Data.FacebookManager;
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
    private SharedPreferences.Editor preferenceEditor;
    private boolean newUser = true;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Tutorial
        // TODO: set constant for first time users, currently shows every time activity is created
        View tutorialDetail = findViewById(R.id.tutorial_detail);
        button = (Button) findViewById(R.id.detail_button);
        // boolean tutorialShown = PreferenceManager.getDefaultSharedPreferences(DetailActivity.this).getBoolean(Constants.PREF_KEY_TUT_MAIN, false);
        if (newUser) {
            tutorialDetail.setVisibility(View.VISIBLE);
            button.setEnabled(false);
        } else {
            tutorialDetail.setVisibility(View.GONE);
        }

        tutorialDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                newUser = false;
                button.setEnabled(true);
            }
        });

        // Real detail starts from here

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


        Firebase ref = new Firebase(Constants.FIREBASE_URL).child("events").child(event.getId());
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
            }


            @Override
            public void onCancelled(FirebaseError firebaseError) {

            }
        });


            // TODO: Only display the hour of the time (ie. @ 7pm)
        TextView time = (TextView) findViewById(R.id.detail_time);

        time.setText("" + event.getEventTime());

        TextView desc = (TextView) findViewById(R.id.detail_desc);
        desc.setText(event.getDesc());

        // TODO: Display the list of attendees by their Facebook profile picture after
        // TODO: integrating with Facebook
//        TextView attendees = (TextView) findViewById(R.id.detail_attendees);
//        Set<String> users = event.getAttendees();
//        String list = "";
//        for (String user: users) {
//            if (event.getOwner().getFacebookID().equals(user)) {
//                list += "(Creator) ";
//            }
//            list += user.getFacebookID()+ ", ";
//        }
//        attendees.setText(getString(R.string.detail_who) + list);

        // TODO: Display location using text, but hopefully with a map
        TextView locationLabel = (TextView) findViewById(R.id.detail_location);
        locationLabel.setText(event.getLocation());

        // Set appropriate text and onclick's depending on user's status
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
        } else if (event.getAttendees().contains(currentUser.getFacebookID())) { // user is an attendee, have option to leave
            button.setText(getString(R.string.detail_leave));
            // Go back to main page
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainIntent = new Intent(DetailActivity.this, MainActivity.class);
                    startActivity(mainIntent);

                    // remove event from user's attending
                    currentUser.leaveEvent(event);

                    setPreferences();

                    updateToFireBase();
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

                    // add event to user's attending
                    currentUser.attendEvent(event);
                    // add user to event's attendees
                    //event.addAttendee(currentUser);

                    setPreferences();

                    Log.i(TAG, "POST current user ID: " + currentUser.getFacebookID());
                    Log.i(TAG, "POST current events attending: " + currentUser.getEventsAttending());
                    Log.i(TAG, "POST current events created: " + currentUser.getEventsCreated());

                    updateToFireBase();

                }
            });
        }
    }

    public void setPreferences() {
        // update the GSON object in SHARED PREFS TO REFLECT CHANGES IN USER!!
        preferenceSettings = getSharedPreferences(Constants.SHARED_KEY, Context.MODE_PRIVATE);
        preferenceEditor = preferenceSettings.edit();

        // Store the current User object in SharedPreferences
        Gson gson = new Gson();
        String json = gson.toJson(currentUser);
        Log.i(TAG, "JSON: " + json);
        preferenceEditor.putString("MyUser", json);
        preferenceEditor.commit();

    }

    public void updateToFireBase() {
        // updates user's attending
        Firebase userRef = new Firebase(Constants.FIREBASE_URL).child("users").child(currentUser.getFacebookID()).child("eventsAttending");
        userRef.setValue(currentUser.getEventsAttending());




        // update event's attendees field
        Firebase eventRef = new Firebase(Constants.FIREBASE_URL).child("events").child(event.getId()).child("attendees");
        eventRef.runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData currentData) {
                currentData.setValue(event.getAttendees());
                return Transaction.success(currentData);
            }

            @Override
            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
                //This method will be called once with the results of the transaction
            }
        });

    }
}