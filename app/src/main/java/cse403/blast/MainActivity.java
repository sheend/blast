package cse403.blast;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.FacebookSdk;
import com.facebook.Profile;
import com.facebook.login.LoginManager;
import com.firebase.client.DataSnapshot;
import com.firebase.client.Firebase;
import com.firebase.client.FirebaseError;
import com.firebase.client.ValueEventListener;
import com.google.gson.Gson;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Set;

import cse403.blast.Data.Constants;
import cse403.blast.Data.FacebookManager;
import cse403.blast.Data.LocationHandler;
import cse403.blast.Model.Event;
import cse403.blast.Model.User;
import cse403.blast.Support.EventAdapter;
import cse403.blast.Support.RoundImage;

/**
 * The Main landing page of the app, displaying the list of "Blasts" (or events) near you.
 * In the Main Activity, the user has the option to view each event in more detail, access
 * the left drawer, or create a new Blast.
 *
 * Main Screen:
 *  The MainActivity list filters events based on time, only displaying events that will
 *  be occurring within 24 hours from the current time.
 *
 * Left Drawer:
 *  The left drawer (swipe in from left or click on hamburger button on upper lefthand side of
 *  screen) contains information on the current user's Blasts Attending and Blasts Created.
 *
 * Create New Blast:
 *  Indicated by a hovering "+" botton, the creat button will redirect the user to the
 *  CreateEventActivity.
 *
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private List<Event> attendingEventList;
    private List<Event> createdEventList;
    private FacebookManager fbManager = null;
    private FloatingActionButton fab;
    private Gson gson;
    private ListView mainListView;
    private EventAdapter adapter;
    private User currentUser;
    private boolean sortByTime;

    private static final String TAG = "MainActivity";

    /**
     * Populates the landing page with events within 24 hours and
     * populates the left drawer with the current user's events attending
     * and created.
     *
     * Checks that the current user has logged in, otherwise redirects to
     * the Login page.
     *
     * @param savedInstanceState: saved state of this Activity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        //Initialize Facebook SDK and set up Facebook Manager
        FacebookSdk.sdkInitialize(getApplicationContext());
        fbManager = FacebookManager.getInstance();

        // If no instance session exists, check local storage
        if (!fbManager.isValidSession()) {
            fbManager.getSession(getApplicationContext());
        }

        // Redirecting to Login if necessary
        if (!fbManager.isValidSession()) {
            Log.i(TAG, "NO USER");
            Intent loginPage = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(loginPage);
            finish();
        }

        // Initialize frame
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // The Button that allows the user to create a new Event
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent createEventIntent = new Intent(MainActivity.this, CreateEventActivity.class);
                createEventIntent.putExtra("edit", false);
                startActivity(createEventIntent);
            }
        });

        // Get current user from shared preferences
        final SharedPreferences preferenceSettings = getApplicationContext().getSharedPreferences("blastPrefs", 0);
        gson = new Gson();
        String json = preferenceSettings.getString("MyUser", "");
        Log.i(TAG, "Set currentUser" + json);
        currentUser = gson.fromJson(json, User.class);
        sortByTime = true;

        // No user found in local memory, try firebase (new user just logged in)
        if (currentUser == null) {
            if (fbManager.isValidSession()) {
                final String fid = FacebookManager.getInstance().getUserID();
                final String name = FacebookManager.getInstance().getUserName();
                final Firebase ref = new Firebase(Constants.FIREBASE_URL).child("users").child(fid);

                // Add listener for the child with given Facebook ID
                ref.addListenerForSingleValueEvent(new ValueEventListener() {

                    /**
                     * Queries for the User with given FacebookID and initializes
                     * the local User.
                     *
                     * @param dataSnapshot Object representing the current state of the database
                     *                     at the given reference node
                     */
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        if (dataSnapshot.getValue() == null) {
                            // Add user to DB
                            currentUser = new User(fid, name);
                            ref.setValue(currentUser);
                            Log.i(TAG, "We added a new user");
                        } else {
                            // User already exists, so extract the information
                            currentUser = dataSnapshot.getValue(User.class);
                            Log.i(TAG, "User already exists in db");
                        }

                        // Store user to shared preferences
                        SharedPreferences settings = getApplicationContext().getSharedPreferences("blastPrefs", 0);
                        SharedPreferences.Editor editor = settings.edit();
                        editor.putString("userid", fid);
                        editor.putString("name", name);

                        populateLeftDrawer();

                        // Store the current User object in SharedPreferences
                        gson = new Gson();
                        String json = gson.toJson(currentUser);
                        Log.i(TAG, "JSON: " + json);
                        editor.putString("MyUser", json);
                        editor.commit();
                    }

                    /**
                     * Displays an informative message to the user if unable to complete request.
                     * @param firebaseError error raised by Firebase
                     */
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Toast.makeText(MainActivity.this, "Unable to connect.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "error: " + firebaseError.getMessage());
                    }
                });
            }
        } else {
            populateLeftDrawer();
        }

        // Display tutorial
        View tutorialMain = findViewById(R.id.tutorial_main);
        if (preferenceSettings.getBoolean("initialMainLaunch", true)) {
            tutorialMain.setVisibility(View.VISIBLE);
            fab.setEnabled(false);
            fab.setFocusable(false);
        } else {
            tutorialMain.setVisibility(View.GONE);
        }

        // Hides tutorial when user clicks on screen
        tutorialMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                v.setVisibility(View.GONE);
                fab.setEnabled(true);
                fab.setFocusable(true);
                preferenceSettings.edit().putBoolean("initialMainLaunch", false).apply();
            }
        });

        // Real Main

        // The left drawer, which allows the user to view the Events that he/she made
        // or the Events that he/she is attending.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Query firebase for all events and store in local events list
        Firebase ref = new Firebase(Constants.FIREBASE_URL).child("events");
        ref.addValueEventListener(new ValueEventListener() {

            /**
             * Queries for all events in the database and initializes the local copy of
             * the events list.
             *
             * @param dataSnapshot Object representing the current state of the database
             *                     at the given reference node
             */
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Setting up the list view with everything
                List<Event> events = new ArrayList<Event>();

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    Event eventToAdd = child.getValue(Event.class);
                    events.add(eventToAdd);
                }
                Log.i("Log tag", "The events list changed!");
                setupListEvents(events);
            }

            /**
             * Displays an informative message to the user if unable to complete request.
             * @param firebaseError error raised by Firebase
             */
            @Override
            public void onCancelled(FirebaseError firebaseError) {
                Toast.makeText(MainActivity.this, "Unable to connect.", Toast.LENGTH_LONG).show();
                Log.d(TAG, "error: " + firebaseError.getMessage());
            }
        });
    }

    /**
     * Populates left drawer for old and new users, events and profile picture
     */
    public void populateLeftDrawer() {
        //set user name in left drawer
        TextView profileName = (TextView) findViewById(R.id.profileName);
        profileName.setText(currentUser.getName());

        //populate attending list
        preSetupAttendingList(R.id.attending_list, currentUser.getEventsAttending());
        preSetupCreatedList(R.id.created_list, currentUser.getEventsCreated());

        ImageView iv = (ImageView) findViewById(R.id.imageView);
        try {
            RoundImage roundImage = new RoundImage(fbManager.getFacebookProfilePicture(fbManager.getUserID()));
            iv.setImageDrawable(roundImage);
            Log.i(TAG, " RAN");
        } catch (IOException e) {
            Log.i(TAG, "error: " + e.getMessage());
        }
    }

    /**
     * Displays all events that are occuring within 24 hours of the current time.
     *
     * @param events list of all events submitted to the applicataion
     */
    public void setupListEvents(List<Event> events) {
        mainListView = (ListView) findViewById(R.id.main_blast_list_view);

        // Sanitize list events: remove events that are in the past
        // or more than 24 hours in the future
        for (int i = 0; i < events.size(); i++) {
            String timeDif = events.get(i).retrieveTimeDifference();
            if (timeDif.startsWith(" -") || timeDif.contains(" d")) {
                events.remove(i);
                i--;
            }
        }

        // Adding the adapter with all the events, sorting in
        // real time with the adapter's sort
        adapter = new EventAdapter(this, events);
        mainListView.setAdapter(adapter);

        if (sortByTime) {
            setSortByTime(adapter);
        } else {
            setSortByDistance(adapter);
        }

        // Redirects user to the Detail Activity after clicking on an event
        mainListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            /**
             * Sets up a click listener for list items; redirects when necessary
             *
             * @param parent parent Adapter View
             * @param view current View
             * @param position position of clicked item in list
             * @param id ID of the clicked item
             */
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Event eventAtPosition = (Event) parent.getItemAtPosition(position);

                // Launch the Detail Activity
                Intent detailIntent = new Intent(MainActivity.this, DetailActivity.class);
                detailIntent.putExtra("event", eventAtPosition);
                detailIntent.putExtra("attendees", (Serializable) eventAtPosition.getAttendees());
                startActivity(detailIntent);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
            }
        });
    }

    /**
     * sets the main activity list adapter to sort by time proximity
     */
    private void setSortByTime(EventAdapter adapter) {
        Log.i(TAG, "Sorting by time");
        adapter.sort(new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                return lhs.compareTo(rhs);
            }
        });
    }

    /**
     * sets the main activity list adapter to sort by distance proximity
     */
    private void setSortByDistance(EventAdapter adapter) {
        Log.i(TAG, "Sorting by distance");
        LocationHandler instance = new LocationHandler();
        final Location user = getUserLocation(); //if location services not enabled, defaults seattle

        adapter.sort(new Comparator<Event>() {
            @Override
            public int compare(Event lhs, Event rhs) {
                // separate events without proper chosen location
                if (lhs.getLatitude() == Double.NaN && rhs.getLatitude() == Double.NaN) {
                    return 0;
                } else if (lhs.getLatitude() == Double.NaN) {
                    return 1;
                } else if (rhs.getLatitude() == Double.NaN) {
                    return -1;
                }

                float[] result1 = new float[3];
                android.location.Location.distanceBetween(user.getLatitude(), user.getLongitude(),
                        lhs.getLatitude(), lhs.getLongitude(), result1);
                Float distance1 = result1[0];

                float[] result2 = new float[3];
                android.location.Location.distanceBetween(user.getLatitude(), user.getLongitude(),
                        rhs.getLatitude(), rhs.getLongitude(), result2);
                Float distance2 = result2[0];

                return distance1.compareTo(distance2);
            }
        });
    }

    /**
     * Initializes the user's Events Attending list for display in the left drawer.
     * Executes the task that dynamically updates the "Blasts Attending" list.
     *
     * @param elementId ID of event element
     * @param events list of all events
     */
    public void preSetupAttendingList(int elementId, Set<String> events) {
        if (events.contains(""))
            events.remove("");

        List<String> eventIDList = new ArrayList<>(events);
        attendingEventList = new ArrayList<Event>();
        new AttendingUpdates().execute(eventIDList);
    }

    /**
     * Initializes the user's Events Created list for display in the left drawer.
     * Executes the task that dynamically updates the "Blasts Created" list.
     *
     * @param elementId ID of event element
     * @param events list of all events
     */
    public void preSetupCreatedList(int elementId, Set<String> events) {
        if (events.contains(""))
            events.remove("");

        List<String> eventIDList = new ArrayList<>(events);
        createdEventList = new ArrayList<Event>();
        new CreatedUpdates().execute(eventIDList);
    }

    /**
     * Closes drawer when back button is pressed.
     */
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        }
    }

    /**
     * Inflates the menu and adds items to the action bar if it is present.
     *
     * @param menu menu to be inflated
     * @return true if success
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    /**
     * Handle action bar item clicks here. The action bar will
     * automatically handle clicks on the Home/Up button, so long
     * as the a parent activity in the Android Manifest is specified.
     *
     * @param item
     * @return
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_logout:
                fbManager.clearSession(getApplicationContext());
                fbManager.clearToken();
                LoginManager.getInstance().logOut();
                Profile.getCurrentProfile().setCurrentProfile(null);

                // Clears the Blast application Shared Preferences file
                SharedPreferences settings = getApplicationContext().getSharedPreferences("blastPrefs", 0);
                SharedPreferences.Editor editor = settings.edit();
                editor.putString("userid", "");
                editor.putString("name", "");
                editor.putString("MyUser", "");
                editor.commit();

                // Redirect back to login page
                Intent i = new Intent(this, LoginActivity.class);
                startActivity(i);
                finish();
                return true;
            case R.id.action_sort:
                if (sortByTime) {
                    setSortByDistance(adapter);
                    Toast.makeText(this, "Sorting events by distance", Toast.LENGTH_SHORT).show();
                    // change icon to clock
                    item.setIcon(R.drawable.ic_sort_time);
                    sortByTime = false;
                } else {
                    setSortByTime(adapter);
                    Toast.makeText(this, "Sorting events by time", Toast.LENGTH_SHORT).show();
                    // change icon to map
                    item.setIcon(R.drawable.ic_sort_distance);
                    sortByTime = true;
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Handle navigation view item clicks here.
     *
     * @param item menu item selected
     * @return true on success
     */
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Method for Setting the Height of the ListView dynamically.
     * Fix the issue of not showing all the items of the ListView
     * when placed inside a ScrollView
     *
     * @param listView list view being adjusted
     */
    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter listAdapter = listView.getAdapter();
        if (listAdapter == null)
            return;

        DisplayMetrics metrics = Resources.getSystem().getDisplayMetrics();
        int height = metrics.heightPixels;
        Log.i(TAG, "height:" + height);

        ViewGroup.LayoutParams params = listView.getLayoutParams();

        //Header is 160dp
        Resources r = getResources();
        float header = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, r.getDisplayMetrics());
        int listSpace = height - Math.round(header);
        params.height = (int)(0.4 * listSpace); // Almost half remaining space divided by 2 sections

        listView.setLayoutParams(params);
    }

    /**
     * Gets the latitude and longitude of the current user
     * @return the Location of the current user
     */
    public Location getUserLocation() {
        LocationManager lm = (LocationManager) (getSystemService(Context.LOCATION_SERVICE));

        if ( Build.VERSION.SDK_INT >= 23 &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        //default location is drumheller fountain, putting in cse felt to specific
        double lng = -122.3079;
        double lat = 47.6539;

        if (location != null) {
            lng = location.getLongitude();
            lat = location.getLatitude();
        }

        Location userLoc = new Location("userLocation");
        userLoc.setLatitude(lat);
        userLoc.setLongitude(lng);
        return userLoc;
    }

    /**
     * Represents the asynchronous task that updates the Blasts Attending list in the
     * Left Drawer.
     */
    private class AttendingUpdates extends AsyncTask<List<String>, Void, String> {

        /**
         * Query Firebase for Event objects based on given IDs and populates
         * the attending event list.
         *
         * @param params List of Event IDs
         * @return String "Executed" on success
         */
        @Override
        protected String doInBackground(List<String>... params) {
            for (String eventID : (List<String>) params[0]) {

                final Firebase ref = new Firebase(Constants.FIREBASE_URL).child("events").child(eventID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {

                    /**
                     * Queries for the Events with given EventIDs and initializes
                     * the local attending event list.
                     *
                     * @param snapshot Object representing the current state of the database
                     *                     at the given reference node
                     */
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Event eventToAdd = snapshot.getValue(Event.class);
                        if (eventToAdd != null && eventToAdd.getEventTime().after(new Date()))
                            attendingEventList.add(eventToAdd);
                    }

                    /**
                     * Displays an informative message to the user if unable to complete request.
                     * @param firebaseError error raised by Firebase
                     */
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Toast.makeText(MainActivity.this, "Unable to connect.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "error: " + firebaseError.getMessage());
                    }
                });
            }
            return "Executed";
        }

        /**
         * On the completion of the asynchronous query, set up left drawer
         * navigation lists with Events Attending
         *
         * @param result Result message from previous task
         */
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "LEFT DRAWER EVENT LIST: " + attendingEventList.toString());
            setupNavLists(R.id.attending_list);
        }

        /**
         * Displays the Events Attending list in the left drawer
         *
         * @param elementId: id of list to be populated
         */
        private void setupNavLists(int elementId) {
            ArrayAdapter<Event> navAdapter = new ArrayAdapter<Event>(MainActivity.this,
                    android.R.layout.simple_list_item_1, attendingEventList);
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

            // Sets up a listener for the attending list; on click, the user will be
            // redirected to the Event's detail page.
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

    /**
     * Represents the asynchronous task that updates the Blasts Created list in the
     * Left Drawer.
     */
    private class CreatedUpdates extends AsyncTask<List<String>, Void, String> {

        /**
         * Query Firebase for Event objects based on given IDs and populates
         * the eventsCreated list.
         *
         * @param params List of Event IDs
         * @return String "Executed" on success
         */
        @Override
        protected String doInBackground(List<String>... params) {
            for (String eventID : (List<String>) params[0]) {
                // Query Firebase for the Event object based on given ID
                final Firebase ref = new Firebase(Constants.FIREBASE_URL).child("events").child(eventID);
                ref.addListenerForSingleValueEvent(new ValueEventListener() {
                    /**
                     * Queries for the Events with given EventIDs and initializes
                     * the local created event list.
                     *
                     * @param snapshot Object representing the current state of the database
                     *                     at the given reference node
                     */
                    @Override
                    public void onDataChange(DataSnapshot snapshot) {
                        Event eventToAdd = snapshot.getValue(Event.class);
                        if (eventToAdd != null && eventToAdd.getEventTime().after(new Date()))
                            createdEventList.add(eventToAdd);
                    }

                    /**
                     * Displays an informative message to the user if unable to complete request.
                     * @param firebaseError error raised by Firebase
                     */
                    @Override
                    public void onCancelled(FirebaseError firebaseError) {
                        Toast.makeText(MainActivity.this, "Unable to connect.", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "error: " + firebaseError.getMessage());
                    }
                });
            }
            return "Executed";
        }

        /**
         * On the completion of the asynchronous query, set up left drawer
         * navigation lists with Events Created
         *
         * @param result Result message from previous task
         */
        @Override
        protected void onPostExecute(String result) {
            Log.i(TAG, "LEFT DRAWER EVENT LIST: " + createdEventList.toString());
            setupNavLists(R.id.created_list);
        }

        /**
         * Displays the Events Created list in the left drawer
         *
         * @param elementId: id of list to be populated
         */
        private void setupNavLists(int elementId) {
            ArrayAdapter<Event> navAdapter = new ArrayAdapter<Event>(MainActivity.this,
                    android.R.layout.simple_list_item_1, createdEventList);
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

            // Sets up a listener for the created list; on click, the user will be
            // redirected to the Event's detail page.
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


