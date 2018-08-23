package com.poc.pdf.base;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.poc.pdf.model.*;
import com.poc.pdf.util.FileUtil;
import com.poc.pdf.util.PDFUtil;
import com.poc.pdf.util.TableUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TableLines extends BaseLine {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(TableLines.class);


    public static final String TYPE = ".pdf";

    interface Const {
        String layoutName = "./table/";

        //        String prefix = "type-";
        String prefix = "";
    }

    public static void process() throws IOException {
        File file = new File(Const.layoutName);
        file.getParentFile().mkdirs();
        TableLayoutConfig config = new TableLayoutConfig();
        List<List<TableStructure>> listInList = new ArrayList<>();

        int size = config.getNumberOfCategory() * config.getEachCategoryTotal();

        List<FileInfo> fileNames = new ArrayList<>(size);

        List<FileInfo> signatureList = FileUtil.loadDirImages(config.getSignatureDir());

        for (int index = 0; index < config.getNumberOfCategory(); index++) {
            int begin = index * config.getDistance() + config.getBeginAt();
            List<TableStructure> resultList = getResult(config);
            listInList.add(resultList);
            String beginStr = StringUtils.leftPad("" + begin, 6, "0");
            String path = Const.layoutName + Const.prefix + beginStr + "/";
            File tempFile = new File(path);
            tempFile.mkdirs();


            for (int i = 0; i < resultList.size(); i++) {
                TableStructure temp = resultList.get(i);

                int offsite = begin + i;

                String offsiteStr = StringUtils.leftPad("" + offsite, 6, "0");
                String fileName = offsiteStr;
                String fullPath = path + fileName + TYPE;

                fileNames.add(new FileInfo(fileName, path, TYPE));

                createPdf(fullPath, fileName, config, temp, signatureList);

                TableStructureText structureText = TableUtil.structure2Xml(temp, offsiteStr, config);
                String xmlFullPath = path + fileName + ".xml";
                String txtFullPath = path + fileName + ".txt";

                FileUtil.write(structureText.getStructureXml(), xmlFullPath);
                FileUtil.write(structureText.getText().toString(), txtFullPath);
            }
        }

        for (FileInfo fileInfo : fileNames) {
            String fullPath = fileInfo.getParentPath() + fileInfo.getFileName() + fileInfo.getType();
            String originalPdf = fullPath;
            PDFUtil.splitPdf2Jpg(originalPdf, fileInfo.getParentPath(), 72);
        }

//        for (int index = 0; index < config.getNumberOfCategory(); index++) {
//            String path = Const.layoutName + "type-" + index + "/";
//            List<TableStructure> resultList = listInList.get(index);
//            for (int i = 0; i < resultList.size(); i++) {
//                String fileName = "type-" + index + "-" + i;
//                String fullPath = path + fileName + TYPE;
//
//                String originalPdf = fullPath;
//                PDFUtil.splitPdf2Jpg(originalPdf, path, 72);
//            }
//        }
    }

    public static void main(String args[]) throws IOException {
        process();
    }

    public static List<TableStructure> getResult(TableLayoutConfig config) {
        List<TableStructure> resultList = new ArrayList<>();
        TableStructure structure = TableUtil.getFulfillStructure(config);
        for (int i = 0; i < config.getEachCategoryTotal(); i++) {
            resultList.add(structure);
        }
        return resultList;
    }

    public static void createPdf(String dest, String fileName, TableLayoutConfig config, TableStructure structure, List<FileInfo> signatureList) throws IOException {
        //Initialize PDF document
        PdfWriter writer = new PdfWriter(dest);
        PdfDocument pdf = new PdfDocument(writer);
        Rectangle tempPagesize = new Rectangle(config.getTotalWidth(), config.getTotalHeight());
        PageSize ps = new PageSize(tempPagesize);
        PdfPage page = pdf.addNewPage(ps);
        PdfCanvas canvas = new PdfCanvas(page);

        //Replace the origin of the coordinate system to the center of the page
        for (Line line : structure.getLineList()) {
            canvas.setLineWidth(line.getWidth()).setStrokeColor(config.getBorderColor());
            drawLine(canvas, line, config);
            canvas.stroke();
        }

        //fill in text
        for (TableCell rectangle : structure.getCellList()) {
            int fontSize = structure.getFontSize();
            float leadingSize = structure.getLineHeight();
            List<String> textList = TableUtil.randomString(rectangle, fontSize, config);
            int x = rectangle.getReal1(config.getTotalHeight()).getX() + structure.getCellLeftPadding();
            int y = rectangle.getReal1(config.getTotalHeight()).getY() - structure.getCellTopPadding();

            String fontFamily = structure.getFontFamily();
            canvas.beginText()
                    .setFontAndSize(PdfFontFactory.createFont(fontFamily), fontSize)
//                    .setStrokeColor(TableUtil.randomColor())
//                    .setColor(TableUtil.randomColor(), false)
                    .setLeading(leadingSize)
                    .moveText(x, y);
            for (String text : textList) {
                canvas.newlineText();
                canvas.showText(text);
//                canvas.newlineShowText(text);
                logger.debug("text:" + text);
            }
            canvas.endText();

            rectangle.setTextList(textList);
        }

        List<Line> markLineList = TableUtil.createMarkRect(structure, config);
        for (Line line : markLineList) {
            canvas.setLineWidth(line.getWidth()).setStrokeColor(config.getMarkBorderColor());
            drawLine(canvas, line, config);
            canvas.stroke();
        }

        //make noise
        int padding = 20;

        markNoiseTable(config, canvas, structure);
        makeNoiseTop(config, padding, canvas, fileName, structure);
        makeNoiseBottom(config, padding, canvas, structure);
        makeNoiseLeft(config, padding, canvas, structure);
        makeNoiseRight(config, padding, canvas, structure);

        insertSignature(config, signatureList, canvas, structure, pdf);

        //Close document
        pdf.close();
    }


    private static void markNoiseTable(TableLayoutConfig config, PdfCanvas canvas, TableStructure tableStructure) {
        boolean noise = TableUtil.randomTF(config.getNoiseTableProbability());
        if (!noise) {
            return;
        }
        canvas.setStrokeColor(config.getNoiseBorderColor());
        List<Line> lineList = TableUtil.randomLine(config.getNoiseTableMaxCount(), config.getTotalWidth(), config.getTotalHeight(), config.getBorderMaxWidth());
        for (Line line : lineList) {
            float unitsOn = 0;
            float phase = 0;
            if (TableUtil.randomTF(50)) {
                unitsOn = Math.round(10);
                phase = Math.round(10);
            }
            if (unitsOn > 0 && phase > 0) {
                canvas.setLineDash(unitsOn, phase);
            }
            canvas.setLineWidth(line.getWidth());
            drawLine(canvas, line, config);
            canvas.stroke();
        }
        canvas.setStrokeColor(Color.BLACK);
    }


    private static void makeNoiseBottom(TableLayoutConfig config, int padding, PdfCanvas canvas, TableStructure structure) throws IOException {
        boolean noise = TableUtil.randomTF(config.getNoiseBottomProbability());
        structure.setNoiseBottom(noise);
        if (!noise) {
            return;
        }
        int fontSize = TableUtil.fontSize();
        float leadingSize = 1.2f * fontSize;
        int bottom = config.getTotalHeight() - structure.getStartPoint().getY() - structure.getTableHeight();
        int row = (int) ((bottom - 1 * padding) / leadingSize);

        int rowRd = TableUtil.randomRange(row, 1) - 2;

        int gridWidth = config.getTotalWidth() - config.getPaddingLeft() - config.getPaddingRight();
        String bottomText = TableUtil.randomText(gridWidth, fontSize);
        String fontFamily = TableUtil.fontProgramNoneBold();
        int bottomX = config.getPaddingLeft() - padding;
        int bottomY = (int) (rowRd * 1 * leadingSize) + padding;
        PdfFont baseFont = PdfFontFactory.createFont(fontFamily);
        canvas.beginText()
                .setFontAndSize(baseFont, fontSize)
                .setLeading(leadingSize)
                .moveText(bottomX, bottomY);
        canvas.newlineShowText(bottomText);
        float maxLength = 0;
        for (int i = 0; i < rowRd; i++) {
            String temp = TableUtil.randomText(gridWidth / 1, fontSize);
            float length = baseFont.getWidth(temp, fontSize);
            if (length > maxLength) {
                maxLength = length;
            }
            canvas.newlineShowText(temp);
        }

        canvas.endText();
        int x1 = bottomX;
        int y1 = config.getTotalHeight() - bottomY;
        com.poc.pdf.model.Rectangle rectangle = TableUtil.createRect(rowRd, leadingSize, maxLength, x1, y1, config);

        structure.setBottomTextRect(rectangle);
    }

    private static void makeNoiseTop(TableLayoutConfig config, int padding, PdfCanvas canvas, String fileName, TableStructure structure) throws IOException {
        boolean noise = TableUtil.randomTF(config.getNoiseTopProbability());
        structure.setNoiseTop(noise);
        if (!noise) {
            return;
        }
        int gridWidth = config.getTotalWidth() - config.getPaddingLeft() - config.getPaddingRight();
        int fontSizeTop = TableUtil.fontSize();

        float leadingSizeTop = 1.2f * fontSizeTop;
        int top = structure.getStartPoint().getY();
        int row = (int) ((top - padding) / leadingSizeTop);
        int rowRd = TableUtil.randomRange(row, 3);

        String header = fileName.toUpperCase() + " ---- " + new Date();
        String fontFamilyTop = TableUtil.fontProgramSoft();
        int topX = config.getPaddingLeft() + 2 * padding;
        int topY = config.getTotalHeight() - padding;
        PdfFont baseFont = PdfFontFactory.createFont(fontFamilyTop);
        canvas.beginText()
                .setFontAndSize(baseFont, fontSizeTop)
                .setLeading(leadingSizeTop)
                .moveText(topX, topY);
        canvas.newlineShowText(header);
        float maxLength = 0;
        for (int i = 1; i < rowRd; i++) {
            String temp = TableUtil.randomText(gridWidth / 1, fontSizeTop);
            float length = baseFont.getWidth(temp, fontSizeTop);
            if (length > maxLength) {
                maxLength = length;
            }
            canvas.newlineShowText(temp);
        }
        canvas.endText();

        int x1 = topX;
        int y1 = config.getTotalHeight() - topY;

        com.poc.pdf.model.Rectangle rectangle = TableUtil.createRect(rowRd, leadingSizeTop, maxLength, x1, y1, config);
        structure.setTopTextRect(rectangle);
    }

    private static void makeNoiseLeft(TableLayoutConfig config, int padding, PdfCanvas canvas, TableStructure structure) throws IOException {
        boolean noise = TableUtil.randomTF(config.getNoiseLeftProbability());
        structure.setNoiseLeft(noise);
        if (!noise) {
            return;
        }
        int fontSize = TableUtil.smallFontSize();
        float leadingSize = 1.2f * fontSize;
        int left = structure.getStartPoint().getX();
        int row = (int) ((left - 2 * padding) / 1.5 / fontSize);
        int rowRd = TableUtil.randomRange(row, 1);


        int gridWidth = config.getTotalHeight() - config.getPaddingTop() - config.getPaddingBottom();
        String leftText = TableUtil.randomText((int) (gridWidth / 1.5f), fontSize);
        String fontFamily = TableUtil.fontProgramSoft();
        int beginLeft = fontSize / 2;
        int leftX = beginLeft;
        int leftY = config.getTotalHeight() - 2 * padding;
        showVerticalText(leftX, leftY, leftText, canvas, fontFamily, fontSize, leadingSize);
        for (int i = 1; i < rowRd; i++) {
            leftX = beginLeft + i * fontSize;
            leftText = TableUtil.randomText((int) (gridWidth / 1.5f), fontSize);
            showVerticalText(leftX, leftY, leftText, canvas, fontFamily, fontSize, leadingSize);
        }
    }

    private static void showVerticalText(int x, int y, String text, PdfCanvas canvas, String fontFamily, int fontSize, float leadingSize) throws IOException {
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(fontFamily), fontSize)
                .setLeading(leadingSize)
                .moveText(x, y);
        int size = text.length();
        for (int i = 0; i < size; i++) {
            int begin = i;
            int end = begin + 1;
            String temp = text.substring(begin, end);
            canvas.newlineShowText(temp);
//            logger.error(MessageFormat.format("showVerticalText {0}", temp));
        }
        canvas.endText();
    }

    private static void makeNoiseRight(TableLayoutConfig config, int padding, PdfCanvas canvas, TableStructure structure) throws IOException {
        boolean noise = TableUtil.randomTF(config.getNoiseRightProbability());
        structure.setNoiseRight(noise);
        if (!noise) {
            return;
        }
        int fontSize = TableUtil.smallFontSize();
        float leadingSize = 1.2f * fontSize;
        int left = config.getTotalWidth() - structure.getStartPoint().getX() - structure.getTableWidth();
        int row = (int) ((left - 2 * padding) / 1.5 / fontSize);
        int rowRd = TableUtil.randomRange(row, 1);

        int gridWidth = config.getTotalHeight() - config.getPaddingTop() - config.getPaddingBottom();
        String leftText = TableUtil.randomText((int) (gridWidth / 1.5f), fontSize);
        String fontFamily = TableUtil.fontProgramSoft();

        int beginLeft = (int) (fontSize / 1.5);
        int leftX = config.getTotalWidth() - beginLeft;
        int leftY = config.getTotalHeight() - 2 * padding;
        showVerticalText(leftX, leftY, leftText, canvas, fontFamily, fontSize, leadingSize);
        for (int i = 1; i < rowRd; i++) {
            leftX = config.getTotalWidth() - beginLeft - i * fontSize;
            leftText = TableUtil.randomText((int) (gridWidth / 1.5f), fontSize);
            showVerticalText(leftX, leftY, leftText, canvas, fontFamily, fontSize, leadingSize);
        }
    }

    private static void insertSignature(TableLayoutConfig config, List<FileInfo> signatureList, PdfCanvas canvas, TableStructure structure, PdfDocument doc) throws IOException {
        List<com.poc.pdf.model.Rectangle> blankRect = new ArrayList<>();
        if (structure.isNoiseTop()) {
            RectangleLine rectangleLine = new RectangleLine(structure.getTopTextRect());
            for (Line line : rectangleLine.getLineList()) {
                canvas.setLineWidth(line.getWidth()).setStrokeColor(config.getMarkBorderColor()).setLineDash(10, 10);
                drawLine(canvas, line, config);
                canvas.stroke();
            }
        }

        if (structure.isNoiseBottom()) {
            RectangleLine rectangleLine = new RectangleLine(structure.getBottomTextRect());
            for (Line line : rectangleLine.getLineList()) {
                canvas.setLineWidth(line.getWidth()).setStrokeColor(config.getMarkBorderColor());
                drawLine(canvas, line, config);
                canvas.stroke();
            }
        }
        if (config.getSignatureMax() == 0) {
            return;
        }
        blankRect.add(getBlankTopRect(config, structure));
        blankRect.add(getBlankBottomRect(config, structure));
        com.poc.pdf.model.Rectangle leftBlank = getBlankLeftRect(config, structure);
        com.poc.pdf.model.Rectangle rightBlank = getBlankRightRect(config, structure);
        if (leftBlank != null) {
            blankRect.add(leftBlank);
        }
        if (rightBlank != null) {
            blankRect.add(rightBlank);
        }

        drawBlankArea(blankRect, canvas, config);

        List<Point> pointList = new ArrayList<>();
        for (com.poc.pdf.model.Rectangle rect : blankRect) {
            List<Point> tempPL = randomStartPoint(rect, config);
            if (CollectionUtils.isNotEmpty(tempPL)) {
                pointList.addAll(tempPL);
            }
        }
        drawImage(pointList, signatureList, canvas, config);
    }


    private static com.poc.pdf.model.Rectangle getBlankTopRect(TableLayoutConfig config, TableStructure structure) {
        Point point1 = new Point();
        if (structure.isNoiseTop()) {
            Point a = structure.getTopTextRect().getPoint1();
            Point c = structure.getTopTextRect().getPoint2();
            Point b = new Point(c.getX(), a.getY());
            Point d = new Point(a.getX(), c.getY());
            point1 = b;
        } else {
            point1.setX(config.getPaddingLeft());
            point1.setY(10);
        }
        int y2 = structure.getStartPoint().getY() - 10;
        int x2 = config.getTotalWidth() - 10;
        Point point2 = new Point(x2, y2);
        com.poc.pdf.model.Rectangle blank = new com.poc.pdf.model.Rectangle();
        blank.setPoint1(point1);
        blank.setPoint2(point2);
        return blank;
    }

    private static com.poc.pdf.model.Rectangle getBlankLeftRect(TableLayoutConfig config, TableStructure structure) {
        if (structure.isNoiseLeft()) {
            return null;
        }
        Point point1 = new Point(0, structure.getStartPoint().getY() + 10);
        int y2 = structure.getStartPoint().getY() + structure.getTableHeight() - 10;
        int x2 = structure.getStartPoint().getX() - 10;
        Point point2 = new Point(x2, y2);
        com.poc.pdf.model.Rectangle blank = new com.poc.pdf.model.Rectangle();
        blank.setPoint1(point1);
        blank.setPoint2(point2);
        return blank;
    }

    private static com.poc.pdf.model.Rectangle getBlankRightRect(TableLayoutConfig config, TableStructure structure) {
        if (structure.isNoiseRight()) {
            return null;
        }
        Point point1 = new Point(structure.getStartPoint().getX() + structure.getTableWidth() + 10, structure.getStartPoint().getY() + 10);
        int y2 = structure.getStartPoint().getY() + structure.getTableHeight() - 10;
        int x2 = config.getTotalWidth() - 10;
        Point point2 = new Point(x2, y2);
        com.poc.pdf.model.Rectangle blank = new com.poc.pdf.model.Rectangle();
        blank.setPoint1(point1);
        blank.setPoint2(point2);
        return blank;
    }

    private static com.poc.pdf.model.Rectangle getBlankBottomRect(TableLayoutConfig config, TableStructure structure) {
        Point point1 = new Point();
        point1.setY(structure.getStartPoint().getY() + structure.getTableHeight() + 10);
        if (structure.isNoiseBottom()) {
            Point a = structure.getBottomTextRect().getPoint1();
            Point c = structure.getBottomTextRect().getPoint2();
            Point b = new Point(c.getX(), a.getY());
            Point d = new Point(a.getX(), c.getY());
            point1.setX(b.getX());
        } else {
            point1.setX(config.getPaddingLeft());
        }

        int y2 = config.getTotalHeight() - 10;
        int x2 = config.getTotalWidth() - 10;
        Point point2 = new Point(x2, y2);
        com.poc.pdf.model.Rectangle blank = new com.poc.pdf.model.Rectangle();
        blank.setPoint1(point1);
        blank.setPoint2(point2);
        return blank;
    }


}