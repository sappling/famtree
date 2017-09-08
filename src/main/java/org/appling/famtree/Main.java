package org.appling.famtree;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.batik.dom.GenericDOMImplementation;
import org.apache.batik.svggen.SVGGraphics2D;
import org.apache.batik.svggen.SVGGraphics2DIOException;
import org.apache.commons.cli.*;
import org.appling.famtree.graph.DescendantLayout;
import org.appling.famtree.gedcom.GedException;
import org.appling.famtree.gedcom.Person;
import org.appling.famtree.gedcom.PersonRegistry;
import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.parser.GedcomParser;
import org.w3c.dom.DOMImplementation;

import java.awt.*;
import java.io.*;
import java.util.*;

/**
 * Created by sappling on 8/29/2017.
 */
public class Main {
    private static final String OPTION_ID = "id";
    private static final String OPTION_OUT = "out";
    private static final String OPTION_IN = "in";
    private static final String OPTION_GEN = "gen";
    private static final String OPTION_STOP = "stop";
    private static final String OPTION_HELP = "help";
    private static final String OPTION_TYPE = "type";

    public static void main (String args[]) {
        CommandLineParser parser = new DefaultParser();
        Options options = setupOptions();
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        }  catch (MissingOptionException me) {
            System.out.println(me.getLocalizedMessage());
            printHelp(options);
            System.exit(-1);
        }catch (ParseException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        // todo - make required options no longer required so we can get to the help printout
        if (line.hasOption(OPTION_HELP)) {
            printHelp(options);
            System.exit(0);
        }

        DescendantLayout layout = null;
        String rootName = "";
        try {
            layout = new DescendantLayout();
            //layout.showGrid();

            Person rootPerson = getPerson(line.getOptionValue(OPTION_IN), line.getOptionValue(OPTION_ID));
            rootName = rootPerson.getFullName();

            if (line.hasOption(OPTION_GEN)) {
                layout.setLimit(Integer.parseInt(line.getOptionValue(OPTION_GEN)));
            }
            if (line.hasOption(OPTION_STOP)) {
                Person stopPerson = PersonRegistry.instance().getPerson(String.format("@I%s@", line.getOptionValue(OPTION_STOP)));
                layout.setStopPerson(stopPerson);
            }
            layout.layout(rootPerson);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(-1);
        }

        boolean typePdf = true;
        if (line.hasOption(OPTION_TYPE)) {
            String typeString = line.getOptionValue(OPTION_TYPE);
            if ("svg".equalsIgnoreCase(typeString)) {
                typePdf = false;
            }
        }

        String outFile;
        if (line.hasOption(OPTION_OUT)) {
            outFile = line.getOptionValue(OPTION_OUT);
        } else {

            outFile = rootName+ (typePdf ? ".pdf" : ".svg");
        }


        if (typePdf) {
            writePdf(layout, outFile);
        } else {
            writeSvg(layout, outFile);
        }


        System.out.format("Results are %.2f in wide and %.2f tall\n", layout.getWidth()/72.0, layout.getHeight()/72.0);
        System.out.format("That's %d generations and and a total of %d people\n", layout.getNumGenerations(), layout.getTotalPeople());
        System.out.format("Wrote %s\n", outFile);

        // below was for testing changes in each sweep of layout
        /*
        int numGen = layout.getNumGenerations();
        for (int i=numGen-2; i>=0; i--) {
            try {
                layout.centerParents(layout.getGeneration(i));
            } catch (GedException e) {
                e.printStackTrace();
            }
            writePdf(layout, "test-"+i+".pdf");
        }
        */
    }

    private static void writePdf(DescendantLayout layout, String outPath) {
        int width = layout.getWidth();
        int height = layout.getHeight();
        Document document = new Document(new Rectangle(width, height));
        PdfWriter writer = null;
        try {
            writer = PdfWriter.getInstance(document, new FileOutputStream(outPath));
        } catch (DocumentException | FileNotFoundException e) {
            e.printStackTrace();
            System.exit(-1);
        }
        document.open();
        PdfContentByte canvas = writer.getDirectContent();
        PdfTemplate template = canvas.createTemplate(width, height);
        Graphics2D g2d = new PdfGraphics2D(template, width, height);

        layout.render(g2d);

        g2d.dispose();
        canvas.addTemplate(template, 0, 0);
        document.newPage();
        document.close();
    }

    private static void writeSvg(DescendantLayout layout, String outPath) {
        // Get a DOMImplementation.
        DOMImplementation domImpl =
                GenericDOMImplementation.getDOMImplementation();

        // Create an instance of org.w3c.dom.Document.
        String svgNS = "http://www.w3.org/2000/svg";
        org.w3c.dom.Document document = domImpl.createDocument(svgNS, "svg", null);

        // Create an instance of the SVG Generator.
        SVGGraphics2D g2d = new SVGGraphics2D(document);
        g2d.setSVGCanvasSize(new Dimension(layout.getWidth(), layout.getHeight()));

        boolean useCSS = true; // we want to use CSS style attributes
        Writer out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(outPath), "UTF-8");
            layout.render(g2d);
            g2d.stream(out, useCSS);
        } catch (UnsupportedEncodingException | SVGGraphics2DIOException | FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    private static Person getPerson(String filePath, String personId) throws GedException, IOException, GedcomParserException {
        GedcomParser gp = new GedcomParser();
        File gedFile = new File(filePath);
        gp.load(gedFile.getAbsolutePath());
        Gedcom g = gp.getGedcom();
        PersonRegistry pr = PersonRegistry.instance();
        pr.setGedcom(g);

        return pr.getPerson(String.format("@I%s@", personId));
    }

    private static void printHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp("famtree", options, true);
    }


    private static Options setupOptions() {
        Options options = new Options();
        options.addOption(Option.builder(OPTION_IN)
                .desc("specifies input file")
                .required()
                .optionalArg(false)
                .numberOfArgs(1)
                .argName("filename").build());
        options.addOption(Option.builder(OPTION_OUT)
                .desc("specifies input file")
                .optionalArg(false)
                .numberOfArgs(1)
                .argName("filename").build());
        options.addOption(Option.builder(OPTION_ID)
                .desc("ID number of top ancestor")
                .required()
                .optionalArg(false)
                .numberOfArgs(1)
                .argName("ID").build());
        options.addOption(Option.builder(OPTION_GEN)
                .desc("maximum number of generations")
                .optionalArg(false)
                .numberOfArgs(1)
                .argName("numgen").build());
        options.addOption(Option.builder(OPTION_STOP)
                .desc("Person to stop at")
                .optionalArg(false)
                .numberOfArgs(1)
                .argName("ID").build());
        options.addOption(Option.builder(OPTION_TYPE)
                .desc("Type of output: svg or pdf")
                .optionalArg(false)
                .numberOfArgs(1)
                .argName("type").build());
        options.addOption(Option.builder(OPTION_HELP)
                .desc("Show commmand line usage")
                .build());

        return options;
    }
}
