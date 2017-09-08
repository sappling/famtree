package org.appling.famtree.graph;

import org.appling.famtree.gedcom.GedException;
import org.appling.famtree.gedcom.Person;

import java.awt.*;
import java.util.Collections;

/**
 * Created by sappling on 9/1/2017.
 */
public class PaternalAncestorLayout extends AbstractLayout {
    @Override
    public void layout(Person startPerson) throws GedException {
        populate(startPerson, 0, limit);
        Collections.reverse(generations);
    }

    public void populate(Person person, int generation, int limit) throws GedException {
        Generation gen = getGeneration(generation++);
        gen.addFrame(person.getFrame());
        if (generation < limit) {
            Person father = person.getFather();
            if (father != null ) {
                Generation parentGen = getGeneration(generation);
                Person mother = person.getMother();
                if (mother != null) {
                    //parentGen.
                }
                populate(father, generation, limit);
            }
        }
    }

}
