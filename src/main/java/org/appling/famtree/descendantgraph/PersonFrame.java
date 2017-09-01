package org.appling.famtree.descendantgraph;

import org.appling.famtree.gedcom.Family;
import org.appling.famtree.gedcom.GedException;
import org.appling.famtree.gedcom.Person;
import org.appling.famtree.util.DateUtils;
import org.appling.famtree.util.StringUtils;
import org.imgscalr.Scalr;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * Created by sappling on 8/12/2017.
 */
public class PersonFrame {
    public static final int VERT_PAGE_MARGIN = 20;
    public static final int HORIZ_PAGE_MARGIN = 20;
    public static final int FRAME_HEIGHT = 200;
    public static final int FRAME_WIDTH = 82;
    public static final int MIN_HSPACE = 20;
    public static final int HSPACING = 40;
    public static final int VSPACING = 80;
    private static final int PICTURE_WIDTH = 80;
    private static final int PICTURE_HEIGHT = 100;
    private Person person;
    private int xPos = 0;
    private int yPos = 0;
    private int leftSpace = MIN_HSPACE;
    private int rightSpace = MIN_HSPACE;
    private Generation generation;
    private int spouseCount = 0;


    public PersonFrame(Person person) {
        this.person = person;
    }

    public void setPosition(int x, int y) {
        xPos = x;
        yPos = y;
    }

    public int getXPosition() {
        return xPos;
    }

    public void setXPosition(int x) {
        xPos = x;
    }

    public Person getPerson() {
        return person;
    }

    public void setGeneration(Generation generation) {
        this.generation = generation;
    }

    @Nullable
    public Generation getGeneration() {
        return generation;
    }

    public int getLeftSpace() {
        return leftSpace;
    }

    public void setLeftSpace(int leftSpace) {
        this.leftSpace = Math.max(leftSpace, MIN_HSPACE);
    }

    public int getRightSpace() {
        return rightSpace;
    }

    public void setRightSpace(int rightSpace) {
        this.rightSpace = Math.max(rightSpace, MIN_HSPACE);
    }

    public IntPoint getEastPort() {
        return new IntPoint(xPos + FRAME_WIDTH, yPos + PICTURE_HEIGHT + 2);
    }

    public IntPoint getWestPort() {
        return new IntPoint(xPos, yPos + PICTURE_HEIGHT + 2);
    }

    public IntPoint getNorthPort() {
        return new IntPoint(xPos + (FRAME_WIDTH/2), yPos);
    }

    public IntPoint getSouthPort() {
        return new IntPoint(xPos + (FRAME_WIDTH/2), yPos + FRAME_HEIGHT);
    }

    public int getWidth() {
        return FRAME_WIDTH + leftSpace + rightSpace;
    }

    /**
     * Get counter used to indicate famliy relationships.
     *  0 = descendant, 1 = first spouse, 2 = second spouse, etc.
     * @return
     */
    public int getSpouseCount() {
        return spouseCount;
    }

    /**
     * Set counter used to indicate famliy relationships.
     *  0 = descendant, 1 = first spouse, 2 = second spouse, etc.
     * @return
     */
    public void setSpouseCount(int spouseCount) {
        this.spouseCount = spouseCount;
    }

    public void paint(Graphics2D g2d) {
        if (getSpouseCount() == 0) { // if this is a descendant
            paintSpouseLines(g2d);
            paintChildrenLines(g2d);
        }
        paintBox(g2d);
        paintText(g2d);
    }

    private void paintChildrenLines(Graphics2D g2d) {
        java.util.List<Family> families = person.getFamiliesWhereSpouse();
        for (Family family : families) {
            try {
                Person spouse = family.getOtherSpouse(person);
                IntPoint topPoint;
                if (spouse != null) {
                    topPoint = spouse.getFrame().getWestPort();
                    topPoint.moveBy(-1*(MIN_HSPACE/2), 0);
                } else {
                    topPoint = getSouthPort();
                }
                java.util.List<Person> children = family.getChildren();
                for (Person child : children) {
                    IntPoint bottomPoint = child.getFrame().getNorthPort();
                    IntPoint firstTurn = new IntPoint(topPoint.getX(), bottomPoint.getY() - (VSPACING/3));
                    IntPoint secondTurn = new IntPoint(bottomPoint.getX(), firstTurn.getY());
                    //g2d.drawLine(topPoint.getX(), topPoint.getY(), bottomPoint.getX(), bottomPoint.getY());
                    drawLine(g2d, topPoint, firstTurn);
                    drawLine(g2d, firstTurn, secondTurn);
                    drawLine(g2d, secondTurn, bottomPoint);
                }
            } catch (GedException e) {
                e.printStackTrace();
            }
        }

    }

    private void drawLine(Graphics2D g2d, IntPoint point1, IntPoint point2) {
        g2d.drawLine(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    private void paintSpouseLines(Graphics2D g2d) {
        java.util.List<Family> families = person.getFamiliesWhereSpouse();
        for (Family family : families) {
            try {
                Person spouse = family.getOtherSpouse(person);
                if (spouse != null) {
                    IntPoint spousePort = spouse.getFrame().getWestPort();
                    IntPoint myPort = getEastPort();
                    g2d.drawLine(myPort.getX(), myPort.getY(), spousePort.getX(), spousePort.getY());
                }
            } catch (GedException e) {
                e.printStackTrace();    // just print it out and continue.  No logging now
            }
        }
    }

    private void paintBox(Graphics2D g2d) {
        g2d.setPaint(Color.white);
        g2d.fill(new Rectangle(xPos, yPos, FRAME_WIDTH, FRAME_HEIGHT));

        g2d.setPaint(Color.black);
        g2d.draw(new Rectangle(xPos, yPos, FRAME_WIDTH, FRAME_HEIGHT));
        g2d.draw(new Rectangle(xPos, yPos, FRAME_WIDTH, PICTURE_HEIGHT+2));
        BufferedImage image = null;
        try {
            String imagePath = person.getProfileImagePath();
            //imagePath = "testtall.jpg";
            if (imagePath != null) {
                image = ImageIO.read(new File(imagePath));
                // src mode width height ops
                BufferedImage scaledImage = scaleToFit(image, PICTURE_WIDTH, PICTURE_HEIGHT);
                IntPoint offset = offsetToCenter(scaledImage, PICTURE_WIDTH, PICTURE_HEIGHT);
                int imgY = yPos + offset.getY() + 1;
                int imgX = xPos + offset.getX() + 1;
                g2d.drawImage(scaledImage, imgX, imgY, scaledImage.getWidth(),scaledImage.getHeight(), null);
                image.flush();
                scaledImage.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private BufferedImage scaleToFit(BufferedImage image, int width, int height) {
        BufferedImage scaledImage = Scalr.resize(image, Scalr.Mode.FIT_TO_WIDTH, PICTURE_WIDTH, PICTURE_HEIGHT);
        if (scaledImage.getHeight() > height) {
            scaledImage.flush();
            scaledImage = Scalr.resize(image, Scalr.Mode.FIT_TO_HEIGHT, PICTURE_WIDTH, PICTURE_HEIGHT);
        }
        return scaledImage;
    }

    private void paintText(Graphics2D g2d) {
        Font font = new Font("Lucida Sans", Font.PLAIN, 11);
        g2d.setFont(font);
        FontMetrics metrics = g2d.getFontMetrics();
        java.util.List<String> lines = StringUtils.wrap(person.getFullName(), metrics, FRAME_WIDTH-4);
        int lineHeight = metrics.getHeight();

        int y = yPos + PICTURE_HEIGHT + lineHeight;
        for (String line : lines) {
            g2d.drawString(line, xPos+1, y);
            y+= lineHeight;
        }


        String birth = DateUtils.normStringFromDate(person.getBirthDate());
        String death = DateUtils.normStringFromDate(person.getDeathDate());

        y = yPos + PICTURE_HEIGHT + (5*lineHeight);

        g2d.drawString("B:"+birth, xPos+1, y);
        y+= lineHeight;

        g2d.drawString("D:"+death, xPos+1, y);
        y+= lineHeight;
    }

    private IntPoint offsetToCenter(BufferedImage image, int widthOfSpace, int heightOfSpace) {
        int width = image.getWidth();
        int height = image.getHeight();

        int hoffset = (widthOfSpace - width) / 2;
        int voffset = (heightOfSpace - height) / 2;
        return new IntPoint(hoffset, voffset);
    }

    public String toString() {
        return String.format("%d,%d %s", xPos, yPos, person.toString());
    }

}
