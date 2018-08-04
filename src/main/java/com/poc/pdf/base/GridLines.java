package com.poc.pdf.base;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.lowagie.text.Element;
import com.lowagie.text.Phrase;
import com.lowagie.text.pdf.ColumnText;
import com.lowagie.text.pdf.PdfContentByte;
import com.lowagie.text.pdf.PdfLayer;
import com.poc.pdf.model.GridLayoutConfig;
import com.poc.pdf.model.GridLayoutResult;
import com.poc.pdf.model.Line;
import com.poc.pdf.model.Point;
import com.poc.pdf.util.GridLayoutUtil;
import org.apache.commons.lang.RandomStringUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Rectangle tempPagesize = new Rectangle(config.getTotalWidth(), config.getTotalHeight());
        PageSize ps = new PageSize(tempPagesize);
        PdfPage page = pdf.addNewPage(ps);

        PdfCanvas canvas = new PdfCanvas(page);

        canvas.setLineWidth(config.getBorderWidth()).setStrokeColor(config.getBorderColor());
        //Replace the origin of the coordinate system to the center of the page
        for (Line line : result.getLineList()) {
            drawLine(canvas, line, config);
        }


        canvas.stroke();
        int padding = 20;
        //fill in text
        for (com.poc.pdf.model.Rectangle rectangle : result.getRectList()) {
            if (rectangle.isSplit()) {
                continue;
            }
            int fontSize = fontSize();
            float leadingSize = 1.2f * fontSize;
            List<String> textList = randomString(rectangle, fontSize);
            int x = rectangle.getReal1(config).getX() + padding;
            int y = rectangle.getReal1(config).getY() - padding;

            String fontFamily = fontProgram();
            canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(fontFamily), fontSize)
                    .setLeading(leadingSize)
                    .moveText(x, y);
            for(String text : textList){
                canvas.newlineShowText(text);
            }
            canvas.endText();

            rectangle.setText(textList);
        }

        //make noise


        //Close document
        pdf.close();
    }

    private static void drawLine(PdfCanvas canvas, Line line, GridLayoutConfig config) {
        Point p1 = line.getReal1(config);
        Point p2 = line.getReal2(config);

        canvas.moveTo(p1.getX(), p1.getY()).lineTo(p2.getX(), p2.getY());
    }

    private static List<String> randomString(com.poc.pdf.model.Rectangle rectangle, int fontSize) {
        int baseFont = 14;
        int unitCharLength = 16;
        int offsite = 8;

        int height = rectangle.height();
        int row = height / (unitCharLength * fontSize / baseFont) - offsite;
        if (row < 1) {
            row = 1;
        }
        List<String> list = new ArrayList<>(row);
        for(int i = 0 ; i<row;i++){
            list.add(randomText(rectangle.width(), fontSize));
        }
        return list;
    }

    private static String randomText(int width, int fontSize){
        int baseFont = 14;
        int unitCharLength = 9;
        int offsite = 6;

        int count = width / (unitCharLength * fontSize / baseFont) - offsite;
        if (count < 3) {
            count = 3;
        }
//        String range = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+=-{}[];:'\"|\\/><,.`";
        String range = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        return RandomStringUtils.random(count, range);
    }




    private static int fontSize() {
        double rd = Math.random() * 1000;
        return 14 + (int) (rd / 10);
    }

    private static String fontProgram() {
        double rd = Math.random() * 1000;
        String[] fontArr = new String[]{
                FontConstants.COURIER,
                FontConstants.COURIER_BOLD,
                FontConstants.COURIER_OBLIQUE,
                FontConstants.COURIER_BOLDOBLIQUE,
                FontConstants.HELVETICA,
                FontConstants.HELVETICA_BOLD,
                FontConstants.HELVETICA_OBLIQUE,
                FontConstants.HELVETICA_BOLDOBLIQUE,
                FontConstants.SYMBOL,
                FontConstants.TIMES_ROMAN,
                FontConstants.TIMES_BOLD,
                FontConstants.TIMES_ITALIC,
                FontConstants.TIMES_BOLDITALIC,
                FontConstants.ZAPFDINGBATS,
                FontConstants.TIMES

        };
        int index = (int) (rd % fontArr.length - 1);
        return fontArr[index];
    }

}