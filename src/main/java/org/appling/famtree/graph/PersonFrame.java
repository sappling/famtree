package org.appling.famtree.graph;

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

/**
 * Created by sappling on 8/12/2017.
 */
public class PersonFrame {
    public static final int VERT_PAGE_MARGIN = 20;
    public static final int HORIZ_PAGE_MARGIN = 20;
    public static final int FRAME_HEIGHT = 170;
    public static final int FRAME_WIDTH = 82;
    public static final int MIN_HSPACE = 20;
    public static final int HSPACING = 40;
    public static final int VSPACING = 80;
    private static final int PICTURE_WIDTH = 80;
    private static final int PICTURE_HEIGHT = 80;
    private static final int SPOUSE_LINE_OFFSET = 10;
    private Person person;
    private int xPos = 0;
    private int yPos = 0;
    private int leftSpace = MIN_HSPACE;
    private int rightSpace = MIN_HSPACE;
    private Generation generation;
    private int spouseCount = 0;
    boolean hideChildren = false;
    boolean showCrossRef = false;


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

    public void setHideChildren() { hideChildren = true; }

    public void setGeneration(Generation generation) {
        this.generation = generation;
    }

    @Nullable
    public Generation getGeneration() {
        return generation;
    }

    public void setShowCrossRef() {
        showCrossRef = true;
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
        return getEastPort(0);
    }

    public IntPoint getEastPort(int spouseCount) {
        return new IntPoint(xPos + FRAME_WIDTH, (yPos + PICTURE_HEIGHT + 2) - (spouseCount * SPOUSE_LINE_OFFSET));
    }

    public IntPoint getWestPort() {
        return getWestPort(0);
    }

    public IntPoint getWestPort(int spouseCount) {

        return new IntPoint(xPos, (yPos + PICTURE_HEIGHT + 2) - (spouseCount * SPOUSE_LINE_OFFSET));
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
        if (!hideChildren) {
            java.util.List<Family> families = person.getFamiliesWhereSpouse();
            int spouseCount = 0;

            for (Family family : families) {
                try {
                    Person spouse = family.getOtherSpouse(person);

                    IntPoint topPoint = null;
                    if (spouse != null) {
                        PersonFrame spouseFrame = getGeneration().findFollowingFrame(this, spouse);
                        if (spouseFrame != null) {
                            topPoint = spouseFrame.getWestPort(spouseCount);
                            topPoint.moveBy(-1 * (MIN_HSPACE / 2), 0);
                        }
                    }
                    if (topPoint == null) {
                        topPoint = getSouthPort();
                    }
                    java.util.List<Person> children = family.getChildren();
                    for (Person child : children) {
                        if (child.getFrame().getGeneration() != null) {
                            IntPoint bottomPoint = child.getFrame().getNorthPort();
                            IntPoint firstTurn = new IntPoint(topPoint.getX(), bottomPoint.getY() - (VSPACING / 3) - (spouseCount * SPOUSE_LINE_OFFSET));
                            IntPoint secondTurn = new IntPoint(bottomPoint.getX(), firstTurn.getY());

                            drawLine(g2d, topPoint, firstTurn);
                            drawLine(g2d, firstTurn, secondTurn);
                            drawLine(g2d, secondTurn, bottomPoint);
                        }
                    }
                    spouseCount++;
                } catch (GedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void drawLine(Graphics2D g2d, IntPoint point1, IntPoint point2) {
        g2d.drawLine(point1.getX(), point1.getY(), point2.getX(), point2.getY());
    }

    private void paintSpouseLines(Graphics2D g2d) {
        java.util.List<Family> families = person.getFamiliesWhereSpouse();

        int spouseCount = 0;
        for (Family family : families) {
            try {
                Person spouse = family.getOtherSpouse(person);
                if (spouse != null) {
                    PersonFrame spouseFrame = getGeneration().findFollowingFrame(this, spouse);
                    if (spouseFrame != null) {
                        IntPoint spousePort = spouseFrame.getWestPort(spouseCount);
                        IntPoint myPort = getEastPort(spouseCount);
                        g2d.drawLine(myPort.getX(), myPort.getY(), spousePort.getX(), spousePort.getY());
                        spouseCount++;
                    }
                }
            } catch (GedException e) {
                e.printStackTrace();    // just print it out and continue.  No logging now
            }
        }
    }

    private void paintBox(Graphics2D g2d) {
        g2d.setPaint(Color.white);
        g2d.fill(new Rectangle(xPos, yPos, FRAME_WIDTH, FRAME_HEIGHT));

        g2d.setPaint(Color.gray);
        g2d.fill(new Rectangle(xPos, yPos+1, FRAME_WIDTH, PICTURE_HEIGHT+1));
        BufferedImage image = null;
        try {
            String imagePath = person.getProfileImagePath();
            //imagePath = "testtall.jpg";
            if (imagePath == null) {
                g2d.setPaint(Color.white);
                g2d.fill(new Rectangle(xPos, yPos+1, FRAME_WIDTH, PICTURE_HEIGHT+1));
            } else {
                image = ImageIO.read(new File(imagePath));
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
        g2d.setPaint(Color.black);
        g2d.draw(new Rectangle(xPos, yPos, FRAME_WIDTH, FRAME_HEIGHT));
        g2d.draw(new Rectangle(xPos, yPos, FRAME_WIDTH, PICTURE_HEIGHT+2));
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


        font = new Font("Lucida Sans", Font.PLAIN, 10);
        g2d.setFont(font);
        String birth = person.getBirthString(); //DateUtils.normStringFromDate(person.getBirthDate());
        String death = person.getDeathString(); //DateUtils.normStringFromDate(person.getDeathDate());

        y = yPos + PICTURE_HEIGHT + (5*lineHeight);

        g2d.drawString("B:"+birth, xPos+1, y);
        y+= lineHeight;

        g2d.drawString("D:"+death, xPos+1, y);
        y+= lineHeight;

        if (showCrossRef) {
            int x = xPos + 1;
            y = yPos - 3;
            g2d.drawString("See other "+getPerson().getCleanId(), x, y);
        }
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
