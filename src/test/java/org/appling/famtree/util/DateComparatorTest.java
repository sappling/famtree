package org.appling.famtree.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.GregorianCalendar;

import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * Created by sappling on 8/27/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class DateComparatorTest {

    @Test
    public void testCompare() {
        DateComparator dc = new DateComparator();
        assertTrue(dc.compare(new GregorianCalendar(2017, 7,20).getTime(), new GregorianCalendar(2017, 7,27).getTime()) < 0 );
        assertTrue(dc.compare(new GregorianCalendar(2018, 7,20).getTime(), new GregorianCalendar(2017, 7,27).getTime()) > 0 );
        assertTrue(dc.compare(new GregorianCalendar(2017, 7,20).getTime(), new GregorianCalendar(2017, 7,20).getTime()) == 0 );
    }
}
