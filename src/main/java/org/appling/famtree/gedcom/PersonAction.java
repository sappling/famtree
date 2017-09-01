package org.appling.famtree.gedcom;

/**
 * Created by sappling on 8/15/2017.
 */
public interface PersonAction {
    /**
     * Perform the action needed for each person when walking the tree.
     * @param person - the current person in the walk
     * @param spouseCount - 0=descendant, 1=first spouse, 2=second spouse, etc.
     * @param generation - 0 = generation at the root of the tree
     */
    void act(Person person, int spouseCount, int generation);

    /**
     * Action needed for the last child or last spouse of last child.
     * @param person
     */
    void lastChildOrSpouse(Person person);
}
