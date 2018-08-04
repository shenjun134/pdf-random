package com.poc.pdf.base;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.poc.pdf.model.GridLayoutConfig;
import com.poc.pdf.model.GridLayoutResult;
import com.poc.pdf.model.Line;
import com.poc.pdf.model.Point;
import com.poc.pdf.util.GridLayoutUtil;

import java.io.File;
import java.io.IOException;

public class GridLines {

    public static final String DEST = "./grid_lines2.pdf";

    public static void main(String args[]) throws IOException {
        File file = new File(DEST);
        file.getParentFile().mkdirs();
        new GridLines().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {
        GridLayoutConfig config = new GridLayoutConfig();
        GridLayoutResult result = GridLayoutUtil.randomGrid(config);
        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));
        Rectangle tempPagesize = new Rectangle(config.getTotalWidth(), config.getTotalHeight());
        PageSize ps = new PageSize(tempPagesize);
        PdfPage page = pdf.addNewPage(ps);

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.setLineWidth(config.getBorderWidth()).setStrokeColor(config.getBorderColor());
        //Replace the origin of the coordinate system to the center of the page
//        canvas.concatMatrix(1, 0, 0, 1, ps.getWidth() / 2, ps.getHeight() / 2);
        for(Line line : result.getLineList()){
            drawLine(canvas, line, config);
        }

        canvas.stroke();

        //Draw axes

//        C02E01_Axes.drawAxes(canvas, ps);

        //Draw plot

        //Close document
        pdf.close();
    }

    private static void drawLine(PdfCanvas canvas, Line line, GridLayoutConfig config){
        Point p1 = line.getReal1(config);
        Point p2 = line.getReal2(config);

        canvas.moveTo(p1.getX(), p1.getY()).lineTo(p2.getX(), p2.getY());
    }
}