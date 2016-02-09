package cse403.blast.Support;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import cse403.blast.R;


/**
 * Created by Sheen on 2/6/16.
 * This class is a fragment that allows a pop-up appearance for the time picker
 */
public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener onTimeSetListener;
    TimePickerDialog dialog;

    /**
    Returns a new instance of the TPFragment
     */
    public static TimePickerFragment newInstance(Date date, TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        TimePickerFragment pickerFragment = new TimePickerFragment();
        pickerFragment.setOnDateSetListener(onTimeSetListener);

        //Pass the date in a bundle.
        Bundle bundle = new Bundle();
        bundle.putSerializable("Time", date);
        pickerFragment.setArguments(bundle);
        return pickerFragment;
    }

    @Override
    /**
     * Initializing picker with the bundled date
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        Date initialDate = (Date) getArguments().getSerializable("Time");
        int[] hourMinute = ymdTripleFor(initialDate);
        dialog = new TimePickerDialog(getActivity(), onTimeSetListener,
                hourMinute[0], hourMinute[1], false);
        return dialog;
    }

    // Attaches listener
    private void setOnDateSetListener(TimePickerDialog.OnTimeSetListener listener) {
        this.onTimeSetListener = listener;
    }

    /**
     * Converting given date to an int[]
     * @param date
     * @return
     */
    private int[] ymdTripleFor(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return new int[]{cal.get(Calendar.HOUR), cal.get(Calendar.MINUTE)};
    }


//
//    private TimePickerDialog.OnTimeSetListener getOnTimeSetListener() {
//        return new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                EditText tackTime = (EditText) findViewById(R.id.tackTime);
//                tackTime.setText(DateFormatter.getTime(hourOfDay, minute));
//            }
//        };
//    }



}