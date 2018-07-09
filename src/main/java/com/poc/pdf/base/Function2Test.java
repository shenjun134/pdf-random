package com.poc.pdf.base;


import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceCmyk;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Function2Test {


    public static void main(String[] args) throws Exception {
//        withLine();
        withSkew();
    }

    public static List<String> text = new ArrayList();

    static {
        text.add("         Episode V         ");
        text.add("  THE EMPIRE STRIKES BACK  ");
        text.add("It is a dark time for the");
        text.add("Rebellion. Although the Death");
        text.add("Star has been destroyed,");
        text.add("Imperial troops have driven the");
        text.add("Rebel forces from their hidden");
        text.add("base and pursued them across");
        text.add("the galaxy.");
        text.add("Evading the dreaded Imperial");
        text.add("Starfleet, a group of freedom");
        text.add("fighters led by Luke Skywalker");
        text.add("has established a new secret");
        text.add("base on the remote ice world");
        text.add("of Hoth...");

    }

    public static void withLine() throws FileNotFoundException {
        PdfDocument pdf = new PdfDocument(new PdfWriter(new FileOutputStream("with-canvas.pdf")));
        PageSize ps = PageSize.A4.rotate();
        PdfPage page = pdf.addNewPage(ps);
        PdfCanvas canvas = new PdfCanvas(page);

        canvas.concatMatrix(1, 0, 0, 1, ps.getWidth() / 2, ps.getHeight() / 2);

        canvas.moveTo(-(ps.getWidth() / 2 - 15), 0)
                .lineTo(ps.getWidth() / 2 - 15, 0)
                .stroke();
//Draw X axis arrow
        canvas.setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
                .moveTo(ps.getWidth() / 2 - 25, -10)
                .lineTo(ps.getWidth() / 2 - 15, 0)
                .lineTo(ps.getWidth() / 2 - 25, 10).stroke()
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.MITER);
//Draw Y axis
        canvas.moveTo(0, -(ps.getHeight() / 2 - 15))
                .lineTo(0, ps.getHeight() / 2 - 15)
                .stroke();
//Draw Y axis arrow
        canvas.saveState()
                .setLineJoinStyle(PdfCanvasConstants.LineJoinStyle.ROUND)
                .moveTo(-10, ps.getHeight() / 2 - 25)
                .lineTo(0, ps.getHeight() / 2 - 15)
                .lineTo(10, ps.getHeight() / 2 - 25).stroke()
                .restoreState();
//Draw X serif
        for (int i = -((int) ps.getWidth() / 2 - 61);
             i < ((int) ps.getWidth() / 2 - 60); i += 40) {
            canvas.moveTo(i, 5).lineTo(i, -5);
        }
//Draw Y serif
        for (int j = -((int) ps.getHeight() / 2 - 57);
             j < ((int) ps.getHeight() / 2 - 56); j += 40) {
            canvas.moveTo(5, j).lineTo(-5, j);
        }
        canvas.stroke();


// Draw the axes
        pdf.close();
    }


    private static void withNoSkew() throws IOException {

        PdfDocument pdf = new PdfDocument(new PdfWriter("with-canvas-text.pdf"));
        PageSize ps = PageSize.A4.rotate();
        PdfPage page = pdf.addNewPage(ps);
        PdfCanvas canvas = new PdfCanvas(page);
// Draw the axes

        canvas.concatMatrix(1, 0, 0, 1, 0, ps.getHeight());
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER_BOLD), 14)
                .setLeading(14 * 1.2f)
                .moveText(70, -40);
        for (String s : text) {
            //Add text and move to the next line
            canvas.newlineShowText(s);
        }
        canvas.endText();
        pdf.close();
    }

    private static void withSkew() throws IOException {
        int maxStringWidth = 20;
        PdfDocument pdf = new PdfDocument(new PdfWriter("with-canvas-skew.pdf"));
        PageSize ps = PageSize.A4.rotate();
        PdfPage page = pdf.addNewPage(ps);
        PdfCanvas canvas = new PdfCanvas(page);
// Draw the axes
        canvas.rectangle(0, 0, ps.getWidth(), ps.getHeight())
                .setColor(Color.BLACK, true)
                .fill();
        canvas.concatMatrix(1, 0, 0, 1, 0, ps.getHeight());
        Color yellowColor = new DeviceCmyk(0.f, 0.0537f, 0.769f, 0.051f);
        float lineHeight = 5;
        float yOffset = -40;
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER_BOLD), 1)
                .setColor(yellowColor, true);
        for (int j = 0; j < text.size(); j++) {
            String line = text.get(j);
            float xOffset = ps.getWidth() / 2 - 45 - 8 * j;
            float fontSizeCoeff = 6 + j;
            float lineSpacing = (lineHeight + j) * j / 1.5f;
            int stringWidth = line.length();
            for (int i = 0; i < stringWidth; i++) {
                float angle = (maxStringWidth / 2 - i) / 2f;
                float charXOffset = (4 + (float) j / 2) * i;
                canvas.setTextMatrix(fontSizeCoeff, 0,
                        angle, fontSizeCoeff / 1.5f,
                        xOffset + charXOffset, yOffset - lineSpacing)
                        .showText(String.valueOf(line.charAt(i)));
            }
        }
        canvas.endText();
        pdf.close();
    }


}
