package com.poc.pdf.table;

import com.lowagie.text.Document;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.poc.pdf.model.SubFile;
import com.poc.pdf.model.TableVO;
import com.poc.pdf.util.PDFUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.Calendar;
import java.util.List;

public class Table2Image {

    private static final Logger logger = Logger.getLogger(Table2Image.class);


    public static void main(String[] args) throws Exception {


//        TableVO tableVO = PDFUtil.mockTableVO();

        for (int i = 0; i < 100; i++) {
            TableVO tableVO = randomTV();
            randomTableImage(tableVO);
        }
    }


    private static void randomTableImage(TableVO tableVO) throws Exception {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int min = calendar.get(Calendar.MINUTE);
        int second = calendar.get(Calendar.SECOND);
        String endFix = new StringBuilder().append(hour).append("-").append(min).append("-").append(second).toString();


        String tempPdf = String.format("temp/table-%s.pdf", endFix);
        String imageType = "png";
        String targetImg = String.format("out/table-%s.%s", endFix, imageType);
        String detailText = String.format("out/detail-%s.txt", endFix, imageType);

        float defaultWidth = 2480f;
        float defaultHeight = 3508f;
        float xOffset = 0f;
        float yOffset = 0f;
        Rectangle tempPagesize = new Rectangle(defaultWidth + xOffset, defaultHeight + yOffset);
        float marginLeft = 0f;
        float marginRight = 0f;
        float marginTop = 0f;
        float marginBottom = 0f;

        Document tempDoc = new Document(tempPagesize, marginLeft, marginRight, marginTop, marginBottom);
        // step 2
        FileOutputStream fileOutputStream = new FileOutputStream(tempPdf);
        PdfWriter writer = PdfWriter.getInstance(tempDoc, fileOutputStream);
        // step 3
        tempDoc.open();
        writer.setSpaceCharRatio(25f);

        //step add content
        PdfPTable table = PDFUtil.generatePDFTable(tableVO);
        tempDoc.add(table);

        float totalHeight = table.getTotalHeight();
        float totalWidth = table.getTotalWidth();
        System.out.println("totalHeight:" + totalHeight);
        System.out.println("totalWidth:" + totalWidth);

        StringBuilder detailBuilder = new StringBuilder();
        detailBuilder.append("w:").append(totalWidth).append(",");
        detailBuilder.append("h:").append(totalHeight);
        IOUtils.write(detailBuilder.toString(), new FileOutputStream(new File(detailText)));

        tempDoc.close();

        String userDir = System.getProperty("user.dir");
        String tempDir = userDir + "/temp/";
        String originalPdf = userDir + "/" + tempPdf;
        List<SubFile> list = PDFUtil.splitPdf2Jpg(originalPdf, tempDir);

        System.out.println(list);
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("no image found");
            return;
        }
        SubFile imageFile = list.get(0);


        File tempImageFile = new File(imageFile.getName());
        FileInputStream fis = new FileInputStream(tempImageFile);
        BufferedImage image = ImageIO.read(fis);

        double scale = image.getWidth() / 2480.0d;

        BufferedImage spliter = image.getSubimage(0, 0, (int) totalWidth, (int) totalHeight);
        Graphics2D gr = spliter.createGraphics();
        gr.drawImage(image, 0, 0, (int) totalWidth, (int) totalHeight, 0, 0, (int) (totalWidth * scale), (int) (totalHeight * scale), null);
        gr.dispose();

        ImageIO.write(spliter, imageType, new File(targetImg));

        fis.close();
        tempImageFile.delete();
        File tempPdfFile = new File(tempPdf);
        tempPdfFile.delete();
    }


    interface Constant {
        int max = 8;
        int min = 4;

        int numberOnly = 50;
        int stringOnly = 80;
        int mix = 100;
    }


    private static String randomStr() {
        double random = Math.random() * 100d;
        int length = (int) (Math.random() * 100d % Constant.max);
        if (length < Constant.min && length > 0) {
            length = Constant.min;
        } else if (length == 0) {
            length = Constant.max;
        }

        if (random <= Constant.numberOnly) {
            return RandomStringUtils.random(length, false, true);
        } else if (random <= Constant.stringOnly) {
            return RandomStringUtils.random(length, true, false);
        }
        return RandomStringUtils.random(length, true, true);
    }

    private static TableVO randomTV() {
        int defCol = 6;
        int maxRow = 6;
        int minRow = 2;
        int length = (int) (Math.random() * 100d % maxRow);
        if (length < minRow && length > 0) {
            length = minRow;
        } else if (length == 0) {
            length = maxRow;
        }
        StringBuilder bodyBuilder = new StringBuilder();
        for (int row = 0; row < length; row++) {
            StringBuilder rowBuilder = new StringBuilder();
            for (int col = 0; col < defCol; col++) {
                rowBuilder.append(randomStr()).append(" ");
            }
            rowBuilder.deleteCharAt(rowBuilder.lastIndexOf(" ")).append("\n");
            bodyBuilder.append(rowBuilder.toString());
        }

        return PDFUtil.createdTableVO(PDFUtil.Constant.headerStr, bodyBuilder.toString());

    }

}
