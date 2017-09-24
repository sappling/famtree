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
import org.appling.famtree.gedcom.PersonRegistry;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by sappling on 9/1/2017.
 */
public abstract class AbstractLayout implements Layout {
    protected static final PersonRegistry pr = PersonRegistry.instance();
    protected ArrayList<Generation> generations = new ArrayList<>();
    protected int limit = Integer.MAX_VALUE;
    protected Person stopPerson;
    private boolean showGrid = false;

    @Override
    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void showGrid() {
        showGrid = true;
    }

    @Override
    public int getNumGenerations() {
        return generations.size();
    }

    @Override
    public int getTotalPeople() {
        int result = 0;
        for (Generation generation : generations) {
            result += generation.getNumPeople();
        }
        return result;
    }

    @Override
    public Generation getGeneration(int genNum) {
        while (generations.size()-1 < genNum) {
            Generation g = new Generation(genNum);
            generations.add(g);
        }
        return generations.get(genNum);
    }


    @Override
    public int getWidth() {
        return getWidestGeneration().getWidth();
    }

    @Override
    public int getHeight() {
        Generation lastGen = generations.get(generations.size() - 1);
        return lastGen.getyPos() + PersonFrame.FRAME_HEIGHT + (2 * PersonFrame.VERT_PAGE_MARGIN);
    }

    @Override
    public void setStopPerson(Person stopPerson) {
        this.stopPerson = stopPerson;
    }

    public Generation getWidestGeneration() {
        return Collections.max(generations, Comparator.comparing(Generation::getWidth));
    }

    @Override
    public void render(Graphics2D graphics) {
        for (Generation generation : generations) {
            for (PersonFrame frame : generation.getFrames()) {
                frame.paint(graphics);
            }
        }
        if (showGrid) {
            renderGrid(graphics);
        }
    }

    private void renderGrid(Graphics2D graphics) {
        int height = getHeight();
        int width = getWidth();

        int paperHeight = (int) (8.5 * 72);
        int paperWidth = 11 * 72;

        int horizLines = height / paperHeight;
        int vertLines = width / paperWidth;

        Graphics2D g = (Graphics2D) graphics.create();
        g.setColor(Color.lightGray);
        Stroke dashed = new BasicStroke(3, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{10}, 0);
        graphics.setStroke(dashed);

        for (int i=1; i<= horizLines; i++) {
            g.drawLine(0, paperHeight*i, width, paperHeight*i);
        }
        for (int i=1; i<= vertLines; i++) {
            g.drawLine(paperWidth*i, 0, paperWidth*i, height);
        }

    }

}

