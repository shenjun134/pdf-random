package com.poc.pdf.table;

import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.poc.pdf.enums.FieldType;
import com.poc.pdf.model.*;
import com.poc.pdf.util.FileUtil;
import com.poc.pdf.util.PDFUtil;
import com.poc.pdf.util.RandomUtil;
import com.poc.pdf.util.VOUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Table2Image_1 {

    private static final Logger logger = Logger.getLogger(Table2Image_1.class);


    public static void main(String[] args) throws Exception {
        process2(0, 1);
    }

    public static void process(int count) throws Exception {
        File tempFile = new File("temp");
        if (!tempFile.exists()) {
            tempFile.mkdir();
        }
        File outFile = new File("out-1");
        if (!outFile.exists()) {
            outFile.mkdir();
        }
        for (int i = 0; i < count; i++) {
            TableVO tableVO = randomTV();
            randomTableImage(tableVO, i);
        }
    }

    public static void process2(int begin, int end) throws Exception {
        File tempFile = new File("temp");
        if (!tempFile.exists()) {
            tempFile.mkdir();
        }
        File outFile = new File("out-1");
        if (!outFile.exists()) {
            outFile.mkdir();
        }
        for (int i = begin; i < end; i++) {
            TableVO tableVO = randomTV();
            randomTableImage(tableVO, i);
        }
    }


    private static void randomTableImage(TableVO tableVO, int index) throws Exception {
//        Calendar calendar = Calendar.getInstance();
//        int hour = calendar.get(Calendar.HOUR_OF_DAY);
//        int min = calendar.get(Calendar.MINUTE);
//        int second = calendar.get(Calendar.SECOND);
//        String endFix = new StringBuilder().append(hour).append("-").append(min).append("-").append(second).toString();

        String endFix = "" + index;


        String tempPdf = String.format("temp/table-%s.pdf", endFix);
        String imageType = "jpg";
        String targetImg = String.format("out-1/table-%s.%s", endFix, imageType);
        String detailText = String.format("out-1/detail-%s.txt", endFix, imageType);
        String tableStructureText = String.format("out-1/structure-%s.txt", endFix, imageType);

        float defaultWidth = 1728f;
        float defaultHeight = 1078f;
        float xOffset = 0f;
        float yOffset = 0f;
        Rectangle tempPagesize = new Rectangle(defaultWidth + xOffset, defaultHeight + yOffset);
        float marginLeft = 0f;
        float marginRight = 0f;
        float marginTop = 0f;
        float marginBottom = 0f;

        Document tempDoc = new Document(tempPagesize, marginLeft, marginRight, marginTop, marginBottom);
        // step 2
        FileOutputStream fileOutputStream = null;
        float totalHeight = 0;
        float totalWidth = 0;
        try {
            fileOutputStream = new FileOutputStream(tempPdf);
            PdfWriter writer = PdfWriter.getInstance(tempDoc, fileOutputStream);
            // step 3
            tempDoc.open();
            writer.setSpaceCharRatio(25f);

            //step add content
            PdfPTable table = generatePDFTable(tableVO);
            tempDoc.add(table);

            totalHeight = table.getTotalHeight();
            totalWidth = table.getTotalWidth();
            double maxWidth_ = 0.85 *defaultWidth;

            if(maxWidth_ < totalWidth){
                System.out.println(" ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~ " + index);
            }

            TableStructureVO tableStructureVO = PDFUtil.toStruction(table, tableVO);

            FileUtil.write(VOUtil.tableStructionVO2Str(tableStructureVO), tableStructureText);
            FileUtil.write(VOUtil.tableVO2Str(tableVO), detailText);
        } finally {
//        writer.close();
            tempDoc.close();
            fileOutputStream.close();
        }

        String userDir = System.getProperty("user.dir");
        String tempDir = userDir + "/temp/";
        String originalPdf = userDir + "/" + tempPdf;
        List<SubFile> list = PDFUtil.splitPdf2Jpg(originalPdf, tempDir);
        if (CollectionUtils.isEmpty(list)) {
            logger.warn("no image found");
            return;
        }
        SubFile imageFile = list.get(0);

        BufferedImage image = FileUtil.readImageAndDele(imageFile.getName());

        double scale = image.getWidth() / defaultWidth;

        BufferedImage spliter = image.getSubimage(0, 0, (int) totalWidth, (int) totalHeight);
        Graphics2D gr = spliter.createGraphics();
        gr.drawImage(image, 0, 0, (int) totalWidth, (int) totalHeight, 0, 0, (int) (totalWidth * scale), (int) (totalHeight * scale), null);
        gr.dispose();
        image = null;

        ImageIO.write(spliter, imageType, new File(targetImg));


        File tempPdfFile = new File(tempPdf);
        tempPdfFile.delete();
        tempPdfFile = null;

        System.out.println("----------------- random table:" + targetImg);
    }


    interface Constant {
        int borderWith = 2;

    }

    private static RowVO header = new RowVO();

    static {
        header.add(new CellVO("Agreement ID").setValueX(Element.ALIGN_RIGHT).setValueType(FieldType.AGREEMENT).setWidth(4));
        header.add(new CellVO("CP").setValueX(Element.ALIGN_LEFT).setValueDef("ICI").setWidth(2));
        header.add(new CellVO("Fund Account").setValueX(Element.ALIGN_LEFT).setValueType(FieldType.FUND).setWidth(3));
        header.add(new CellVO("Transaction Type").setValueX(Element.ALIGN_LEFT).setValueType(FieldType.TXN_TYPE).setWidth(10));
        header.add(new CellVO("Transfer Out Fund").setValueX(Element.ALIGN_LEFT).setValueType(FieldType.W_FUND).setWidth(6));
        header.add(new CellVO("Transfer In Fund").setValueX(Element.ALIGN_LEFT).setValueType(FieldType.W_FUND).setWidth(6));
        header.add(new CellVO("Trade Date").setValueX(Element.ALIGN_RIGHT).setValueType(FieldType.DATE).setWidth(5));
        header.add(new CellVO("Value Date").setValueX(Element.ALIGN_RIGHT).setValueType(FieldType.DATE).setWidth(5));
        header.add(new CellVO("NOMINAL").setValueX(Element.ALIGN_RIGHT).setValueType(FieldType.NUMBER).setWidth(7));
        header.add(new CellVO("ISIN").setValueX(Element.ALIGN_LEFT).setValueType(FieldType.ISIN).setWidth(8));
        header.add(new CellVO("Settl Location").setValueX(Element.ALIGN_LEFT).setValueType(FieldType.LOCATION).setWidth(4));
        header.add(new CellVO("Reference").setValueX(Element.ALIGN_LEFT).setValueType(FieldType.REFERENCE).setWidth(4));
        header.add(new CellVO("Type").setValueX(Element.ALIGN_LEFT).setValueType(FieldType.TYPE).setWidth(4));
        header.add(new CellVO("Additional Info:").setValueX(Element.ALIGN_LEFT).setValueDef("FOP-Collateral movement").setWidth(6));
        header.add(new CellVO("Broker Code").setValueX(Element.ALIGN_MIDDLE).setValueType(FieldType.BROKER).setWidth(3));
    }


    private static TableVO randomTV() {
        List<RowVO> bodyList = new ArrayList<>();
        int maxRow = 9;
        int minRow = 6;
        int length = (int) (Math.random() * 100d % maxRow);
        if (length < minRow && length > 0) {
            length = minRow;
        } else if (length == 0) {
            length = maxRow;
        }
        for (int i = 0; i < length; i++) {
            RowVO body = new RowVO();
            for (int j = 0; j < header.getList().size(); j++) {
                CellVO headVO = header.getList().get(j);
                String value = headVO.getValueDef();
                if (StringUtils.isBlank(value)) {
                    value = RandomUtil.randomStr(headVO.getValueType());
                }
                CellVO colVo = new CellVO(value).setxAlign(headVO.getValueX()).setyAlign(headVO.getValueY());
                body.add(colVo);
            }
            bodyList.add(body);
        }
        return new TableVO(header, bodyList);
    }


    private static float[] getColSize(RowVO header) {
        float[] col = new float[header.getList().size()];
        for (int i = 0; i < col.length; i++) {
            col[i] = header.getList().get(i).getWidth();
        }
        return col;
    }

    public static PdfPTable generatePDFTable(TableVO tableVO) {
        String space = StringUtils.leftPad("", 1);
        float paddingBottom = 5f;
        float paddingLeft = 30f;
        float paddingTop = 20f;
        BaseFont bf1 = null;
        try {
            bf1 = BaseFont.createFont("Helvetica"
                    , "UTF-8", BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        com.lowagie.text.Font font = new com.lowagie.text.Font(bf1);
        int red = 00;
        int green = 00;
        int blue = 00;
        font.setColor(red, green, blue);
//        font.setFamily("Abhaya Libre");
        font.setSize(16f);
        font.setStyle(Font.NORMAL);

        float[] columnWidths = getColSize(header);

        int colSize = tableVO.cloSize();
        PdfPTable table = new PdfPTable(columnWidths);
        table.setRunDirection(1);
        table.setHorizontalAlignment(0);
        table.setWidthPercentage(RandomUtil.randomInt(83, 78));
        table.getDefaultCell().setUseAscender(true);
        table.getDefaultCell().setUseDescender(true);


        /**
         * set header
         */
        for (int i = 0; i < colSize; i++) {
            CellVO cellVO = tableVO.getHeader().getList().get(i);
            PdfPCell header = new PdfPCell();
            header.setBorderWidth(0);
            String value = cellVO.getValue().toString();
            header.setHorizontalAlignment(cellVO.getxAlign());
            header.setVerticalAlignment(cellVO.getyAlign());
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
                header.setBorderWidth(Constant.borderWith);
                String value = cellVO.getValue().toString();
                header.setHorizontalAlignment(cellVO.getxAlign());
                header.setVerticalAlignment(cellVO.getyAlign());
//                if (j == 0) {
//                    header.setBorderWidthLeft(borderWith);
//                    value = space + cellVO.getValue().toString();
//                }
//                if (j == colSize - 1) {
//                    header.setBorderWidthRight(borderWith);
//                }
//                header.setPaddingBottom(paddingBottom);
                if (i != 0) {
                    header.setBorderWidthTop(0);
                }
                if (j != colSize - 1) {
                    header.setBorderWidthRight(0);
                }
                if (isLast) {
                    header.setBorderWidthBottom(3);
                }
                header.setPaddingTop(paddingTop);
                header.setPaddingBottom(paddingBottom);
                header.setHorizontalAlignment(cellVO.getxAlign());
                header.setVerticalAlignment(cellVO.getyAlign());
                header.setIndent(2);
                Phrase phrase = new Phrase(value, font);
                header.setPhrase(phrase);
                table.addCell(header);
            }

        }


        return table;
    }

}
