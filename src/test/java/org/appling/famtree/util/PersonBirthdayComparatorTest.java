package org.appling.famtree.util;

import org.appling.famtree.gedcom.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.Date;
import java.util.GregorianCalendar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Created by sappling on 8/27/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class PersonBirthdayComparatorTest {
    private static final Date nyd2017 = new GregorianCalendar(2017, 0,1).getTime();
    private static final Date nyd2016 = new GregorianCalendar(2016, 0,1).getTime();
    private static final PersonBirthdayComparator c = new PersonBirthdayComparator();

    Person person2017;

    Person person2016;

    @Test
    public void testCompareNonNull() {
        Person person2016 = mock(Person.class);
        when(person2016.getBirthDate()).thenReturn(nyd2016);
        Person person2017 = mock(Person.class);
        when(person2017.getBirthDate()).thenReturn(nyd2017);

        assertTrue(c.compare(person2016, person2017) < 0);
        assertTrue(c.compare(person2017, person2016) > 0);
        assertTrue(c.compare(person2016, person2016) == 0);
    }

    @Test
    public void testCompareWithNulls() {
        Person person2016 = mock(Person.class);
        when(person2016.getBirthDate()).thenReturn(nyd2016);

        assertTrue(c.compare(person2016, null) < 0);
        assertTrue(c.compare(null, person2016) > 0);
        assertTrue(c.compare(null, null) == 0);
    }
}
