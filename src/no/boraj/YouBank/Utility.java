package no.boraj.YouBank;

import android.content.Context;

import java.util.Calendar;
import java.util.Date;

/**
 * Author: Børge André Jensen
 * Author URL: http://borgizzle.com/
 */
public class Utility {
    public static int dpToPx(Context context, int dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    public static String getStringFromDateDay(int date) {
        String strDay = "" + date;

        if (strDay.charAt(strDay.length() - 1) == '1') {
            return strDay + "st";
        } else if (strDay.charAt(strDay.length() - 1) == '2') {
            return strDay + "nd";
        } else if (strDay.charAt(strDay.length() - 1) == '3') {
            return strDay + "rd ";
        }

        return strDay + "th";
    }

    public static String dateToStr(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(date.getTime());
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);

        String[] strMonths = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        return String.format("%s %s, %s", strMonths[month], dayOfMonth, year);
    }
}
