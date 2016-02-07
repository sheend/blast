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
 */
public class TimePickerFragment extends DialogFragment {

    private TimePickerDialog.OnTimeSetListener onTimeSetListener;

    public static TimePickerFragment newInstance(Date date, TimePickerDialog.OnTimeSetListener onTimeSetListener) {
        TimePickerFragment pickerFragment = new TimePickerFragment();
        pickerFragment.setOnDateSetListener(onTimeSetListener);

        //Pass the date in a bundle.
        Bundle bundle = new Bundle();
        bundle.putSerializable("coolTime", date);
        pickerFragment.setArguments(bundle);
        return pickerFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        Date initialDate = (Date) getArguments().getSerializable("coolTime");
        int[] hourMinute = ymdTripleFor(initialDate);
        TimePickerDialog dialog = new TimePickerDialog(getActivity(), onTimeSetListener,
                hourMinute[0], hourMinute[1], false);
        return dialog;
    }

    private void setOnDateSetListener(TimePickerDialog.OnTimeSetListener listener) {
        this.onTimeSetListener = listener;
    }

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