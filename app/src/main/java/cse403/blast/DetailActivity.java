package cse403.blast;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import cse403.blast.Data.Constants;
import cse403.blast.Data.FacebookManager;
import cse403.blast.Data.LocationHandler;
import cse403.blast.Model.Event;
import cse403.blast.Model.User;
import cse403.blast.Support.RoundImage;

/**
 * The Detail Activity displays the information of the Event in more detail.
 * It will display the title, time, location, description, and the names of
 * the people who are currently attending the event. A user will have the option
 * to attend the blast and a creator will have the option to edit or delete the event.
 */
public class DetailActivity extends AppCompatActivity {

    private final String TAG = "DetailActivity";
    private final int MAX_NUM_IMGS = 1;
    private Event event;
    private User currentUser;
    private String currentUserID;
    private SharedPreferences preferenceSettings;
    private SharedPreferences.Editor preferenceEditor;
    private Button button;

    /**
     * When the activity starts, it will will display the title, time, location, description,
     * and the names of the people who are currently attending the event. A user will have the option
     * to attend the blast and a creator will have the option to edit or delete the event.
     *
     * @param savedInstanceState: saved stated of this Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.getBackground().setAlpha(0); //making the toolbar transparent

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("");
        preferenceSettings = getApplicationContext().getSharedPreferences("blastPrefs", 0);

        /* TUTORIAL:
         * displays the tutorial once if the current user is a new user
         */
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
        //setTitle(event.getTitle());

        ImageView eventImage = (ImageView) findViewById(R.id.detail_category_image);

        //TODO: remove duplication
        String selected = event.getCategory().toUpperCase();
        if (selected.equals("Social".toUpperCase())) {
            eventImage.setImageResource(R.drawable.social);
        } else if (selected.equals("Active".toUpperCase())) {
            eventImage.setImageResource(R.drawable.active);
        } else if (selected.equals("Food".toUpperCase())) {
            eventImage.setImageResource(R.drawable.food);
        } else if (selected.equals("Entertainment".toUpperCase())) {
            eventImage.setImageResource(R.drawable.entertainment);
        } else {
            eventImage.setImageResource(R.drawable.other);
        }


        Firebase ref = new Firebase(Constants.FIREBASE_URL).child("events").child(event.getId());
        ref.addValueEventListener(new ValueEventListener() {
            /**
             * Queries the event object from the database based on the event ID.
             *
             * @param dataSnapshot: Takes a snapshot of the current state of the database
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                event = dataSnapshot.getValue(Event.class);
            }

            /**
             * Displays an informative message to the user
             *
             * @param firebaseError: error raised by Firebase
             */
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(DetailActivity.this, "Unable to connect.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "error: " + firebaseError.getMessage());
            }
        });


        // TODO: Only display the hour of the time (ie. @ 7pm)
        TextView time = (TextView) findViewById(R.id.detail_time);

        time.setText("" + event.retrieveEventTimeString());

        TextView desc = (TextView) findViewById(R.id.detail_desc);
        desc.setText(event.getDesc());

        // TODO: Display the list of attendees by their Facebook profile picture after
        //final TextView attendees = (TextView) findViewById(R.id.attendees_text);
        final LinearLayout attendeeImages = (LinearLayout) findViewById(R.id.attendees_images);
        final List<String> attendeeIDList = new ArrayList<>(event.getAttendees());
        int imageCount = 0;
        for (String attendeeID : attendeeIDList) {
            Log.i(TAG, "attendee IDS:" + attendeeID);
            if (attendeeID != null && !attendeeID.equals("")) {

                if (imageCount < MAX_NUM_IMGS) {
                    FacebookManager fbManager = FacebookManager.getInstance();
                    ImageView iView = new ImageView(DetailActivity.this);
                    try {
                        RoundImage roundImage = new RoundImage(fbManager.getFacebookProfilePicture(attendeeID));
                        iView.setImageDrawable(roundImage);
                        attendeeImages.addView(iView);
                    } catch (IOException e) {
                        final Firebase attendeeRef = new Firebase(Constants.FIREBASE_URL).child("users").child(attendeeID);
                        attendeeRef.addListenerForSingleValueEvent(new ValueEventListener() {
                            /**
                             *Queries the database for the names of the attendees based on the given user ID
                             *
                             * @param snapshot: Takes a snapshot of the current state of the database
                             */
                            @Override
                            public void onDataChange(DataSnapshot snapshot) {
                                User attendeeObj = snapshot.getValue(User.class);
                                /*if (attendeeObj != null) {
                                    if (!attendees.getText().toString().isEmpty()) {
                                        attendees.append(", ");
                                    }
                                    String attName = attendeeObj.getName();
                                    attendees.append(attName.substring(0, attName.indexOf(" ")));
                                }*/
                            }

                            /**
                             * Displays an informative message to the user
                             *
                             * @param firebaseError: error raised by Firebase
                             */
                            @Override
                            public void onCancelled(FirebaseError firebaseError) {
                                Toast.makeText(DetailActivity.this, "Unable to connect.", Toast.LENGTH_LONG).show();
                                Log.d(TAG, "error: " + firebaseError.getMessage());
                            }
                        });
                    }
                    imageCount++;
                } else {
                    TextView otherAttendees = new TextView(DetailActivity.this);
                    otherAttendees.setTextSize(20);
                    otherAttendees.setTextColor(getResources().getColor(R.color.lightText));
                    otherAttendees.append(" and " + (attendeeIDList.size() - MAX_NUM_IMGS - 1) + " others");
                    attendeeImages.addView(otherAttendees);
                    break;
                }
            }
        }

        // TODO: Display location using text, but hopefully with a map
        TextView locationLabel = (TextView) findViewById(R.id.detail_location);
        locationLabel.setText(event.getLocation());

        LocationHandler instance = new LocationHandler();
        ImageView mapView = (ImageView) findViewById(R.id.detail_location_image);
        WebView webView = (WebView) findViewById(R.id.map_webview);

        try {
            //TODO: Add proper address, handle case when image is not returned
            mapView.setImageBitmap(instance.getStaticImage("4131 University Way NE, Seattle, WA 98105",
                    event.getLatitude(), event.getLongitude()));
            //webView.loadUrl("https://www.google.com/maps/embed/v1/place?q=4131+University+Way+NE,+Seattle,+WA+98105&key=AIzaSyCfGcSst1xqsfr7P_mxPEvcIZylw7ZhX9Y");


        } catch (IOException e) {
            Log.i(TAG, "Map image didn't show didn't show");
        }

        // Set appropriate text and onclick's depending on user's status
        if (currentUser.getFacebookID().equals(event.getOwner())) { // user is owner, have option to edit
            button.setText(getString(R.string.detail_edit));
            // Go to creation page
            button.setOnClickListener(new View.OnClickListener() {
                /**
                 * Directs to CreateEventActivity to allow the owner to edit the event
                 *
                 * @param v: the current View
                 */
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
                /**
                 * Directs to the MainActivity if the user wants to leave the event
                 *
                 * @param v: the current View
                 */
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
                /**
                 * Directs to the MainActivity once the user attends the event
                 *
                 * @param v: the current View
                 */
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

                    /**
                     * Prevents the user from attending the event if the number of attendees
                     * has reached the limit.
                     */
                    if (event.getAttendees().size() - 1 == event.getLimit()) {
                        Intent createIntent = new Intent(DetailActivity.this, MainActivity.class);
                        startActivity(createIntent);
                        Toast.makeText(DetailActivity.this, "This event is already full :(", Toast.LENGTH_SHORT).show();
                    } else {  // still has space in the event

                        // add event to user's attending
                        currentUser.attendEvent(event);

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

    /**
     * Stores the current User object in Shared Preferences
     */
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

    /**
     * Updates the current user's event attending list and updates the
     * new list in the database
     */
    public void updateToFireBase() {
        // updates user's attending
        Firebase userRef = new Firebase(Constants.FIREBASE_URL).child("users").child(currentUser.getFacebookID()).child("eventsAttending");
        userRef.setValue(currentUser.getEventsAttending());

        // update event's attendees field
        Firebase eventRef = new Firebase(Constants.FIREBASE_URL).child("events").child(event.getId()).child("attendees");
        eventRef.setValue(event.getAttendees());
    }

//  TODO: back button doesn't transition well
//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//        overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
//    }

}