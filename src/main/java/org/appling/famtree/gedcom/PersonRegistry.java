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

import org.appling.famtree.graph.PersonFrame;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.model.Individual;
import org.jetbrains.annotations.NotNull;

import java.util.*;

/**
 * Created by sappling on 8/15/2017.
 */
public class PersonRegistry {
    private static PersonRegistry singleton = new PersonRegistry();
    private HashMap<String, Person> personMap = new HashMap<>();
    private HashMap<String, PersonFrame> frameMap = new HashMap<>();

    private PersonRegistry() {
    }

    public static PersonRegistry instance() {
        return singleton;
    }

    public void setGedcom(Gedcom gedcom) {
        Map<String, Individual> indMap = gedcom.getIndividuals();
        for (Map.Entry<String, Individual> entry : indMap.entrySet()) {
            Person person = new Person(entry.getValue());
            PersonFrame frame = new PersonFrame(person);
            person.setFrame(frame);
            personMap.put(person.getCleanId(), person);
            frameMap.put(person.getCleanId(), frame);
        }
    }

    @NotNull
    public Person getPerson(String id) throws GedException {
        Person result = personMap.get(id);
        if (result == null) {
            throw new GedException("Unknown person ID:"+id);
        }
        return result;
    }

    public List<Person> getAllPeople() {
        return Collections.unmodifiableList(new ArrayList<Person>(personMap.values()));
    }

    public PersonFrame getFrame(String id)  throws GedException {
        PersonFrame result = frameMap.get(id);
        if (result == null) {
            throw new GedException("Unknown person ID:"+id);
        }
        return result;
    }

    public List<PersonFrame> getAllFrames() {
        return Collections.unmodifiableList(new ArrayList<PersonFrame>(frameMap.values()));
    }
}
