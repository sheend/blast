package cse403.blast.Support;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * Created by Sheen on 2/6/16.
 * This class is a fragment that allows a pop-up appearance for the date picker
 */
public class DatePickerFragment extends DialogFragment {

    private DatePickerDialog.OnDateSetListener onDateSetListener;
    DatePickerDialog dialog;

    /**
    Returns a new instance of the DPFragment
     */
    public static DatePickerFragment newInstance(Date date, DatePickerDialog.OnDateSetListener onDateSetListener) {
        DatePickerFragment pickerFragment = new DatePickerFragment();
        pickerFragment.setOnDateSetListener(onDateSetListener);

        //Pass the date in a bundle.
        Bundle bundle = new Bundle();
        bundle.putSerializable("Date", date);
        pickerFragment.setArguments(bundle);
        return pickerFragment;
    }

    @Override
    /**
     * Initializing picker with the bundled date
     */
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);

        Date initialDate = (Date) getArguments().getSerializable("Date");
        int[] yearMonthDay = ymdTripleFor(initialDate);
        dialog = new DatePickerDialog(getActivity(), onDateSetListener,
                yearMonthDay[0], yearMonthDay[1], yearMonthDay[2]);
        return dialog;
    }

    // Attaches listener
    private void setOnDateSetListener(DatePickerDialog.OnDateSetListener listener) {
        this.onDateSetListener = listener;
    }

    /**
     * Converting given date to an int[]
     * @param date
     * @return
     */
    private int[] ymdTripleFor(Date date) {
        Calendar cal = Calendar.getInstance(Locale.US);
        cal.setTime(date);
        return new int[]{cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)};
    }
}