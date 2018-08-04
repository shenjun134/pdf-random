package com.poc.pdf.base;


import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfWriter;
import com.poc.pdf.model.GridLayoutConfig;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class GridTest {

    private static final float PDF_PERCENT = 1f;

    public static void main(String[] args) throws Exception {
        withLine();
    }

    public static List<String> text = new ArrayList();


    public static void withLine() throws FileNotFoundException {
        GridLayoutConfig config = new GridLayoutConfig();
        FileOutputStream fileOutputStream = new FileOutputStream("with-canvas-line.pdf");

        Rectangle tempPagesize = new Rectangle(config.getTotalWidth(), config.getTotalHeight());
        float marginLeft = 0f;
        float marginRight = 0f;
        float marginTop = 0f;
        float marginBottom = 0f;
        Document tempDoc = new Document(tempPagesize, marginLeft, marginRight, marginTop, marginBottom);
        try {
            PdfWriter writer = PdfWriter.getInstance(tempDoc, fileOutputStream);
            tempDoc.setPageSize(tempPagesize);
            tempDoc.open();
            PdfContentByte canvas = writer.getDirectContent();
            canvas.setLineWidth(config.getBorderWidth());
//            canvas.setColorStroke(config.getBorderColor());
            drawLine(canvas, 0, 0, 200, 400);

            drawLine(canvas, 323, 0, 200, 400);

        } catch (DocumentException e) {
            e.printStackTrace();
        }
        // step 3
        tempDoc.close();
    }

    private static void drawLine(PdfContentByte canvas, float left, float top, int x, int y) {
        canvas.saveState();
        canvas.moveTo(left, top);
        canvas.lineTo(left + x * PDF_PERCENT, top - y * PDF_PERCENT);
        canvas.stroke();
        canvas.restoreState();
    }






}
