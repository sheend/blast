package cse403.blast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.firebase.client.ChildEventListener;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.Query;
import com.firebase.client.ValueEventListener;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.Set;

import cse403.blast.Data.Constants;
import cse403.blast.Data.FacebookManager;
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

        // Grab ID of current user from shared preferences file
        preferenceSettings = getSharedPreferences(Constants.SHARED_KEY, Context.MODE_PRIVATE);
        currentUserID = preferenceSettings.getString("userid", "user");
        Log.i("detailActivity", "theCurrentID is: " + currentUserID);

        // Grab User object associated with currentUserID

        // final Firebase ref = new Firebase(Constants.FIREBASE_URL).child("users").child(currentUserID);
//
//        Firebase ref = new Firebase(Constants.FIREBASE_URL).child("users");
//        Query queryRef = ref.orderByChild("facebookID").equalTo(currentUserID);
//        queryRef.addChildEventListener(new ChildEventListener() {
//            @Override
//            public void onChildAdded(DataSnapshot snapshot, String previousChild) {
//                   //currentUser = snapshot.getValue(User.class);
//                    currentUser = new User("123456");
//            }
//
//            @Override
//            public void onChildChanged(DataSnapshot snapshot, String previousChild) {
//            }
//
//            @Override
//            public void onChildRemoved(DataSnapshot snapshot) {
//            }
//
//            @Override
//            public void onChildMoved(DataSnapshot snapshot, String previousChild) {
//            }
//
//            @Override
//               public void onCancelled(FirebaseError firebaseError) {
//            }
//        });

//        ref.addListenerForSingleValueEvent(new ValueEventListener() {
//               @Override
//               public void onDataChange(DataSnapshot dataSnapshot) {
//                   currentUser = dataSnapshot.getValue(User.class);
//               }
//
//               @Override
//               public void onCancelled(FirebaseError firebaseError) {
//               }
//           });



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
                    currentUser.attendEvent(event);

                    final Firebase ref = new Firebase(Constants.FIREBASE_URL).child("users").child(currentUser.getFacebookID());
                    ref.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            ref.setValue(currentUser);
                            Log.i("attendingEventTag", "user is attending event");
                        }

                        @Override
                        public void onCancelled(FirebaseError firebaseError) {

                        }
                    });

                    startActivity(mainIntent);
                }
            });
        }
    }
}