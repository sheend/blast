package cse403.blast.Support;

import android.text.InputFilter;
import android.text.Spanned;

/**
 * Created by Carson on 3/3/16.
 *
 * Filter that restricts the numerical value entered in an EditText to be between a specified min
 * and max value (inclusive).
 */
public class MinMaxInputFilter implements InputFilter {
    private int min;
    private int max;

    /**
     * Constructs a new filter with the given bounds
     *
     * @param min The lowest acceptable value (inclusive)
     * @param max The greatest acceptable value (inclusive)
     */
    public MinMaxInputFilter(int min, int max) {
        this.min = min;
        this.max = max;
    }

    /**
     * Applies the filter to the user's input
     *
     * @param source Existing user input
     * @param start Start of existing input
     * @param end End of existing input
     * @param dest New user input
     * @param dstart Start of new input
     * @param dend End of new input
     * @return The filtered input
     */
    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        try {
            // Remove the string out of destination that is to be replaced
            String newVal = dest.toString().substring(0, dstart) + dest.toString().substring(dend, dest.toString().length());
            // Add the new string in
            newVal = newVal.substring(0, dstart) + source.toString() + newVal.substring(dstart, newVal.length());
            int input = Integer.parseInt(newVal);
            if (isInRange(min, max, input))
                return null;
        } catch (NumberFormatException nfe) { }
        return "";
    }

    /**
     * Checks if the input is within the bounds (inclusive)
     *
     * @param a Min bound
     * @param b Max bound
     * @param c Input
     * @return true if c >= a && c <= b, false otherwise
     */
    private boolean isInRange(int a, int b, int c) {
        return b > a ? c >= a && c <= b : c >= b && c <= a;
    }
}
