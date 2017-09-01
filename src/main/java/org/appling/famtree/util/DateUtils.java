package org.appling.famtree.util;

import org.jetbrains.annotations.Nullable;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by sappling on 8/27/2017.
 */
public class DateUtils {
    private static SimpleDateFormat gedFormat = new SimpleDateFormat("dd MMM yyyy");
    private static SimpleDateFormat normFormat = new SimpleDateFormat("MM/dd/yyyy");

    @Nullable
    public static Date dateFromGedDate(String datesString) {
        Date result = null;
        if (datesString != null) {
            try {
                return gedFormat.parse(datesString);
            } catch (ParseException e) {} // intentionally ignore and use default value
        }
        return result;
    }

    public static String normStringFromDate(@Nullable Date date) {
        String result = "Unknown";
        if (date != null) {
            result = normFormat.format(date);
        }
        return result;
    }

}
