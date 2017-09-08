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
            personMap.put(entry.getKey(), person);
            frameMap.put(entry.getKey(), frame);
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
