package com.poc.pdf.util;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.poc.pdf.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class PDFUtil {

    private static final Logger logger = Logger.getLogger(PDFUtil.class);

    public interface Constant {
        String headerStr = "department PM status manager commission member";
        String bodyStr = "-40791 75931 65046 97433 89780 -37667.06\n" +
                "-72575 -39754 6cHFJd 16722 13613 -28747\n" +
                "\n" +
                "58407.92 32120 25223.4 GySQWOrK -81452 8393\n" +
                "\n" +
                "-93549.53 -69912 20544.94 38824 eBE2Hk3C0 -15053.91";
    }

    public static List<SubFile> splitPdf2Jpg(String originalFile, String storeTempPath, int dpi) {
        if (!StringUtils.endsWith(originalFile, ".pdf")) {
            return null;
        }
        File file = new File(originalFile);
        String tiffSrc = file.getName().substring(0, file.getName().indexOf("."));
        List<SubFile> subFiles = new ArrayList<SubFile>();
        File dir = new File(storeTempPath);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        PDDocument document = null;
        try {
            document = PDDocument.load(file);
            PDFRenderer pdfRenderer = new PDFRenderer(document);

            int pageSize = document.getNumberOfPages();
            for (int page = 0; page < pageSize; ++page) {
                String tempName = dir.getAbsolutePath() + File.separator + tiffSrc + "-" + page + ".jpg";
                if (pageSize == 1) {
                    tempName = dir.getAbsolutePath() + File.separator + tiffSrc + ".jpg";
                }
                BufferedImage bim = pdfRenderer.renderImageWithDPI(page, dpi, ImageType.RGB);
                ImageIOUtil.writeImage(bim, tempName, dpi);

                SubFile subFile = new SubFile();
                subFile.setName(tempName);
                subFiles.add(subFile);

            }
        } catch (Exception e) {
            logger.error("splitPdf2Jpg error", e);
            throw new RuntimeException("splitPdf2Jpg error", e);
        } finally {
            if (document != null) {
                try {
                    document.close();
                } catch (IOException e) {
                    logger.error("close document error when splitPdf2Jpg", e);
                }
            }
        }

        return subFiles;
    }

    public static List<SubFile> splitPdf2Jpg(String originalFile, String storeTempPath) {
        return splitPdf2Jpg(originalFile, storeTempPath, 98);
    }

    public static TableVO mockTableVO() {
        return createdTableVO(Constant.headerStr, Constant.bodyStr);
    }

    public static TableVO createdTableVO(String headerStr, String bodyStr) {


        String[] hArr = StringUtils.split(headerStr, " ");


        RowVO header = new RowVO();
        java.util.List<RowVO> body = new ArrayList<>();
        for (String h : hArr) {
            CellVO cellVO = new CellVO(h);
            header.add(cellVO);
        }
        String[] rowArr = StringUtils.split(bodyStr, "\n");
        for (String rowStr : rowArr) {
            if (StringUtils.isBlank(rowStr)) {
                continue;
            }
            String[] cellArr = StringUtils.split(rowStr, " ");
            RowVO row = new RowVO();
            for (String c : cellArr) {
                CellVO cellVO = new CellVO(c);
                row.add(cellVO);
            }
            body.add(row);

        }
        TableVO tableVO = new TableVO(header, body);
        return tableVO;
    }

    public static PdfPTable generatePDFTable(TableVO tableVO) {
        String space = StringUtils.leftPad("", 1);
        float paddingBottom = 40f;
        float lastPaddingBottom = 20f;
        float paddingLeft = 30f;
        float paddingTop = 20f;
        BaseFont bf1 = null;
        try {
            bf1 = BaseFont.createFont("Times-Bold"
                    , "UTF-8", BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Font font = new Font(bf1);
        int red = 00;
        int green = 00;
        int blue = 00;
        font.setColor(red, green, blue);
//        font.setFamily("Abhaya Libre");
        font.setSize(46f);
        font.setStyle(Font.NORMAL);

        int colSize = tableVO.cloSize();
        PdfPTable table = new PdfPTable(colSize);
        table.setRunDirection(1);
        table.setHorizontalAlignment(0);

        int borderWith = 6;
        /**
         * set header
         */
        for (int i = 0; i < colSize; i++) {
            CellVO cellVO = tableVO.getHeader().getList().get(i);
            PdfPCell header = new PdfPCell();
            header.setBorderWidth(0);
            String value = cellVO.getValue().toString();
            if (i == 0) {
                header.setBorderWidthLeft(borderWith);
                value = cellVO.getValue().toString();
            }

            if (i == colSize - 1) {
                header.setBorderWidthRight(borderWith);
            }
            header.setHorizontalAlignment(1);
            header.setBorderWidthTop(borderWith);
            header.setPaddingLeft(paddingLeft);
            header.setPaddingTop(paddingTop);
            header.setPaddingBottom(paddingBottom);

            Phrase phrase = new Phrase(value, font);
            header.setPhrase(phrase);
            table.addCell(header);
        }


        /**
         * set body
         */
        int rowSize = tableVO.getBodyList().size();
        for (int i = 0; i < rowSize; i++) {
            RowVO rowVO = tableVO.getBodyList().get(i);
            boolean isLast = i == rowSize - 1;

            for (int j = 0; j < colSize; j++) {
                CellVO cellVO = rowVO.getList().get(j);
                PdfPCell header = new PdfPCell();
                header.setBorderWidth(0);
                String value = cellVO.getValue().toString();
                if (j == 0) {
                    header.setBorderWidthLeft(borderWith);
                    value = space + cellVO.getValue().toString();
                }
                if (j == colSize - 1) {
                    header.setBorderWidthRight(borderWith);
                }
                header.setPaddingBottom(paddingBottom);
                if (isLast) {
                    header.setBorderWidthBottom(borderWith);
                    header.setPaddingBottom(lastPaddingBottom);
                }
                header.setHorizontalAlignment(1);
                header.setPaddingLeft(paddingLeft);
                Phrase phrase = new Phrase(value, font);
                header.setPhrase(phrase);
                table.addCell(header);
            }

        }


        return table;
    }


    public static void table() throws IOException, DocumentException {

        String target = "table.pdf";
        String temp = "temp-";

        float xOffset = 0f;
        float yOffset = 0f;
        Rectangle tempPagesize = new Rectangle(2480f + xOffset, 3508f + yOffset);
        float marginLeft = 0f;
        float marginRight = 0f;
        float marginTop = 0f;
        float marginBottom = 0f;

        Document tempDoc = new Document(tempPagesize, marginLeft, marginRight, marginTop, marginBottom);
        // step 2
        FileOutputStream fileOutputStream = new FileOutputStream(temp + target);
        PdfWriter writer = PdfWriter.getInstance(tempDoc, fileOutputStream);
        // step 3
        tempDoc.open();
        writer.setSpaceCharRatio(25f);

        //step add content
        TableVO tableVO = mockTableVO();
        PdfPTable table = generatePDFTable(tableVO);
        tempDoc.add(table);

        int borderWith = 6;
        float totalHeight = table.getTotalHeight();
        float totalWidth = table.getTotalWidth();
        System.out.println("totalHeight:" + totalHeight);
        System.out.println("totalWidth:" + totalWidth);

//        writer.close();
//        fileOutputStream.close();
        tempDoc.close();

        String userDir = System.getProperty("user.dir");
        String tempDir = userDir + "/temp/";
        String oiginalPdf = userDir + "/temp-table.pdf";
        List<SubFile> list = splitPdf2Jpg(oiginalPdf, tempDir);

        System.out.println(list);
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("no image found");
            return;
        }
        SubFile imageFile = list.get(0);


        File file = new File(imageFile.getName());
        FileInputStream fis = new FileInputStream(file);
        BufferedImage image = ImageIO.read(fis);

        double scale = image.getWidth() / 2480.0d;

        BufferedImage spliter = image.getSubimage(0, 0, (int) totalWidth, (int) totalHeight);
        Graphics2D gr = spliter.createGraphics();
        //gr.drawImage(image, 0, 0,chunckwidth,chunckheigth,null);
        gr.drawImage(image, 0, 0, (int) totalWidth, (int) totalHeight, 0, 0, (int) (totalWidth * scale), (int) (totalHeight * scale), null);
        gr.dispose();

        ImageIO.write(spliter, "png", new File("img-table.png"));


    }

    public static TableStructureVO toStruction(PdfPTable table, TableVO tableVO) {
        float[] absWidth = table.getAbsoluteWidths();
        int rowNum = 1 + tableVO.getBodyList().size();
        float[] absHeight = new float[rowNum];
        for (int i = 0; i < rowNum; i++) {
            absHeight[i] = table.getRowHeight(i);
        }
        float totalHeight = table.getTotalHeight();
        float totalWidth = table.getTotalWidth();
        TableStructureVO tableStructureVO = new TableStructureVO(totalHeight, totalWidth);
        tableStructureVO.setColWidth(absWidth);
        tableStructureVO.setRowheight(absHeight);
        return tableStructureVO;
    }

    public static void main(String[] args) throws Exception {
        String userDir = System.getProperty("user.dir");
        String layoutIndex = "2";
        String layoutName = "/layout-" + layoutIndex;
        String tempDir = userDir + layoutName + "/";
        for (int i = 0; i < 100; i++) {
            String originalPdf = userDir + layoutName + "/table-" + i + ".pdf";
            PDFUtil.splitPdf2Jpg(originalPdf, tempDir, 72);
        }

//        table();
    }

}
