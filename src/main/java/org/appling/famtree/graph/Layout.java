package org.appling.famtree.graph;

import org.appling.famtree.gedcom.GedException;
import org.appling.famtree.gedcom.Person;

import java.awt.*;

/**
 * Created by sappling on 9/1/2017.
 */
public interface Layout {
    void layout(Person startPerson) throws GedException;

    int getWidth();

    int getHeight();

    int getNumGenerations();

    int getTotalPeople();

    void render(Graphics2D gaphics);

    Generation getGeneration(int genNum);
}
