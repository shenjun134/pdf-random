package com.poc.pdf.base;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;

import java.io.FileOutputStream;
import java.io.IOException;

public class ColumnWidthExample {

    public static final String DEST = "column_width_example.pdf";
    public static void main(String[] args) throws IOException, DocumentException {
        new ColumnWidthExample().createPdf(DEST);
    }

    public void createPdf(String dest) throws IOException, DocumentException {
        Document document = new Document(PageSize.A4.rotate());
        PdfWriter.getInstance(document, new FileOutputStream(dest));
        document.open();
        float[] columnWidths = {1, 3, 52};
        PdfPTable table = new PdfPTable(columnWidths);
        table.setWidthPercentage(100);
        table.getDefaultCell().setUseAscender(true);
        table.getDefaultCell().setUseDescender(true);
        Font f = new Font(BaseFont.FONT_TYPE_CJK, 13, Font.NORMAL, GrayColor.GRAYWHITE);
        PdfPCell cell = new PdfPCell(new Phrase("This is a header", f));
        cell.setBackgroundColor(GrayColor.GRAYBLACK);
        cell.setHorizontalAlignment(Element.ALIGN_CENTER);
        cell.setColspan(3);
        table.addCell(cell);
        table.getDefaultCell().setBackgroundColor(new GrayColor(0.75f));
        for (int i = 0; i < 2; i++) {
            table.addCell("#");
            table.addCell("Key");
            table.addCell("Value");
        }
        table.setHeaderRows(3);
        table.setFooterRows(1);
        table.getDefaultCell().setBackgroundColor(GrayColor.GRAYWHITE);
        table.getDefaultCell().setHorizontalAlignment(Element.ALIGN_CENTER);
        for (int counter = 1; counter < 101; counter++) {
            table.addCell(String.valueOf(counter));
            table.addCell("key " + counter);
            table.addCell("value " + counter);
        }
        document.add(table);
        document.close();
    }
}