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

import cse403.blast.Support.DatePickerFragment;
import cse403.blast.Support.TimePickerFragment;

/**
 * CreateEventActivity allows a user to create a new Event. After inputting valid data for each
 * of the Event fields, the user clicks "Blast It!" to publish the Event and make it visible to
 * other users within the appropriate location radius. The Event is then displayed under the device
 * user's "Blasts You Created" section of the main page's drawer.
 */
public class CreateEventActivity extends AppCompatActivity {

    private final String TAG = "CreateEventActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_event);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        Intent createEventIntent = getIntent();

        // sets up the listener for displaying the date picker
        EditText dateText = (EditText) findViewById(R.id.date);
        dateText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    showDatePickerDialog(v);
                return false;
            }
        });

        // sets up the listener for displaying the time picker
        EditText timeText = (EditText) findViewById(R.id.time);
        timeText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (MotionEvent.ACTION_UP == event.getAction())
                    showTimePickerDialog(v);
                return false;
            }
        });
        
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
     * Displays the time picker
     *
     * @param v The View to display to
     */
    private void showTimePickerDialog(View v) {
        TimePickerFragment newFragment = TimePickerFragment.newInstance(
                new Date(Calendar.getInstance().getTimeInMillis()), getTimeSetListener());
        newFragment.show(getSupportFragmentManager(), "timePicker");
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
        }
    };

    /**
     * Gets the time picker
     *
     * @return DialogFragment
     */
    private DialogFragment getTimePickerDialog() {
        DialogFragment newFragment = TimePickerFragment.newInstance(
                new Date(Calendar.getInstance().getTimeInMillis()), getTimeSetListener());
        return newFragment;
    }

    /**
     * Time picker on set listener
     *
     * @return TimePickerDialog.OnTimeSetListener
     */
    // TODO: Format time to not be in military
    private TimePickerDialog.OnTimeSetListener getTimeSetListener() {
        return new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                EditText tackTime = (EditText) findViewById(R.id.time);
                tackTime.setText("" + hourOfDay + ":" + minute);
            }
        };
    }


}
