package cse403.blast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

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
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //preferenceSettings = getSharedPreferences(Constants.SHARED_KEY, Context.MODE_PRIVATE);
        preferenceSettings = getApplicationContext().getSharedPreferences("blastPrefs", 0);

        /* Tutorial */
        View tutorialDetail = findViewById(R.id.tutorial_detail);
        button = (Button) findViewById(R.id.detail_button);
        if (preferenceSettings.getBoolean("initialDetailLaunch", true)) {
            tutorialDetail.setVisibility(View.VISIBLE);
            button.setEnabled(false);
        } else {
            tutorialDetail.setVisibility(View.GONE);
        }

        tutorialDetail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                button.setEnabled(true);
                preferenceSettings.edit().putBoolean("initialDetailLaunch", false).apply();
            }
        });

        // Real detail starts from here

        // Grab ID of current user from SharedPreferences file
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

        time.setText("" + event.retrieveEventTimeString());

        TextView desc = (TextView) findViewById(R.id.detail_desc);
        desc.setText(event.getDesc());

        // TODO: Display the list of attendees by their Facebook profile picture after
        final TextView attendees = (TextView) findViewById(R.id.detail_attendees);
        List<String> attendeeIDList = new ArrayList<>(event.getAttendees());

        for (String attendeeID : attendeeIDList) {
            Log.i(TAG, "attendee IDS:" + attendeeID);
            if (attendeeID != null && !attendeeID.equals("")) {
                // Query Firebase for the names of the attendees based on given user ID
                final Firebase attendeeRef = new Firebase(Constants.FIREBASE_URL).child("users").child(attendeeID);
                attendeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        User attendeeObj = snapshot.getValue(User.class);

                        if (attendeeObj != null) {
                            if (!attendees.getText().toString().isEmpty()) {
                                attendees.append(", ");
                            }
                            String attName = attendeeObj.getName();
                            attendees.append(attName.substring(0, attName.indexOf(" ")));
                        }
                    }

                    @Override
                    public void onCancelled(FirebaseError error) {
                    }
                });
            }
        }


        // TODO: Display location using text, but hopefully with a map
        TextView locationLabel = (TextView) findViewById(R.id.detail_location);
        locationLabel.setText(event.getLocation());


        // Set appropriate text and onclick's depending on user's status
        if (currentUser.getFacebookID().equals(event.getOwner())) { // user is owner, have option to edit
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
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                    // remove event from user's attending
                    currentUser.leaveEvent(event);
                    setPreferences();
                    updateToFireBase();
                    Toast.makeText(DetailActivity.this, "Left Event", Toast.LENGTH_SHORT).show();
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
                    overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);

                    Log.i(TAG, "PRE current user ID: " + currentUser.getFacebookID());
                    Log.i(TAG, "PRE current events attending: " + currentUser.getEventsAttending());
                    Log.i(TAG, "PRE current events created: " + currentUser.getEventsCreated());


                    Log.i("HOW MANY ATTEND", " " + event.getAttendees().size());
                    Log.i("LIMIT OF THIS EVENT", " " + event.getLimit());


                    if (event.getAttendees().size() - 1 == event.getLimit()) {
                        Intent createIntent = new Intent(DetailActivity.this, MainActivity.class);
                        startActivity(createIntent);
                        Toast.makeText(DetailActivity.this, "This event is already full :(", Toast.LENGTH_SHORT).show();
                    } else {

                        // add event to user's attending
                        currentUser.attendEvent(event);
                        // add user to event's attendees
                        //event.addAttendee(currentUser);

                        setPreferences();

                        Log.i(TAG, "POST current user ID: " + currentUser.getFacebookID());
                        Log.i(TAG, "POST current events attending: " + currentUser.getEventsAttending());
                        Log.i(TAG, "POST current events created: " + currentUser.getEventsCreated());

                        updateToFireBase();
                        Toast.makeText(DetailActivity.this, "You are now attending: " + event.getTitle(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    public void setPreferences() {
        // update the GSON object in SHARED PREFS TO REFLECT CHANGES IN USER!!
        preferenceSettings = getApplicationContext().getSharedPreferences("blastPrefs", 0);
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
        eventRef.setValue(event.getAttendees());


//        eventRef.runTransaction(new Transaction.Handler() {
//            @Override
//            public Transaction.Result doTransaction(MutableData currentData) {
//                currentData.setValue(event.getAttendees());
//                return Transaction.success(currentData);
//            }
//
//            @Override
//            public void onComplete(FirebaseError firebaseError, boolean committed, DataSnapshot currentData) {
//                //This method will be called once with the results of the transaction
//            }
//        });

    }
//  TODO: back button doesn't transition well
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//    }
}