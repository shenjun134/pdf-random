package com.poc.pdf.layout;


import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.poc.pdf.model.AreaVO;
import com.poc.pdf.model.BoxVO;
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

public class PdfTestPage_2 {

    public static final String FONT_FAMILY = "Courier";


    public static void main(String[] args) throws Exception {
        int count = 1;
//        process(count);

        process2(0, count);

    }

    public static void process(int count) throws Exception {
        File outFile = new File("layout-2");
        if (!outFile.exists()) {
            outFile.mkdir();
        }
        for (int i = 0; i < count; i++) {
            withPageSize(i);
        }
        convert2Jpg(0, count);
    }

    public static void process2(int begin, int end) throws Exception {
        File outFile = new File("layout-2");
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
        String layoutIndex = "2";
        String layoutName = "/layout-" + layoutIndex;
        String tempDir = userDir + layoutName + "/";
        for (int i = begin; i < end; i++) {
            String name = StringUtils.leftPad("" + i, 6, "0");
            String originalPdf = userDir + layoutName + "/" + name + ".pdf";
            PDFUtil.splitPdf2Jpg(originalPdf, tempDir, 72);
        }
    }


    public static void withPageSize(int index) throws IOException, DocumentException {

        String folder = "layout-2";
        String name = StringUtils.leftPad("" + index, 6, "0");
        String pdfFile = name + ".pdf";
        String imgFile = name + ".jpg";
        String xmlFile = name + ".xml";
        String targetPDF = folder + "/" + pdfFile;
        String targetXMl = folder + "/" + xmlFile;


        String userDir = System.getProperty("user.dir");
        Path tablePath = Paths.get(userDir + "/out-2/table-" + index + ".jpg");
        Path structurePath = Paths.get(userDir + "/out-2/structure-" + index + ".txt");


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
                    BaseFont.HELVETICA, "UTF-8", BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Font fontMargin = new Font(bfMargin);
        fontMargin.setSize(26f);


        BaseFont bfHead = null;
        try {
            bfHead = BaseFont.createFont(
                    BaseFont.HELVETICA_BOLD, "UTF-8", BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Font headMargin = new Font(bfHead);
        headMargin.setSize(32f);


        BaseFont bfSmall = null;
        try {
            bfSmall = BaseFont.createFont(
                    BaseFont.HELVETICA_BOLD, "UTF-8", BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Font small = new Font(bfSmall);
        small.setSize(10f);

        BaseFont bfMid = null;
        try {
            bfMid = BaseFont.createFont(
                    BaseFont.HELVETICA_BOLD, "UTF-8", BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Font fmid = new Font(bfMid);
        fmid.setSize(16f);

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
                "11077143 Pg 6 of 6", font), 400, defaultHeight - 20, 0);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "#1203 P . 001/001", fontMargin), 20, defaultHeight - 100, 90);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "24/04  2017 05 : 15  41235271", fontMargin), 20, 400, 90);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "4/24/2017", fmid), 500, defaultHeight - 60, 0);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "FAQ MTP ETO Pricing Team", fmid), 490, defaultHeight - 80, 0);
        cb.endLayer();


        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "PLEASE ADD TO CUSTODY ONLY", headMargin), 900, defaultHeight - 120, 0);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "Fax number 00t 6t7 1740180", fmid), 600, defaultHeight - 160, 0);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "Authenticate signature:", fmid), 500, 180, 1);
        cb.endLayer();


        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "YHHNG LLIHG", fmid), 500, 160, 1);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "P+ 91 22 33123 2131", fmid), 500, 140, 1);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "Email: XXX@sdasdasd.com", fmid), 500, 120, 1);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "Sasdas Sdasda", fmid), 1000, 160, 1);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "P+ 91 22 231231 2131", fmid), 1000, 140, 1);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "Email: XXX2@sdasdasd.com", fmid), 1000, 120, 1);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                "Please do not duplicate the trades, Thanks!", headMargin), 900, 100, 1);
        cb.endLayer();


        String text = "9unmfUIOC0AaQXy4UGcmMB12N0G4wAUheZe2gn91n7cp5l4s4RLUtJ5IQR1irPZji7dBZUCaRNjOASBKaE0eMKJ6zRGveb35pMup2VvKuNFmrQkoqTuw6zMV2vAJIMcMQpfi7hqer5auj4HMns3YoiutJoAlmqucByJnxNw2Wo91s6G2k9GsWbj4HvRcPRqpGoRKf61";
        String end = "mfUIOC0AaQsadasdasda";

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                text, small), defaultWidth - 400, 90, 1);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                text, small), defaultWidth - 400, 70, 1);
        cb.endLayer();

        cb.beginLayer(not_printed);
        ColumnText.showTextAligned(cb, Element.ALIGN_RIGHT, new Phrase(
                end, small), 238, 30, 1);
        cb.endLayer();


        writer.setSpaceCharRatio(23f);


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

        // step 5
//        writer.close();
        document.close();

        System.out.println("------------------layout-2 generate:" + pdfFile);
    }


    interface Constant {
        int xBeginMin = 50;
        int xBeginMax = 180;

        int yBeginMin = 220;
        int yBeginMax = 280;
    }


}
