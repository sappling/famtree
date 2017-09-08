package org.appling.famtree.graph;

import org.appling.famtree.gedcom.Person;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.ListIterator;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.mock;

/**
 * Created by sappling on 8/27/2017.
 */
@RunWith(MockitoJUnitRunner.class)
public class DescandentLayoutTest {

    @Test
    public void testGetLastSpouseNormal() {
        Person person = mock(Person.class);

        ArrayList<PersonFrame> frames = new ArrayList<>();
        // test data,  one spouse, then two spouses, then no spouse, then one spouse at end - total of 8 frames
        for (int i=0; i<8; i++) {
            PersonFrame frame = new PersonFrame(person);
            // use left space as identifier since all sharing same mock person.  Have to be > 20 because there is a min space
            frame.setLeftSpace(100 + i);
            frames.add(frame);
        }
        Iterator<PersonFrame> it = frames.iterator();
        it.next().setSpouseCount(0);    // 100
        it.next().setSpouseCount(1);    // 101
        it.next().setSpouseCount(0);    // 102
        it.next().setSpouseCount(1);    // 103
        it.next().setSpouseCount(2);    // 104
        it.next().setSpouseCount(0);    // 105
        it.next().setSpouseCount(0);    // 106
        it.next().setSpouseCount(1);    // 107

        ListIterator<PersonFrame> li = frames.listIterator();
        PersonFrame descendant = li.next();
        assertThat(descendant.getLeftSpace(), equalTo(100));

        PersonFrame spouse = DescendantLayout.getLastSpouse(li);
        assertThat(spouse.getLeftSpace(), equalTo(101));

        descendant = li.next();
        spouse = DescendantLayout.getLastSpouse(li);
        assertThat(spouse.getLeftSpace(), equalTo(104));

        descendant = li.next();
        spouse = DescendantLayout.getLastSpouse(li);
        assertThat(spouse, nullValue());

        descendant = li.next();
        spouse = DescendantLayout.getLastSpouse(li);
        assertThat(spouse.getLeftSpace(), equalTo(107));
    }
}
