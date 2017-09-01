package org.appling.famtree.util;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by sappling on 8/27/2017.
 */
public class DateComparator implements Comparator<Date> {
    @Override
    /*
     * returns a negative integer, zero, or a positive integer as the
     *         first argument is less than, equal to, or greater than the
     *         second.
     */
    public int compare(Date o1, Date o2) {
        if (o1.getTime() == o2.getTime()) {
            return 0;
        } else if (o1.getTime() < o2.getTime()) {
            return -1;
        } else {
            return 1;
        }
    }
}
