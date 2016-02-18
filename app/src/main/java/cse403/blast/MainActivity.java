package cse403.blast;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import cse403.blast.Data.Constants;
import cse403.blast.Model.Event;
import cse403.blast.Model.User;


/**
 * The Main page of the app, displaying the list of "Blasts" (or events) near you.
 * In the Main Activity, the user has the option to view each event in more detail, access
 * the left drawer, or create a new Blast.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // Redirecting to Login if necessary
        // TODO: replace with real login stuff (ParseUser.getCurrentUser() == null)
        if (true) {
            Log.i(TAG, "NO USER");
            Intent loginPage = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginPage);
            finish();
        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // the Button that allows the user to create a new Event
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createEventIntent = new Intent(MainActivity.this, CreateEventActivity.class);
                startActivity(createEventIntent);

            }
        });

        // The left drawer, which allows the user to view the Events that he/she made, or the Events that
        // he/she is attending.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


//        // hardcoded the userID, event title, description, # of attendees, and time
//        User user1 = new User("Grace");
//        User user2 = new User("Michelle");
//        Event event1 = new Event(user1, "Karaoke on the Ave", "Sing the night away!", "Star Karaoke", 10, new Date(1));
//        user1.addCreatedEvent(event1);
//        event1.addAttendee(new User("Sheen"));
//        event1.addAttendee(new User("Carson"));
//        Event event2 = new Event(user2, "Bubble Tea Run", "Lets get some bubble tea!!", "Oasis", 5, new Date(1));
//        user2.addCreatedEvent(event2);
//        event2.addAttendee(new User("Melissa"));
//        event2.addAttendee(new User("Kristi"));
//        events.add(event1);
//        events.add(event2);

        Firebase ref = new Firebase(Constants.FIREBASE_URL).child("events");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Setting up the list view with everything
                // TODO: Eventually we will have to use Data Manager to populate Events list
                List<Event> events = new ArrayList<Event>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Event eventToAdd = child.getValue(Event.class);
                    events.add(eventToAdd);
                }
                Log.i("Log tag", "The data changed!");

                listEvent(events);
            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }


    public void listEvent(List<Event> events) {
        ListView mainListView = (ListView) findViewById(R.id.main_blast_list_view);

        ArrayAdapter<Event> stringArrayAdapter = new ArrayAdapter<Event>(this,
                android.R.layout.simple_list_item_1, events);
        mainListView.setAdapter(stringArrayAdapter);

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Event eventAtPosition = (Event) parent.getItemAtPosition(position);
                Toast.makeText(MainActivity.this, eventAtPosition.getTitle(), Toast.LENGTH_SHORT).show();

                // Creating a detail activity
                // TODO: remove toString() after Data Manager is set up
                // TODO: Attendees - eventually get the list of attendees once Facebook integration is set up. but for now,
                // TODO: it returns an empty list
                Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);

                detailIntent.putExtra("event", eventAtPosition);
                //detailIntent.putExtra("name", eventAtPosition.getTitle());
                //detailIntent.putExtra("time", eventAtPosition.getEventTime().toString());
                //detailIntent.putExtra("desc", eventAtPosition.getDesc());

//                Set<User> exampleSet = new HashSet<User>();
                detailIntent.putExtra("attendees", (Serializable) eventAtPosition.getAttendees());
                //detailIntent.putExtra("attendees", (Serializable) exampleSet);

                startActivity(detailIntent);
            }
        });
    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
