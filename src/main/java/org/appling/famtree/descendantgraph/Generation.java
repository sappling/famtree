package org.appling.famtree.descendantgraph;

import org.appling.famtree.gedcom.Person;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sappling on 8/26/2017.
 */
public class Generation {
    ArrayList<PersonFrame> frames = new ArrayList<>();
    int yPos = 0;
    int genNum;

    public Generation(int genNum) {
        this.genNum = genNum;
        yPos = ((PersonFrame.VSPACING + PersonFrame.FRAME_HEIGHT)* genNum)+PersonFrame.VERT_PAGE_MARGIN;
    }

    public void addFrame(PersonFrame frame) {
        frame.setGeneration(this);
        frames.add(frame);
    }

    public int getWidth() {
        int result = PersonFrame.HORIZ_PAGE_MARGIN;
        if (!frames.isEmpty()) {
            PersonFrame lastFrame = frames.get(frames.size() - 1);
            result = lastFrame.getXPosition() + PersonFrame.FRAME_WIDTH + lastFrame.getRightSpace();
        }
        return result;
    }

    public int getNumPeople() {
        return frames.size();
    }

    public int getyPos() {
        return yPos;
    }

    public int getNumber() {
        return genNum;
    }

    public void moveRight(PersonFrame targetFrame, int amount) {
        boolean found = false;
        for (PersonFrame frame : frames) {
            if (frame.equals(targetFrame)) {
                found = true;
            }
            if (found) {
              frame.setXPosition(frame.getXPosition() + amount);
            }
        }
    }

    /*
    public void setyPos(int yPos) {
        this.yPos = yPos;
    }
    */

    List<PersonFrame> getFrames() {
        return frames;
    }

}
