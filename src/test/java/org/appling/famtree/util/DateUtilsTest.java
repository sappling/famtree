package org.appling.famtree.util;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.GregorianCalendar;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * Created by sappling on 8/27/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class DateUtilsTest {

    @Test
    public void testDateFromGedString() {
        assertThat(DateUtils.dateFromGedDate("27 Aug 2017"), equalTo(new GregorianCalendar(2017, 7,27).getTime()));
        assertThat(DateUtils.dateFromGedDate("27 Aug 1717"), equalTo(new GregorianCalendar(1717, 7,27).getTime()));
        assertThat(DateUtils.dateFromGedDate(""), nullValue());
        assertNull(DateUtils.dateFromGedDate(null));
    }

    @Test
    public void testNormStringFromDate() {
        assertThat(DateUtils.normStringFromDate(new GregorianCalendar(2017, 7,27).getTime()), equalTo("08/27/2017"));
        assertThat(DateUtils.normStringFromDate(new GregorianCalendar(2017, 11,1).getTime()), equalTo("12/01/2017"));
        assertThat(DateUtils.normStringFromDate(null), equalTo("Unknown"));
    }
}


