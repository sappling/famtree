/*
 * Copyright (c) 2017 Steve Appling
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
