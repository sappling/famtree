package org.appling.famtree;

import com.itextpdf.awt.PdfGraphics2D;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.itextpdf.text.pdf.PdfWriter;
import org.apache.commons.cli.*;
import org.appling.famtree.descendantgraph.DescandentLayout;
import org.appling.famtree.gedcom.GedException;
import org.appling.famtree.gedcom.Person;
import org.appling.famtree.gedcom.PersonRegistry;
import org.gedcom4j.exception.GedcomParserException;
import org.gedcom4j.model.Gedcom;
import org.gedcom4j.parser.GedcomParser;

import java.awt.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by sappling on 8/29/2017.
 */
public class Main {
    private static final String OPTION_ID = "id";
    private static final String OPTION_OUT = "out";
    private static final String OPTION_IN = "in";

    public static void main (String args[]) {
        CommandLineParser parser = new DefaultParser();
        Options options = setupOptions();
        CommandLine line = null;
        try {
            line = parser.parse(options, args);
        } catch (ParseException e) {
            e.printStackTrace();
            System.exit(-1);
        }

        DescandentLayout layout = null;
        try {
            layout = getLayout(line.getOptionValue(OPTION_IN), line.getOptionValue(OPTION_ID));
        } catch (GedcomParserException | GedException | IOException e) {
            e.printStackTrace();
            System.exit(-1);
        }


        String outFile;
        if (line.hasOption(OPTION_OUT)) {
            outFile = line.getOptionValue(OPTION_OUT);
        } else {
            outFile = String.format("ID-%s.pdf", line.getOptionValue(OPTION_ID));
        }
        writePdf(layout, outFile);

        System.out.format("Results are %.2f in wide and %.2f tall\n", layout.getWidth()/72.0, layout.getHeight()/72.0);
        System.out.format("That's %d generations and and a total of %d people\n", layout.getNumGenerations(), layout.getTotalPeople());
        // below was for testing changes in each sweep of layout
        /*
        int numGen = layout.getNumGenerations();
        for (int i=numGen-2; i>=0; i--) {
            try {
                layout.centerParents(layout.getGaneration(i));
            } catch (GedException e) {
                e.printStackTrace();
            }
            writePdf(layout, "test-"+i+".pdf");
        }
        */
    }

    private static void writePdf(DescandentLayout layout, String outPath) {
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

    private static DescandentLayout getLayout(String filePath, String personId) throws GedException, IOException, GedcomParserException {
        GedcomParser gp = new GedcomParser();
        DescandentLayout layout = new DescandentLayout();
        File gedFile = new File(filePath);
        gp.load(gedFile.getAbsolutePath());
        Gedcom g = gp.getGedcom();
        PersonRegistry pr = PersonRegistry.instance();
        pr.setGedcom(g);

        Person rootPerson = pr.getPerson(String.format("@I%s@", personId));
        //Person rootPerson = pr.getPerson("@I340@");   // George Washington Sparkman
        //Person rootPerson = pr.getPerson("@I65@");   // Thomas Bryant Sparkman
        //Person rootPerson = pr.getPerson("@I158@");   // Thomas Lansford Sparkman (5 deep)
        //Person rootPerson = pr.getPerson("@I162@");   // Stephen Charles Appling
        //Person rootPerson = pr.getPerson("@I266@");  // Ann Bell
        layout.layout(rootPerson);
        return layout;
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
        return options;
    }
}
