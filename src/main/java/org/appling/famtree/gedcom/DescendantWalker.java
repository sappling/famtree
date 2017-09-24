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

package org.appling.famtree.gedcom;

import java.util.Iterator;
import java.util.List;

/**
 * Created by sappling on 8/15/2017.
 * Walk the tree in depth first order: Individual, spouse, children with spouse, next spouse, children with that spouse...
 *
 */
public class DescendantWalker {
    private PersonAction action;
    private Person stopPerson;

    public DescendantWalker(PersonAction personAction) {
        action = personAction;
    }

    public void setStopPerson(Person stopPerson) {
        this.stopPerson = stopPerson;
    }

    public void walk(Person person, int generation, int limit) throws GedException {
        walk(person, 0, limit, false);
    }

    private void walk(Person person, int generation, int limit, boolean moreChildren) throws GedException {
        action.act(person, 0, generation);

        List<Family> families = person.getFamiliesWhereSpouse();
        int spouseCount = 1;
        Person lastSpouse = null;
        boolean shouldStop = person.equals(stopPerson);
        for (Family family : families) {
            Person spouse = family.getOtherSpouse(person);
            if (spouse != null) {
                lastSpouse = spouse;
                action.act(spouse, spouseCount++, generation);
            }

            if (generation < limit) {
                if ((stopPerson == null) || (!shouldStop)) {
                    Iterator<Person> it = family.getChildren().iterator();

                    while (it.hasNext()) {
                        Person child = it.next();
                        walk(child, generation + 1, limit, it.hasNext());
                    }
                }
            }
        }
        if (!moreChildren || shouldStop) {
            if (lastSpouse != null) {
                action.lastChildOrSpouse(lastSpouse);
            } else {
                action.lastChildOrSpouse(person);
            }
        }
    }
}
