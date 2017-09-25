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

/**
 * Created by sappling on 9/24/2017.
 * Compare as if the names were last, title first middle suffix
 */
public class PersonNameComparator implements Comparator<Person>{
    @Override
    public int compare(Person p1, Person p2) {
        int result = p1.getSurname().compareTo(p2.getSurname());
        if (result == 0) {
            result = p1.getStartingNames().compareTo(p2.getStartingNames());
            if (result == 0) {
                result = p1.getSuffix().compareTo(p2.getSuffix());
            }
        }
        return result;
    }
}
