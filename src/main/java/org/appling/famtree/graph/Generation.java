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

package org.appling.famtree.graph;

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
        setYPosForGen(genNum);
    }

    public void addFrame(PersonFrame frame) {
        frame.setGeneration(this);
        frames.add(frame);
    }

    public void setYPosForGen(int genNum) {
        yPos = ((PersonFrame.VSPACING + PersonFrame.FRAME_HEIGHT)* genNum)+PersonFrame.VERT_PAGE_MARGIN;
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

    /**
     * Find the PersonFram for the specified person after the specified starting frame
     * @param startFrame
     * @param person
     * @return
     */
    public PersonFrame findFollowingFrame(PersonFrame startFrame, Person person) {
        boolean foundStart = false;
        for (PersonFrame frame : frames) {
            if (foundStart) {
                if (frame.getPerson().equals(person)) {
                    return frame;
                }
            } else {
                if (startFrame.equals(frame)) {
                    foundStart = true;
                }
            }
        }
        return null;
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
