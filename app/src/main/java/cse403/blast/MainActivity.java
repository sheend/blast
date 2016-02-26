package cse403.blast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import cse403.blast.Data.Constants;
import cse403.blast.Data.FacebookManager;
import cse403.blast.Model.Event;
import cse403.blast.Model.User;
import cse403.blast.Support.EventAdapter;


/**
 * The Main page of the app, displaying the list of "Blasts" (or events) near you.
 * In the Main Activity, the user has the option to view each event in more detail, access
 * the left drawer, or create a new Blast.
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private static final String TAG = "MainActivity";
    private ListView mainListView;
    private FacebookManager fbManager = null;
    private boolean IGNORE_LOGIN = true;
    private FloatingActionButton fab;
    private SharedPreferences preferenceSettings;
    private User currentUser;
    private List<Event> attendingEventList;
    private List<Event> createdEventList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Initialize Facebook SDK and set up Facebook Manager
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbManager = FacebookManager.getInstance();

        //If no instance session exists, check local storage
        if (!fbManager.isValidSession() && !IGNORE_LOGIN) {
            fbManager.getSession(getApplicationContext());
        }

        // Redirecting to Login if necessary
        if (!fbManager.isValidSession() && !IGNORE_LOGIN) {
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
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createEventIntent = new Intent(MainActivity.this, CreateEventActivity.class);
                createEventIntent.putExtra("edit", false);
                startActivity(createEventIntent);

            }
        });

        // Getting current user
        preferenceSettings = getSharedPreferences(Constants.SHARED_KEY, Context.MODE_PRIVATE);
        Gson gson = new Gson();
        String json = preferenceSettings.getString("MyUser", "");
        Log.i(TAG, "Set currentUser" + json);
        currentUser = gson.fromJson(json, User.class);

        /* Tutorial */
        View tutorialMain = findViewById(R.id.tutorial_main);
        if (preferenceSettings.getBoolean("initialMainLaunch", true)) {
            tutorialMain.setVisibility(View.VISIBLE);
            fab.setEnabled(false);
            fab.setFocusable(false);
        } else {
            tutorialMain.setVisibility(View.GONE);
        }

        tutorialMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                fab.setEnabled(true);
                fab.setFocusable(true);
                preferenceSettings.edit().putBoolean("initialMainLaunch", false).apply();
            }
        });

        /* Real Main */

        // The left drawer, which allows the user to view the Events that he/she made, or the Events that
        // he/she is attending.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        //NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        //navigationView.setNavigationItemSelectedListener(this);

        TextView profileName = (TextView) findViewById(R.id.profileName);
        profileName.setText(currentUser.getName());
        
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

                setupListEvents(events);

                preSetupAttendingList(R.id.attending_list, currentUser.getEventsAttending());
                preSetupCreatedList(R.id.created_list, currentUser.getEventsCreated());

            }

            @Override
            public void onCancelled(FirebaseError firebaseError) {
            }
        });

    }


    public void setupListEvents(List<Event> events) {
        mainListView = (ListView) findViewById(R.id.main_blast_list_view);

        EventAdapter adapter = new EventAdapter(this, events);
        mainListView.setAdapter(adapter);

        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Event eventAtPosition = (Event) parent.getItemAtPosition(position);

                // Creating a detail activity
                // TODO: remove toString() after Data Manager is set up
                // TODO: Attendees - eventually get the list of attendees once Facebook integration is set up. but for now,
                // TODO: it returns an empty list
                Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);

                detailIntent.putExtra("event", eventAtPosition);

//                Set<User> exampleSet = new HashSet<User>();
                detailIntent.putExtra("attendees", (Serializable) eventAtPosition.getAttendees());
                startActivity(detailIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });

    }

    public void preSetupAttendingList(int elementId, Set<String> events) {
        if (events.contains(""))
            events.remove("");

        List<String> eventIDList = new ArrayList<>(events); // good
        attendingEventList = new ArrayList<Event>(); // non null = good

        //LongOperation task = new LongOperation().doInBackground(attendingEventList);
        new AttendingUpdates().execute(eventIDList);

    }

    public void preSetupCreatedList(int elementId, Set<String> events) {
        if (events.contains(""))
            events.remove("");

        List<String> eventIDList = new ArrayList<>(events); // good
        createdEventList = new ArrayList<Event>(); // non null = good

        //LongOperation task = new LongOperation().doInBackground(attendingEventList);
        new CreatedUpdates().execute(eventIDList);

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

        switch (id) {
            case R.id.action_logout:
                fbManager.clearSession(getApplicationContext());
                fbManager.clearToken();
                LoginManager.getInstance().logOut();

                //redirect back to login page
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.action_settings:
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override

    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method for Setting the Height of the ListView dynamically.
     * Fix the issue of not showing all the items of the ListView
     * when placed inside a ScrollView
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int height = metrics.heightPixels;
        Log.i(TAG, "height:" + height); // 2392, 1184 : 1592, 384

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        Resources r = getResources();
        //Header is 160dp
        float header = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, r.getDisplayMetrics());
        int listSpace = height - Math.round(header);
        params.height = (int)(0.4 * listSpace); // Almost half remaining space divided by 2 sections

        listView.setLayoutParams(params);
    }

    /*@Override
    protected void onStop() {
        if (fbManager.isValidSession()) {
            fbManager.saveSession(getApplicationContext());
        } else {
            fbManager.clearSession(getApplicationContext());
        }
        super.onStop();
    }*/

    private class AttendingUpdates extends AsyncTask<List<String>, Void, String> {

        @Override
        protected String doInBackground(List<String>... params) {

            for (String eventID : (List<String>) params[0]) {

                // Query Firebase for the Event object based on given ID
                final Firebase ref = new Firebase(Constants.FIREBASE_URL).child("events").child(eventID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Event eventToAdd = snapshot.getValue(Event.class);
                        if (eventToAdd != null)
                            attendingEventList.add(eventToAdd);
                        //Log.i(TAG, "LEFT DRAWER EVENT LIST (updated): " + attendingEventList.toString() + " ");
                    }

                    @Override
                    public void onCancelled(FirebaseError error) {
                    }
                });

            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            Log.i(TAG, "LEFT DRAWER EVENT LIST: " + attendingEventList.toString());
            setupNavLists(R.id.attending_list);

        }

        private void setupNavLists(int elementId) {
            ArrayAdapter<Event> navAdapter = new ArrayAdapter<Event>(MainActivity.this, android.R.layout.simple_list_item_1, attendingEventList);
            ListView sideNavListView = (ListView) findViewById(elementId);
            sideNavListView.setAdapter(navAdapter);

            // Hack to make the listview scroll on the sidebar
            sideNavListView.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            sideNavListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Event eventAtPosition = (Event) parent.getItemAtPosition(position);

                    Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                    detailIntent.putExtra("event", eventAtPosition);
                    startActivity(detailIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            });

            setListViewHeightBasedOnChildren(sideNavListView);
        }
    }

    private class CreatedUpdates extends AsyncTask<List<String>, Void, String> {

        @Override
        protected String doInBackground(List<String>... params) {

            for (String eventID : (List<String>) params[0]) {

                // Query Firebase for the Event object based on given ID
                final Firebase ref = new Firebase(Constants.FIREBASE_URL).child("events").child(eventID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Event eventToAdd = snapshot.getValue(Event.class);
                        if (eventToAdd != null)
                            createdEventList.add(eventToAdd);
                        //Log.i(TAG, "LEFT DRAWER EVENT LIST (updated): " + attendingEventList.toString() + " ");
                    }

                    @Override
                    public void onCancelled(FirebaseError error) {
                    }
                });

            }

            return "Executed";
        }

        @Override
        protected void onPostExecute(String result) {

            Log.i(TAG, "LEFT DRAWER EVENT LIST: " + createdEventList.toString());
            setupNavLists(R.id.created_list);

        }

        private void setupNavLists(int elementId) {
            ArrayAdapter<Event> navAdapter = new ArrayAdapter<Event>(MainActivity.this, android.R.layout.simple_list_item_1, createdEventList);
            ListView sideNavListView = (ListView) findViewById(elementId);
            sideNavListView.setAdapter(navAdapter);

            // Hack to make the listview scroll on the sidebar
            sideNavListView.setOnTouchListener(new View.OnTouchListener() {
                // Setting on Touch Listener for handling the touch inside ScrollView
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    // Disallow the touch request for parent scroll on touch of child view
                    v.getParent().requestDisallowInterceptTouchEvent(true);
                    return false;
                }
            });

            sideNavListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Event eventAtPosition = (Event) parent.getItemAtPosition(position);

                    Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                    detailIntent.putExtra("event", eventAtPosition);
                    startActivity(detailIntent);
                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

                }
            });

            setListViewHeightBasedOnChildren(sideNavListView);
        }
    }

}
