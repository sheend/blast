package cse403.blast.Support;

/**
 * Created by Sheen on 2/25/16.
 */
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class DateDifference {

    public static DateResult getDifferenceResult(Date today, Date other) {

        long diff = other.getTime() - today.getTime();
        //long duration  = endDate.getTime() - startDate.getTime();

        long diffInMinutes = diff / (60 * 1000) % 60;
        long diffInHours = diff / (60 * 60 * 1000);
        int diffInDays = (int) (diff / (1000 * 60 * 60 * 24));

        if (diffInHours >= 24) {
            diffInDays += diffInHours / 24;
            diffInHours %= 24;
        }
        if (diffInMinutes >= 60) {
            diffInHours += diffInMinutes / 60;
            diffInMinutes %= 60;
        }

//        //long diffInSeconds = TimeUnit.MILLISECONDS.toSeconds(diff);
//        long diffInMinutes = TimeUnit.MILLISECONDS.toMinutes(diff);
//        long diffInHours = TimeUnit.MILLISECONDS.toHours(diff);
//        long diffInDays = TimeUnit.MILLISECONDS.toDays(diff);
        return new DateResult(diffInDays, diffInHours, diffInMinutes);

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

        //last check
        if (result.equals(" 0 m"))
            return "Now";
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