package cse403.blast;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.firebase.client.Firebase;

import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;

import cse403.blast.Data.Constants;
import cse403.blast.Model.Event;
import cse403.blast.Model.User;
import cse403.blast.Support.DatePickerFragment;
import cse403.blast.Support.TimePickerFragment;


/**
 * CreateEventActivity allows a user to create a new Event. After inputting valid data for each
 * of the Event fields, the user clicks "Blast It!" to publish the Event and make it visible to
 * other users within the appropriate location radius. The Event is then displayed under the device
 * user's "Blasts You Created" section of the main page's drawer.
 */
public class CreateEventActivity extends AppCompatActivity {
    private Button submitButton;
    private Button cancelButton;
    private EditText titleText;
    private EditText descText;
    private EditText dateText;
    private EditText timeText;
    private EditText locText;
    private EditText limitText;
    private int userDay;
    private int userMonth;
    private int userYear;
    private int userHour;
    private int userMin;

    private final String TAG = "CreateEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent createEventIntent = getIntent();

        submitButton = (Button) findViewById(R.id.create_submit_button);
        cancelButton = (Button) findViewById(R.id.create_cancel_button);
        titleText = (EditText) findViewById(R.id.create_title);
        descText = (EditText) findViewById(R.id.create_description);
        locText = (EditText) findViewById(R.id.create_location);
        limitText = (EditText) findViewById(R.id.create_limit);

        // sets up the listener for displaying the date picker
        dateText = (EditText) findViewById(R.id.create_date);
        dateText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    showDatePickerDialog(v);
                return false;
            }
        });

        // sets up the listener for displaying the time picker
        timeText = (EditText) findViewById(R.id.create_time);
        timeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    showTimePickerDialog(v);
                return false;
            }
        });

        // adds validation listeners
        addFieldValidationListeners();

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (createEventIntent.getBooleanExtra("edit", true)) {
            Event event = (Event) createEventIntent.getSerializableExtra("event");
            // TODO: prepopulate fields

            // Disable and enable certain parts
            titleText.setEnabled(false);
            locText.setEnabled(false);
            cancelButton.setVisibility(View.VISIBLE);
            cancelButton.setText(getString(R.string.create_cancel_button));
            submitButton.setText(getString(R.string.create_save_button));

        } else {
            // enable non-editing fields
            titleText.setEnabled(true);
            locText.setEnabled(true);
            cancelButton.setVisibility(View.GONE);
            submitButton.setText(getString(R.string.create_blast_button));
        }
        Log.i(TAG, "Done creating page");
    }

    // VALIDATION METHODS

    // sets up the listeners for all the fields
    private void addFieldValidationListeners() {
        addSubmitButtonClickListener();
        addTitleFocusListener();
        addDescriptionFocusListener();
        addDateFocusListener();
        addTimeFocusListener();
        addLocationFocusListener();
        addLimitFocusListener();
    }

    /**
     * Verifies field input based on the view's field id
     *
     * @param v The view that the field id comes from
     * @return true if the field passes verification, false otherwise
     */
    private boolean verify(View v) {
        int id = v.getId();
        switch (id) {
            case R.id.create_submit_button :   // checks that user has filled in all fields
                return (!isEmpty(titleText) && !isEmpty(descText) && !isEmpty(dateText) &&
                        !isEmpty(timeText) && !isEmpty(locText) && !isEmpty(limitText));
            case R.id.create_title :
                if (isEmpty(titleText)) {
                    notifyUser("Title is empty");
                    return false;
                }
                return true;
            case R.id.create_description :
                if (isEmpty(descText)) {
                    notifyUser("Description is empty");
                    return false;
                }
                return true;
            case R.id.create_date :
                if (isEmpty(dateText)) {
                    notifyUser("Date is empty");
                    return false;
                }
                return true;
            case R.id.create_time :
                if (isEmpty(timeText)) {
                    notifyUser("Time is empty");
                    return false;
                }
                return true;
            case R.id.create_location :
                if (isEmpty(locText)) {
                    notifyUser("Location is empty");
                    return false;
                }
                return true;
            case R.id.create_limit :   // check that limit is not empty and that it is an integer >= 1
                if (isEmpty(limitText)) {
                    notifyUser("Limit is empty");
                    return false;
                } else {
                    // check that input is a number
                    String input = limitText.getText().toString();
                    for (int i = 0; i < input.length(); i++) {
                        if (!Character.isDigit(input.charAt(i))) {
                            notifyUser("Limit needs to be a number");
                            return false;
                        }
                    }
                    // input is a number, check if number entered is valid
                    int userEnteredLimit = Integer.parseInt(input);
                    if (userEnteredLimit < 1) {
                        notifyUser("Limit needs to be at least 1");
                        return false;
                    }
                }
                return true;
            default :   // should not hit default case, so return false if we do
                return false;
        }
    }

    /**
     * Checks if the user field has been filled in
     *
     * @param field The field to check
     * @return true if the field is empty or just whitespace, false otherwise
     */
    private boolean isEmpty(EditText field) {
        String text = field.getText().toString();
        if (text == null) {
            return true;
        }
        return text.trim().length() == 0;
    }

    /**
     * Displays a temporary message at the bottom of the screen
     *
     * @param message The message to be displayed
     */
    private void notifyUser(String message) {
        Toast.makeText(CreateEventActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    // adds click listener to submitButton to trigger verification and add event to database
    private void addSubmitButtonClickListener() {
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO display something helpful if verify fails
                // succeeds if the user has filled in all fields
                if (verify(v)) {
                    Intent mainActivityIntent = new Intent(CreateEventActivity.this, MainActivity.class);
                    startActivity(mainActivityIntent);
                    addEvent();
                }
            }
        });
    }

    // adds focus change listener to titleText to trigger verification
    private void addTitleFocusListener() {
        titleText.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify(v);
                }
            }
        });
    }

    // adds focus change listener to descText to trigger verification
    private void addDescriptionFocusListener() {
        descText.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify(v);
                }
            }
        });
    }

    // adds focus change listener to dateText to trigger verification
    private void addDateFocusListener() {
        dateText.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify(v);
                }
            }
        });
    }

    // adds focus change listener to timeText to trigger verification
    private void addTimeFocusListener() {
        timeText.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify(v);
                }
            }
        });
    }

    // adds focus change listener to locText to trigger verification
    private void addLocationFocusListener() {
        locText.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify(v);
                }
            }
        });
    }

    // adds focus change listener to limitText to trigger verification
    private void addLimitFocusListener() {
        limitText.setOnFocusChangeListener(new OnFocusChangeListener() {

            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    verify(v);
                }
            }
        });
    }

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
            EditText tackDate = (EditText) findViewById(R.id.create_date);
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
            EditText tackTime = (EditText) findViewById(R.id.create_time);
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

    // DATABASE METHODS

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
        Event userEvent = new Event(new User("1234", new HashSet<Event>(), new HashSet<Event>()), userEnteredTitle, userEnteredDesc,
                userEnteredLoc, userEnteredLimit, userEnteredDate);

        // Generate unique ID for event
        Firebase eventRef = ref.child("events");
        Firebase newEventRef = eventRef.push();

        // Add event to DB
        newEventRef.setValue(userEvent);

        // String eventId = newEventRef.getKey();
    }
}