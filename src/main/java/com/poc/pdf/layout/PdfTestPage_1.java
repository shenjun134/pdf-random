package com.poc.pdf.layout;


import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.poc.pdf.model.ScanResult;
import com.poc.pdf.util.PDFUtil;
import com.poc.pdf.util.RandomUtil;
import com.poc.pdf.util.VOUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfTestPage_1 {

    public static final String FONT_FAMILY = "Courier";


    public static void main(String[] args) throws Exception {
        int count = 1;
        process2(0, count);
    }

    public static void process(int count) throws Exception {
        File outFile = new File("layout-1");
        if (!outFile.exists()) {
            outFile.mkdir();
        }
        for (int i = 0; i < count; i++) {
            withPageSize(i);
        }
        convert2Jpg(0, count);
    }

    public static void process2(int begin, int end) throws Exception {
        File outFile = new File("layout-1");
        if (!outFile.exists()) {
            outFile.mkdir();
        }
        for (int i = begin; i < end; i++) {
            withPageSize(i);
        }
        convert2Jpg(begin, end);
    }

    private static void convert2Jpg(int begin, int end) {
        String userDir = System.getProperty("user.dir");
        String layoutIndex = "1";
        String layoutName = "/layout-" + layoutIndex;
        String tempDir = userDir + layoutName + "/";
        for (int i = begin; i < end; i++) {
            String name = StringUtils.leftPad("" + i, 6, "0");
            String originalPdf = userDir + layoutName + "/" + name + ".pdf";
            PDFUtil.splitPdf2Jpg(originalPdf, tempDir, 72);
        }
    }


    public static void withPageSize(int index) throws IOException, DocumentException {

        String folder = "layout-1";
        String name = StringUtils.leftPad("" + index, 6, "0");

        String pdfFile = name + ".pdf";
        String imgFile = name + ".jpg";
        String xmlFile = name + ".xml";
        String targetPDF = folder + "/" + pdfFile;
        String targetXMl = folder + "/" + xmlFile;


        String userDir = System.getProperty("user.dir");
        Path tablePath = Paths.get(userDir + "/out-1/table-" + index + ".jpg");
        Path structurePath = Paths.get(userDir + "/out-1/structure-" + index + ".txt");

        String owner = "HengTian";
        float xOffset = 0f;
        float yOffset = 0f;
        float defaultWidth = 1728f;
        float defaultHeight = 1078f;
        ScanResult scanResult = new ScanResult(defaultWidth, defaultHeight);

        Rectangle pagesize = new Rectangle(defaultWidth + xOffset, defaultHeight + yOffset);
        float marginLeft = 140f;
        float marginRight = 0f;
        float marginTop = 10f;
        float marginBottom = 0f;

        // step 4
        BaseFont bf1 = null;
        try {
            bf1 = BaseFont.createFont(
                    FONT_FAMILY, "UTF-8", BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Font font = new Font(bf1);
        int red = 17;
        int green = 17;
        int blue = 17;

        font.setColor(red, green, blue);
//        font.setFamily("Abhaya Libre");
        font.setSize(26f);

        BaseFont bfMargin = null;
        try {
            bfMargin = BaseFont.createFont(
                    BaseFont.TIMES_BOLD, "UTF-8", BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Font fontMargin = new Font(bfMargin);
        fontMargin.setSize(30f);

        Document document = new Document(pagesize, marginLeft, marginRight, marginTop, marginBottom);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(targetPDF));


        // step 3
        document.open();

        PdfContentByte cb = writer.getDirectContent();
        PdfLayer not_printed = new PdfLayer("not printed", writer);
        not_printed.setOnPanel(false);
        not_printed.setPrint("Print", false);
//        cb.setFontAndSize(bfMargin, 30f);
        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "ABCDE ABCDEF Bank 2/17/2017 7:43;05 PM   6/6000     Fax Server", fontMargin), defaultWidth - 30, 150, -90);
        cb.endLayer();


        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "10971232 Pg 6 of 6", font), defaultWidth - 300, 20, -180);
        cb.endLayer();

        writer.setSpaceCharRatio(23f);

        String firstSpace = StringUtils.leftPad(" ", 20);
        String paragraphText =
                firstSpace + "ABCDE ABCDEF ABCDE ABCDEFGHIJK ABCDEFGHIJK";

        String splitSpace = StringUtils.leftPad(" ", 20);

        String paragraphText2 = "Authorized Signature" + splitSpace + "Authorized Signature" + splitSpace + "Test Key";


        Paragraph paragraph = new Paragraph("", font);
        paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        paragraph.setLeading(50);

        document.add(paragraph);

        /**
         * skew
         */
//        float alpha = -2f;
        float alpha = 0f;

        float beta = 0f;

        Chunk chunk = new Chunk(paragraphText, font);
        chunk.setSkew(alpha, beta);

        document.add(chunk);
        document.add(Chunk.NEWLINE);

        Image img3 = Image.getInstance(tablePath.toAbsolutePath().toString());
        img3.scalePercent(100);
        img3.setRotationDegrees(alpha);

        float xBegin = RandomUtil.randomInt(Constant.xBeginMax, Constant.xBeginMin);
        float yBegin = RandomUtil.randomInt(Constant.yBeginMax, Constant.yBeginMin);

        float xMin = xBegin;
        float yMin = defaultHeight - yBegin - img3.getPlainHeight();


        String json = FileUtils.readFileToString(structurePath.toFile());


        scanResult.setOwner(owner);
        scanResult.setFilename(imgFile);
        scanResult.setFolder("VOC2007");

        String xmlOut = VOUtil.mergeResult(json, xMin, yMin, scanResult);
        FileUtils.writeStringToFile(new File(targetXMl), xmlOut);


        //max x, y 220f, 170f
//        img3.setAbsolutePosition(220f, 170f);

        //min x,y 10f, 100f
        img3.setAbsolutePosition(xBegin, yBegin);
        document.add(img3);

        int rowSize = 18;
        String blankRow = StringUtils.leftPad("", rowSize, "\n");

        Chunk chunk2 = new Chunk(blankRow + paragraphText2, font);
        chunk2.setSkew(alpha, beta);

        document.add(chunk2);
        document.add(Chunk.NEWLINE);

        // step 5
//        writer.close();
        document.close();

        System.out.println("------------------layout-1 generate:" + pdfFile);
    }


    interface Constant {
        int xBeginMin = 10;
        int xBeginMax = 220;

        int yBeginMin = 100;
        int yBeginMax = 170;
    }


}
