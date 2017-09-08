package org.appling.famtree.graph;

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
    private boolean showGrid = false;

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

