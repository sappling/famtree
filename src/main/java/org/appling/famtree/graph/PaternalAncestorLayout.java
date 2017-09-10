package org.appling.famtree.graph;

import org.appling.famtree.gedcom.Family;
import org.appling.famtree.gedcom.GedException;
import org.appling.famtree.gedcom.Person;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by sappling on 9/1/2017.
 */
public class PaternalAncestorLayout extends AbstractLayout {
    @Override
    public void layout(Person startPerson) throws GedException {
        populate(startPerson, 0, limit);

        Collections.reverse(generations);
        int genNum = 0;
        for (Generation generation : generations) {
            generation.setYPosForGen(genNum++);
            List<PersonFrame> frames = generation.getFrames();
            int xPos = PersonFrame.HORIZ_PAGE_MARGIN;
            for (PersonFrame frame : frames) {
                frame.setPosition(xPos, generation.getyPos());
                xPos += PersonFrame.FRAME_WIDTH + PersonFrame.MIN_HSPACE;
            }
        }
    }

    public void populate(Person person, int generation, int limit) throws GedException {
        Generation gen = getGeneration(generation);
        gen.addFrame(person.getFrame());
        java.util.List<Family> families = person.getFamiliesWhereSpouse();
        int spouseCount = 1;
        for (Family family : families) {
            Person spouse = family.getOtherSpouse(person);
            if (spouse != null) {
                PersonFrame spouseFrame = spouse.getFrame();
                spouseFrame.setSpouseCount(spouseCount++);
                gen.addFrame(spouseFrame);
            }
        }


        if (generation < limit && (stopPerson == null || !stopPerson.equals(person))) {
            Person father = person.getFather();
            if (father != null) {
                populate(father, generation + 1, limit);
            }
        }
    }

}
