package org.appling.famtree.util;

import org.appling.famtree.gedcom.Person;

import java.util.Comparator;
import java.util.Date;

/**
 * Created by sappling on 8/27/2017.
 */
public class PersonBirthdayComparator implements Comparator<Person> {
    DateComparator dc = new DateComparator();
    @Override
    public int compare(Person p1, Person p2) {
        Date p1Birth = null;
        if (p1 != null) {
            p1Birth = p1.getBirthDate();
        }

        Date p2Birth = null;
        if (p2 != null) {
            p2Birth = p2.getBirthDate();
        }

        if (p1Birth == null && p2Birth == null) {
            return 0;
        } if (p1Birth != null && p2Birth == null) { // consider any day earlier than unknown
            return -1;
        } else if (p2Birth != null && p1Birth == null) {
            return 1;
        }
        else return dc.compare(p1Birth, p2Birth);
    }
}
