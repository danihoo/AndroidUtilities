package de.danihoo94.www.androidutilities.util;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

@SuppressWarnings({"unused", "RedundantSuppression"})
public class ParseUtils {

    public static String parseNoDecimal(double d) {
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormatter.setMinimumFractionDigits(0);
        numberFormatter.setMaximumFractionDigits(0);
        numberFormatter.setGroupingUsed(true);
        return numberFormatter.format(d);
    }

    public static String parseTwoDecimals(double d, boolean grouping) {
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault());
        numberFormatter.setMinimumFractionDigits(2);
        numberFormatter.setMaximumFractionDigits(2);
        numberFormatter.setGroupingUsed(grouping);
        return numberFormatter.format(d);
    }

    public static double parseTwoDecimals(String s) throws ParseException {
        NumberFormat numberFormatter = NumberFormat.getNumberInstance(Locale.getDefault());
        Number number = numberFormatter.parse(s);
        return (number != null) ? number.doubleValue() : 0;
    }

    private static String parseDbDate(int year, int month, int day) {
        String m = (month < 10 ? "0" : "") + month;
        String d = (day < 10 ? "0" : "") + day;
        return year + "-" + m + "-" + d;
    }

    public static String parseDbDate(Calendar c) {
        int y = c.get(Calendar.YEAR);
        int m = c.get(Calendar.MONTH) + 1;
        int d = c.get(Calendar.DAY_OF_MONTH);

        return parseDbDate(y, m, d);
    }

    public static Calendar parseDbDate(String date) {
        int year = Integer.parseInt(date.substring(0, 4));
        int month = Integer.parseInt(date.substring(5, 7)) - 1;
        int day = Integer.parseInt(date.substring(8, 10));

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0);
        c.set(year, month, day);
        return c;
    }

    public static String parseLocalDate(String dbDate) {
        int year = Integer.parseInt(dbDate.substring(0, 4));
        int month = Integer.parseInt(dbDate.substring(5, 7)) - 1;
        int day = Integer.parseInt(dbDate.substring(8, 10));

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0);
        c.set(year, month, day);

        return parseLocalDate(c);
    }

    @SuppressLint("SimpleDateFormat")
    public static String parseFromLocalDate(String localDate) {
        try {
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
            Date d = Objects.requireNonNull(df.parse(localDate));

            DateFormat dfDb = new SimpleDateFormat("yyyy-MM-dd");
            return dfDb.format(d);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return "";
    }

    public static String parseLocalDate(Calendar c) {
        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.getDefault());
        return df.format(c.getTime());
    }

    public static String parseLocalDate(int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(0);
        c.set(year, month, day);

        return parseLocalDate(c);
    }
}