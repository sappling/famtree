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

import org.appling.famtree.util.PersonBirthdayComparator;
import org.gedcom4j.model.IndividualReference;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by sappling on 8/13/2017.
 */
public class Family {
    private static final PersonBirthdayComparator bCompare = new PersonBirthdayComparator();
    private org.gedcom4j.model.Family gfamily;


    public Family(org.gedcom4j.model.Family gfamily) {
        this.gfamily = gfamily;
    }

    @Nullable
    public Person getOtherSpouse(Person person) throws GedException {

        if (person.equals(getHusband())) {
            return getWife();
        } else if (person.equals(getWife())) {
            return getHusband();
        }
        throw new GedException("Can't find spouce for "+person);
    }

    @Nullable
    public Person getHusband() throws GedException {
        Person result = null;
        IndividualReference husband = gfamily.getHusband();
        if (husband != null) {
            result = PersonRegistry.instance().getPerson(Person.getCleanId(husband.getIndividual().getXref()));
        }
        return result;
    }

    public Person getWife() throws GedException {
        Person result = null;
        IndividualReference wife = gfamily.getWife();
        if (wife != null) {
            result = PersonRegistry.instance().getPerson(Person.getCleanId(wife.getIndividual().getXref()));
        }
        return result;
    }

    public List<Person> getChildren() throws GedException {
        List<IndividualReference> gChildren = gfamily.getChildren();
        ArrayList<Person> results = new ArrayList<>();

        if (gChildren != null) {
            for (IndividualReference gChild : gChildren) {
                String id = Person.getCleanId(gChild.getIndividual().getXref());
                PersonRegistry pr = PersonRegistry.instance();
                results.add(pr.getPerson(id));
            }
        }

        results.sort(bCompare);

        return results;
    }
}
