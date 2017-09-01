package org.appling.famtree.descendantgraph;

import org.appling.famtree.gedcom.*;
import org.jetbrains.annotations.Nullable;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Created by sappling on 8/26/2017.
 */
public class DescandentLayout {
    private static final PersonRegistry pr = PersonRegistry.instance();
    ArrayList<Generation> generations = new ArrayList<>();


    public DescandentLayout() {
    }

    public void layout(Person startPerson) throws GedException {
        populate(startPerson);

        ListIterator<Generation> li = generations.listIterator(generations.size());
        li.previous(); // skip last
        while (li.hasPrevious()) {
            Generation parentGen = li.previous();
            centerParents(parentGen);
        }

    }

    private void populate(Person startPerson) throws GedException {
        DescendantWalker walker = new DescendantWalker(new AddFrames());
        walker.walk(startPerson, 0);
    }

    public void centerParents(Generation generation) throws GedException {
        java.util.List<PersonFrame> frames = generation.getFrames();
        ListIterator<PersonFrame> it = frames.listIterator();

        while (it.hasNext()) {
            PersonFrame descendant = it.next();
            PersonFrame spouse = getLastSpouse(it);
            int left = descendant.getXPosition();
            int right = left + PersonFrame.FRAME_WIDTH;
            if (spouse != null) {
                right = spouse.getXPosition() + PersonFrame.FRAME_WIDTH;
            }
            PersonFrame firstChild = getFirstChild(descendant);
            PersonFrame lastChild = getLastChild(descendant);
            int childLeft = left;
            int childRight = right;
            if (firstChild != null && lastChild != null) {
                childLeft = firstChild.getXPosition();
                childRight = lastChild.getXPosition() + PersonFrame.FRAME_WIDTH;
                PersonFrame rightPerson = getLastSpouse(lastChild);
                if (rightPerson != null) {
                    childRight = rightPerson.getXPosition() + PersonFrame.FRAME_WIDTH;
                }
            }
            int parentCenter = left + ((right - left) /2);
            int childCenter = childLeft + ((childRight - childLeft) / 2);
            int move = childCenter - parentCenter;
            if (move > 0) { // move parents right
                generation.moveRight(descendant, move);
            } else {  // move child right
                if (firstChild != null) {
                    moveDescendantsRight(firstChild, generation.getNumber() + 1, -1 * move);
                }
            }
        }
    }

    public void moveDescendantsRight(PersonFrame leftmostChild, int generationNumber, int moveBy) throws GedException {
        if (generationNumber < getNumGenerations()) {
            Generation childGeneration = generations.get(generationNumber);
            childGeneration.moveRight(leftmostChild, moveBy);
            if (generationNumber+1 < getNumGenerations()) {
                PersonFrame grandChild = getFirstBelow(leftmostChild);  // needs to be first below
                if (grandChild != null) {
                    moveDescendantsRight(grandChild, generationNumber+1, moveBy);
                }
            }
        }
    }

    static PersonFrame getLastSpouse(ListIterator<PersonFrame> it) {
        PersonFrame lastSpouse = null;
        while (it.hasNext()) {
            PersonFrame possibleSpouse = it.next();
            if (possibleSpouse.getSpouseCount() > 0) {
                lastSpouse = possibleSpouse;
            } else {
                it.previous();
                break;
            }
        }
        return lastSpouse;
    }

    @Nullable
    private static PersonFrame getFirstChild(PersonFrame frame) throws GedException {
        PersonFrame result = null;
        List<Family> families = frame.getPerson().getFamiliesWhereSpouse();
        for (Family family : families) {
            List<Person> children = family.getChildren();
            if (!children.isEmpty()) {
                result = children.get(0).getFrame();
                break;
            }
        }
        return result;
    }

    @Nullable
    private static PersonFrame getLastChild(PersonFrame frame) throws GedException {
        PersonFrame result = null;
        List<Family> families = frame.getPerson().getFamiliesWhereSpouse();
        ListIterator<Family> li = families.listIterator(families.size());
        while (li.hasPrevious()) {
            Family family = li.previous();
            List<Person> children = family.getChildren();
            if (!children.isEmpty()) {
                result = children.get(children.size()-1).getFrame();
                break;
            }
        }
        return result;
    }

    /**
     * Finds the first person below and to the right of this person.
     * This is the leftmost person in the generation beneath this person who is below or to the right.
     * This would be a child if there are any, or could be a child of anyone else to the right.
     * @param frame
     * @return
     * @throws GedException
     */
    @Nullable
    private static PersonFrame getFirstBelow(PersonFrame frame) throws GedException {
        Generation generation = frame.getGeneration();
        ListIterator<PersonFrame> it = generation.getFrames().listIterator();
        advanceItToPerson(it, frame);

        while (it.hasNext()) {
            PersonFrame nextPeer = it.next();
            PersonFrame child = getFirstChild(nextPeer);
            if (child != null) {
                return child;
            }
        }
        return null;
    }

    private static void advanceItToPerson(ListIterator<PersonFrame> it, PersonFrame frame) {
        while (it.hasNext()) {
            PersonFrame next = it.next();
            if (next.equals(frame)) {
                it.previous();
                break;
            }
        }
    }

    @Nullable PersonFrame getLastSpouse(PersonFrame frame) throws GedException {
        PersonFrame result = null;
        List<Family> families = frame.getPerson().getFamiliesWhereSpouse();
        if (!families.isEmpty()) {
            Family lastFamily = families.get(families.size() - 1);
            Person otherSpouse = lastFamily.getOtherSpouse(frame.getPerson());
            if (otherSpouse != null) {
                result = otherSpouse.getFrame();
            }
        }
        return result;
    }



    public int getWidth() {
        return getWidestGeneration().getWidth();
    }

    public int getHeight() {
        Generation lastGen = generations.get(generations.size() - 1);
        return lastGen.getyPos() + PersonFrame.FRAME_HEIGHT + (2 * PersonFrame.VERT_PAGE_MARGIN);
    }

    public int getNumGenerations() {
        return generations.size();
    }

    public int getTotalPeople() {
        int result = 0;
        for (Generation generation : generations) {
            result += generation.getNumPeople();
        }
        return result;
    }

    public Generation getWidestGeneration() {
        return Collections.max(generations, Comparator.comparing(Generation::getWidth));
    }

    public void render(Graphics2D gaphics) {
        for (Generation generation : generations) {
            for (PersonFrame frame : generation.getFrames()) {
                frame.paint(gaphics);
            }
        }
    }

    public Generation getGaneration(int genNum) {
        while (generations.size()-1 < genNum) {
            Generation g = new Generation(genNum);
            generations.add(g);
        }
        return generations.get(genNum);
    }

    private class AddFrames implements PersonAction {
        @Override
        public void act(Person person, int spouseCount, int genNumber) {
            Generation generation = getGaneration(genNumber);
            PersonFrame frame = person.getFrame();
            frame.setSpouseCount(spouseCount);
            if (frame.getGeneration() == null) { // only add frame if not already in another generation
                int xPos = generation.getWidth();
                if (xPos == 0) {
                    xPos = PersonFrame.HORIZ_PAGE_MARGIN;
                }
                frame.setPosition(xPos, generation.getyPos());

                generation.addFrame(frame);
            }

        }

        @Override
        public void lastChildOrSpouse(Person person) {
            PersonFrame frame = person.getFrame();
            int space = frame.getRightSpace();
            frame.setRightSpace(space + (PersonFrame.FRAME_WIDTH/2)  + (2 * PersonFrame.MIN_HSPACE));
        }

    }
}
