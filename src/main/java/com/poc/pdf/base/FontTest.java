package com.poc.pdf.base;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.poc.pdf.util.FileUtil;
import com.poc.pdf.util.FontUtil;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class FontTest {
    public static final String DEST = "./font-test.pdf";

    public static void main(String args[]) throws IOException {
        process();
    }

    public static void process() throws IOException {
        new FontTest().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException {

        //Initialize PDF document
        PdfDocument pdf = new PdfDocument(new PdfWriter(dest));

        //Add new page
//        PageSize ps = PageSize.A4;
        List<String> fontList = FontUtil.allFont();
        int fontLineBase = 30;
        int len = fontList.size();
        int width = 2000;
        int height = 2000;
        int pageCount = len / fontLineBase;
        if (len % fontLineBase > 0) {
            pageCount = pageCount + 1;
        }
        List<PdfCanvas> canvasList = new ArrayList<>();
        for (int i = 0; i < pageCount; i++) {
            Rectangle tempPagesize = new Rectangle(width, height);
            PageSize ps = new PageSize(tempPagesize);
            PdfPage page = pdf.addNewPage(ps);
            PdfCanvas canvas = new PdfCanvas(page);
            canvasList.add(canvas);
        }
        int realHeight = (int) height;


        String test = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789 . ~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/? ";

        //Replace the origin of the coordinate system to the top left corner
        int x = 40;
        int baseY = 40;
        int y = 40;
        int lineHeight = 30;
        int fontSize = 14;

        float totalW = 0f;
        for (int i = 0; i < len; i++) {
            int canvasIndex = i / fontLineBase;
            int canvasIndexOffsite = i % fontLineBase;
            PdfCanvas canvas = canvasList.get(canvasIndex);

            String font = fontList.get(i);
            PdfFont baseFont = FontUtil.createFont(font);
            if (canvasIndexOffsite == 0) {
                y = baseY;
            } else {
                y = y + lineHeight;
            }

            int realY = realHeight - y;

            canvas.beginText()
                    .setFontAndSize(baseFont, fontSize)
                    .setLeading(fontSize * 1.2f)
                    .moveText(x, realY);
            canvas.newlineShowText(test);
            canvas.endText();

            y = y + lineHeight;
            realY = realHeight - y;

            canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER), fontSize)
                    .setLeading(fontSize * 1.2f)
                    .moveText(x, realY);
            float textW = baseFont.getWidth(test, fontSize);
            String temp = "==========Font:" + i + " " + baseFont + " " + textW;
            System.out.println(temp);
            canvas.newlineShowText(temp);
            canvas.endText();
            totalW = totalW + textW;
            if (i == len - 1) {
                y = y + lineHeight;
                realY = realHeight - y;
                canvas.beginText()
                        .setFontAndSize(PdfFontFactory.createFont(FontConstants.COURIER), fontSize)
                        .setLeading(fontSize * 1.2f)
                        .moveText(x, realY);
                float avgW = totalW / len;
                String last = "Avg width:" + avgW;
                canvas.newlineShowText(last);
                canvas.endText();
            }
        }


        //Close document
        pdf.close();

    }


}