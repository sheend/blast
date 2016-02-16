package cse403.blast;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.w3c.dom.Text;

import java.io.Serializable;
import java.util.Set;

import cse403.blast.Model.Event;
import cse403.blast.Model.User;

/**
 * The Detail Activity displays the information of the Event in more detail.
 */
public class DetailActivity extends AppCompatActivity {

    private final String TAG = "DetailActivity";
    private Event event;
    private User currentUser = new User("Grace");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Grab associated event and display event title
        Intent detailIntent = getIntent();
        event = (Event) detailIntent.getSerializableExtra("event");
        TextView title = (TextView) findViewById(R.id.detail_title);
        title.setText(event.getTitle());

        // TODO: Only display the hour of the time (ie. @ 7pm)
        TextView time = (TextView) findViewById(R.id.detail_time);
        time.setText("@ " + event.getEventTime());

        TextView desc = (TextView) findViewById(R.id.detail_desc);
        desc.setText("What: " + event.getDesc());

        // TODO: Display the list of attendees by their Facebook profile picture after
        // TODO: integrating with Facebook
        TextView attendees = (TextView) findViewById(R.id.detail_attendees);
        Set<User> users = event.getAttendees();
        String list = "";
        for (User user: users) {
            list += user.getFacebookID()+ ", ";
        }
        attendees.setText("Who: " + list);

        // TODO: Display location using text, but hopefully with a map
        TextView locationLabel = (TextView) findViewById(R.id.detail_location_label);
        locationLabel.setText("Where: ");

        // TODO: get current user's status (new viewer, current attendee, or owner)
        // TODO: and display appropriate text and connect to appropriate onclick
        Button button = (Button) findViewById(R.id.button);
        if (currentUser.equals(event.getOwner())) { // user is owner, have option to edit
            button.setText("Edit Blast");
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
            button.setText("Leave Blast :(");
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
            button.setText("Have a Blast! :)");
            // Go back to main page
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent mainIntent = new Intent(DetailActivity.this, MainActivity.class);
                    currentUser.attendEvent(event);
                    startActivity(mainIntent);
                }
            });
        }
    }
}