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
    public DescendantWalker(PersonAction personAction) {
        action = personAction;
    }

    public void walk(Person person, int generation) throws GedException {
        walk(person, 0, false);
    }

    private void walk(Person person, int generation, boolean moreChildren) throws GedException {
        action.act(person, 0, generation);

        List<Family> families = person.getFamiliesWhereSpouse();
        int spouseCount = 1;
        Person lastSpouse = null;
        for (Family family : families) {
            Person spouse = family.getOtherSpouse(person);
            if (spouse != null) {
                lastSpouse = spouse;
                action.act(spouse, spouseCount++, generation);
            }

            Iterator<Person> it = family.getChildren().iterator();

            while (it.hasNext()) {
                Person child = it.next();
                walk(child, generation+1, it.hasNext());
            }
        }
        if (!moreChildren) {
            if (lastSpouse != null) {
                action.lastChildOrSpouse(lastSpouse);
            } else {
                action.lastChildOrSpouse(person);
            }
        }
    }
}
