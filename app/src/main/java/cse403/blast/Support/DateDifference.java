package cse403.blast.Support;

/**
 * Created by Sheen on 2/25/16.
 */
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

public class DateDifference {

    public static DateResult getDifferenceResult(Date today, Date other) {

        long diff = today.getTime() - other.getTime();

        long diffSeconds = diff / 1000 % 60;
        long diffMinutes = diff / (60 * 1000) % 60;
        long diffHours = diff / (60 * 60 * 1000) % 24;
        long diffDays = diff / (24 * 60 * 60 * 1000);

        return new DateResult(diffDays, diffHours, diffMinutes);

    }

    public static String getDifferenceString(Date today, Date other) {
        DateResult dateResult = getDifferenceResult(today, other);
        String result = "";
        if (dateResult.day != 0) {
            result += dateResult.day + " d";
        }
        if (dateResult.hour != 0) {
            result += " " + dateResult.hour + " h";
        }
        result += " " + dateResult.minute + " m";
        return result;
    }

    static class DateResult {
        long day;
        long hour;
        long minute;

        public DateResult(long d, long h, long m) {
            day = d;
            hour = h;
            minute = m;
        }
    }

}