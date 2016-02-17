package cse403.blast;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.app.TimePickerDialog;
import android.app.DatePickerDialog;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import cse403.blast.Data.Constants;
import cse403.blast.Model.User;
import cse403.blast.Model.Event;
import cse403.blast.Support.DatePickerFragment;
import cse403.blast.Support.TimePickerFragment;

import com.firebase.client.Firebase;
import cse403.blast.Model.*;


/**
 * CreateEventActivity allows a user to create a new Event. After inputting valid data for each
 * of the Event fields, the user clicks "Blast It!" to publish the Event and make it visible to
 * other users within the appropriate location radius. The Event is then displayed under the device
 * user's "Blasts You Created" section of the main page's drawer.
 */
public class CreateEventActivity extends AppCompatActivity {
    EditText titleText;
    EditText descText;
    EditText dateText;
    EditText timeText;
    EditText limitText;
    EditText locText;
    int userDay;
    int userMonth;
    int userYear;
    int userHour;
    int userMin;

    private final String TAG = "CreateEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent createEventIntent = getIntent();

        titleText = (EditText) findViewById(R.id.title);
        descText = (EditText) findViewById(R.id.description);
        limitText = (EditText) findViewById(R.id.limit);
        locText = (EditText) findViewById(R.id.location);

        // sets up the listener for displaying the date picker
        dateText = (EditText) findViewById(R.id.date);
        dateText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    showDatePickerDialog(v);
                return false;
            }
        });

        // sets up the listener for displaying the time picker
        timeText = (EditText) findViewById(R.id.time);
        timeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    showTimePickerDialog(v);
                return false;
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Toggle create event display between editing and creation
        Button cancelButton = (Button) findViewById(R.id.cancel_button);
        Button submitButton = (Button) findViewById(R.id.submit_button);
        EditText title = (EditText) findViewById(R.id.title);
        EditText desc = (EditText) findViewById(R.id.description);
        EditText time = (EditText) findViewById(R.id.time);
        EditText location = (EditText) findViewById(R.id.location);
        EditText limit = (EditText) findViewById(R.id.limit);

        // sets up listener for verifying all event fields
//        submitButton.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                // check that all fields have input in them
//            }
//        });

        // set listeners for each text field to call their respective validation methods
        // these listeners are called when the user toggles out of a field

        /*
        TODO: Look at git ticket
        // TODO: make this boolean work
        if (createEventIntent.getBooleanExtra("edit", true)) {
            Event event = (Event) createEventIntent.getSerializableExtra("event");
            // TODO: prepopulate fields

            // Disable and enable certain parts
            title.setEnabled(false);
            location.setEnabled(false);
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText("Cancel Blast :(");
            submitButton.setText("Save Blast");

        } else {
            // enable non-editing fields
            title.setEnabled(true);
            location.setEnabled(true);
            cancelButton.setVisibility(View.GONE);
            submitButton.setText("Blast It!");
        }
        Log.i(TAG, "Done creating page");
        */

        addButtonClickListener();
    }

    // VALIDATION METHODS

    // submit button final validation
//    private Button.onClick
    // this will call the create event component if validation passes

    // individual field validation

    // DIALOG METHODS

    /**
     * Displays the date picker
     *
     * @param v The View to display to
     */
    // TODO: Limit the dates that a user can choose from on the calendar
    private void showDatePickerDialog(View v) {
        DatePickerFragment newFragment = DatePickerFragment.newInstance(
                new Date(Calendar.getInstance().getTimeInMillis()), dateSetListener);
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    /**
     * Date picker on set listener
     */
    // TODO: Use Calendar set to create a reliable, format independent date
    private DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            EditText tackDate = (EditText) findViewById(R.id.date);
            tackDate.setText((monthOfYear + 1) + "/" + dayOfMonth + "/" + year);
            Log.i("Tag", "set");
            // make the time picker display after choosing a date
            getTimePickerDialog().show(getSupportFragmentManager(), "timePicker");
            userYear = year;
            userMonth = monthOfYear;
            userDay = dayOfMonth;
        }
    };

    /**
     * Displays the time picker
     *
     * @param v The View to display to
     */
    private void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(
                new Date(Calendar.getInstance().getTimeInMillis()), timeSetListener);
        newFragment.show(getSupportFragmentManager(), "timePicker");
    }

    /**
     * Time picker on set listener
     *
     * @return TimePickerDialog.OnTimeSetListener
     */
    // TODO: Format time to not be in military
    private TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
        @Override
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            EditText tackTime = (EditText) findViewById(R.id.time);
            tackTime.setText("" + hourOfDay + ":" + minute);
            userHour = hourOfDay;
            userMin = minute;
        }
    };


    /**
     * Gets the time picker
     * This helps make the user interaction simpler by directly opening time after date
     *
     * @return DialogFragment
     */
    private DialogFragment getTimePickerDialog() {
        DialogFragment newFragment = TimePickerFragment.newInstance(
                new Date(Calendar.getInstance().getTimeInMillis()), timeSetListener);
        return newFragment;
    }

    private void addButtonClickListener() {
        Button submitButton = (Button) findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent mainActivityIntent = new Intent(CreateEventActivity.this, MainActivity.class);
                startActivity(mainActivityIntent);
                addEvent();
            }
        });
    }

    /**
     * Adds event to the Events firebase database containing information for:
     * unique date ID
     * event title
     * description
     * guest list limit
     * location
     * date
     */
    public void addEvent() {
        // Get the reference to the root node in Firebase
        Firebase ref = new Firebase(Constants.FIREBASE_URL);

        // Get user-entered data: title, description, limit, location
        String userEnteredTitle = titleText.getText().toString();
        String userEnteredDesc = descText.getText().toString();
        int userEnteredLimit = Integer.parseInt(limitText.getText().toString());
        String userEnteredLoc = locText.getText().toString();

        // Get user-entered date
        Calendar calendar = Calendar.getInstance();
        calendar.set(userYear, userMonth, userDay, userHour, userMin);
        Date userEnteredDate = calendar.getTime();

        // Log string for entered date
        Log.i("TestMyDate", userEnteredDate.toString());

        // Create event object using user-submitted data
        Event userEvent = new Event(new User("1234"), userEnteredTitle, userEnteredDesc, userEnteredLoc, userEnteredLimit, userEnteredDate);

        // Generate unique ID for event
        Firebase eventRef = ref.child("events");
        Firebase newEventRef = eventRef.push();

        // Add event to DB
        newEventRef.setValue(userEvent);

        // String eventId = newEventRef.getKey();
    }
}
