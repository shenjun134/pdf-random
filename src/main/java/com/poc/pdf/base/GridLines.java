package com.poc.pdf.base;

import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.poc.pdf.model.*;
import com.poc.pdf.util.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.*;

public class GridLines extends BaseLine {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(GridLines.class);


    public static final String TYPE = ".pdf";


    interface Const {
        String layoutName = "./layout/";
        int blankPadding = 10;
        String dateFormat = "MMM/dd/yyyy hh:mm:ss";
    }

    public static void main(String args[]) throws IOException {
        process();
    }

    public static void process() throws IOException {
        File file = new File(Const.layoutName);
        file.getParentFile().mkdirs();
        GridLayoutConfig config = new GridLayoutConfig();
        List<List<GridLayoutResult>> listInList = new ArrayList<>();

        List<FileInfo> signatureList = FileUtil.loadDirImages(config.getSignatureDir());

        int size = config.getNumberOfCategory() * config.getEachCategoryTotal();

        List<FileInfo> fileNames = new ArrayList<>(size);
        List<String> structureJsonList = getStructureJsonList(config);

        for (int index = 0; index < config.getNumberOfCategory(); index++) {
            String structureJson = null;
            if (structureJsonList != null && structureJsonList.size() - 1 >= index) {
                structureJson = structureJsonList.get(index);
            }
            List<GridLayoutResult> resultList = getResult(config, structureJson);
            listInList.add(resultList);
            String path = Const.layoutName + "type-" + index + "/";
            File tempFile = new File(path);
            tempFile.mkdirs();

            for (int i = 0; i < resultList.size(); i++) {
                GridLayoutResult temp = resultList.get(i);
                String fileName = "type-" + index + "-" + i;
                String fullPath = path + fileName + TYPE;
                fileNames.add(new FileInfo(fileName, path, TYPE));
                createPdf(fullPath, fileName, config, temp, signatureList);

                GridStructureText structureText = TableUtil.structure2Xml(temp, fileName, config);
                String xmlFullPath = path + fileName + ".xml";
                String txtFullPath = path + fileName + ".txt";
                String jsonFullPath = path + fileName + ".json";

                FileUtil.write(structureText.getStructureXml(), xmlFullPath);
                FileUtil.write(structureText.getText().toString(), txtFullPath);
                FileUtil.write(structureText.getJson().toString(), jsonFullPath);
            }
        }

        for (FileInfo fileInfo : fileNames) {
            String fullPath = fileInfo.getParentPath() + fileInfo.getFileName() + fileInfo.getType();
            String originalPdf = fullPath;
            PDFUtil.splitPdf2Jpg(originalPdf, fileInfo.getParentPath(), 72);
        }

//        for (int index = 0; index < config.getNumberOfCategory(); index++) {
//            String path = Const.layoutName + "type-" + index + "/";
//            List<GridLayoutResult> resultList = listInList.get(index);
//            for (int i = 0; i < resultList.size(); i++) {
//                String fileName = "type-" + index + "-" + i;
//                String fullPath = path + fileName + TYPE;
//
//                String originalPdf = fullPath;
//                PDFUtil.splitPdf2Jpg(originalPdf, path, 72);
//            }
//        }

    }


    /**
     * @param config
     * @return
     */
    public static List<String> getStructureJsonList(GridLayoutConfig config) {
        List<String> jsonList = new ArrayList<>();
        if (config.isFixedStructureEnable() && CollectionUtils.isNotEmpty(config.getFixedStructureJsonList())) {
            for (String jsonPath : config.getFixedStructureJsonList()) {
                try {
                    String json = FileUtils.readFileToString(new File(jsonPath));
                    jsonList.add(json);
                } catch (IOException e) {
                    logger.error("cannot find " + jsonPath + e);
                }
            }
        }
        return jsonList;
    }

    public static List<GridLayoutResult> getResult(GridLayoutConfig config, String structureJson) {
        List<GridLayoutResult> resultList = new ArrayList<>();
        config.getSplitConfigList().clear();

        GridLayoutResult result;
        if (config.isFixedStructureEnable() && StringUtils.isNotBlank(structureJson)) {
            result = TableUtil.Const.gson.fromJson(structureJson, GridLayoutResult.class);
        } else {
            result = GridLayoutUtil.randomGrid(config);
            randomBlank(result, config);
        }
//        result.printRect();
        for (int i = 0; i < config.getEachCategoryTotal(); i++) {
            GridLayoutResult temp = new GridLayoutResult();
            temp.setPaddingRight(result.getPaddingRight());
            temp.setPaddingLeft(result.getPaddingLeft());
            temp.setPaddingBottom(result.getPaddingBottom());
            temp.setPaddingTop(result.getPaddingTop());
            temp.setLineList(result.getLineList());
            temp.setRectList(result.getRectList());
            resultList.add(temp);
//            GridLayoutResult temp = GridLayoutUtil.splitLoop(config);
//            resultList.add(temp);
//            temp.printRect();
        }
        return resultList;
    }

    public static void createPdf(String dest, String fileName, GridLayoutConfig config, GridLayoutResult result, List<FileInfo> signatureList) throws IOException {
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
        Map<String, com.poc.pdf.model.Rectangle> blankMap = getBlank(result);

        for (com.poc.pdf.model.Rectangle rectangle : result.getRectList()) {
            if (rectangle.isSplit()) {
                continue;
            }
            if (rectangle.isBlank()) {
                continue;
            }
            writeText(rectangle, config, padding, canvas);
        }

        //make noise

        makeNoiseTop(config, padding, canvas, fileName, blankMap, result);
        makeNoiseBottom(config, padding, canvas, blankMap, result);
        makeNoiseLeft(config, padding, canvas, blankMap, result);
        makeNoiseRight(config, padding, canvas, blankMap, result);

        drawBlankArea(blankMap, canvas, config);

        List<com.poc.pdf.model.Rectangle> textRectList = insertSignature(blankMap, canvas, config, signatureList);
        for (com.poc.pdf.model.Rectangle rectangle : textRectList) {
            logger.info("begin to write :" + rectangle.getName());
            writeText(rectangle, config, padding, canvas);
        }
        //Close document
        pdf.close();
    }

    private static void writeText(com.poc.pdf.model.Rectangle rectangle, GridLayoutConfig config, int padding, PdfCanvas canvas) throws IOException {
        int fontSize = TableUtil.fontSize();
        float leadingSize = 1.2f * fontSize;
        List<String> textList = TableUtil.randomString(rectangle, fontSize, config);
        int x = rectangle.getReal1(config).getX() + padding;
        int y = rectangle.getReal1(config).getY() - padding;

        String fontFamily = TableUtil.fontProgram();
        canvas.beginText()
                .setFontAndSize(FontUtil.createFont(fontFamily), fontSize)
                .setLeading(leadingSize)
                .moveText(x, y);
        for (String text : textList) {
            canvas.newlineText();
            canvas.showText(text);
//                canvas.newlineShowText(text);
            logger.debug("text:" + text);
        }
        canvas.endText();

        rectangle.setText(textList);
    }

    /**
     * @param result
     * @param config
     * @return
     */
    private static void randomBlank(GridLayoutResult result, SignatureConfig config) {
        Map<String, com.poc.pdf.model.Rectangle> map = new HashMap<>();
        List<com.poc.pdf.model.Rectangle> srcRectList = new ArrayList<>();
        for (com.poc.pdf.model.Rectangle rectangle : result.getRectList()) {
            if (rectangle.isSplit()) {
                continue;
            }
            srcRectList.add(rectangle);
        }
        int rd = TableUtil.randomRange(config.getSignatureMax(), config.getSignatureMin());
        if (rd > srcRectList.size()) {
            if (srcRectList.size() == 1) {
                return;

            }
            rd = TableUtil.randomRange(srcRectList.size(), 0);
        }
        if (rd == 0) {
            return;
        }

        Collections.shuffle(srcRectList);
        for (int i = 0; i < rd; i++) {
            com.poc.pdf.model.Rectangle rectangle = srcRectList.get(i);
            map.put(rectangle.getName(), rectangle);
        }
        for (com.poc.pdf.model.Rectangle rectangle : result.getRectList()) {
            if (map.get(rectangle.getName()) != null) {
                rectangle.setBlank(true);
            }
        }

    }

    /**
     * @param result
     * @return
     */
    private static Map<String, com.poc.pdf.model.Rectangle> getBlank(GridLayoutResult result) {
        Map<String, com.poc.pdf.model.Rectangle> map = new HashMap<>();
        List<com.poc.pdf.model.Rectangle> srcRectList = new ArrayList<>();
        for (com.poc.pdf.model.Rectangle rectangle : result.getRectList()) {
            if (rectangle.isBlank()) {
                srcRectList.add(rectangle);
            }
        }
        return map;
    }


    private static void makeNoiseBottom(GridLayoutConfig config, int padding, PdfCanvas canvas, Map<String, com.poc.pdf.model.Rectangle> blankMap, GridLayoutResult result) throws IOException {
        if (!TableUtil.randomTF(config.getNoiseBottomProbability())) {
            int offsitePadding = Const.blankPadding;
            int x1 = offsitePadding;
            int y1 = (config.getTotalHeight() - result.getPaddingBottom() + offsitePadding);
            int x2 = config.getTotalWidth() - offsitePadding;
            int y2 = (config.getTotalHeight() + offsitePadding);
            Point point1 = new Point(x1, y1);
            Point point2 = new Point(x2, y2);
            com.poc.pdf.model.Rectangle blank = new com.poc.pdf.model.Rectangle();
            blank.setPoint1(point1);
            blank.setPoint2(point2);
            blank.setName("BOTTOM-BLANK");

            blankMap.put("BOTTOM-BLANK", blank);
            return;
        }
        int fontSize = TableUtil.smallFontSize();
        float leadingSize = 1.2f * fontSize;
        int gridWidth = config.getTotalWidth() - result.getPaddingLeft() - result.getPaddingRight();
        String bottomText = TableUtil.randomText(gridWidth, fontSize);
        String fontFamily = TableUtil.fontProgram();
        int bottomX = config.getPaddingLeft() - padding;
        int bottomY = 2 * fontSize;
        PdfFont baseFont = FontUtil.createFont(fontFamily);
        canvas.beginText()
                .setFontAndSize(baseFont, fontSize)
                .setLeading(leadingSize)
                .moveText(bottomX, bottomY);
        canvas.newlineShowText(bottomText);
        canvas.endText();

        float length = baseFont.getWidth(bottomText, fontSize);
        int offsitePadding = Const.blankPadding;
        int x1 = bottomX + (int) length + offsitePadding;
        int y1 = (config.getTotalHeight() - result.getPaddingBottom() + offsitePadding);
        int x2 = config.getTotalWidth() - offsitePadding;
        int y2 = (config.getTotalHeight() + offsitePadding);
        Point point1 = new Point(x1, y1);
        Point point2 = new Point(x2, y2);
        com.poc.pdf.model.Rectangle blank = new com.poc.pdf.model.Rectangle();
        blank.setPoint1(point1);
        blank.setPoint2(point2);
        blank.setName("BOTTOM-BLANK");

        blankMap.put("BOTTOM-BLANK", blank);
    }

    private static void makeNoiseTop(GridLayoutConfig config, int padding, PdfCanvas canvas, String fileName, Map<String, com.poc.pdf.model.Rectangle> blankMap, GridLayoutResult result) throws IOException {
        if (!TableUtil.randomTF(config.getNoiseTopProbability())) {
            int offsitePadding = Const.blankPadding;
            int x1 = offsitePadding;
            int y1 = (offsitePadding);
            int x2 = config.getTotalWidth() - offsitePadding;
            int y2 = (result.getPaddingTop() - offsitePadding);
            Point point1 = new Point(x1, y1);
            Point point2 = new Point(x2, y2);
            com.poc.pdf.model.Rectangle blank = new com.poc.pdf.model.Rectangle();
            blank.setPoint1(point1);
            blank.setPoint2(point2);
            blank.setName("TOP-BLANK");
            blankMap.put("TOP-BLANK", blank);
            return;
        }

        int gridWidth = config.getTotalWidth() - result.getPaddingLeft() - result.getPaddingRight();
        int fontSizeTop = TableUtil.smallFontSize() * 2;
//        int fontSizeTop = 72;
        float leadingSizeTop = 1.2f * fontSizeTop;
        String header = fileName + " " + DateFormatUtils.format(new Date(), Const.dateFormat);
        String fontFamilyTop = TableUtil.fontProgramSoft();
        int topX = result.getPaddingLeft() + 2 * padding;
        int topY = config.getTotalHeight() - padding;
        PdfFont baseFont = FontUtil.createFont(fontFamilyTop);
        String header2 = getLimitMsg(baseFont, header, gridWidth, fontSizeTop);
        String header3 = getLimitMsg(baseFont, header, gridWidth, fontSizeTop);
        canvas.beginText()
                .setFontAndSize(baseFont, fontSizeTop)
                .setLeading(leadingSizeTop)
                .moveText(topX, topY);
        canvas.newlineShowText(header);
        canvas.newlineShowText(header2);
        canvas.newlineShowText(header3);
        canvas.endText();
        float length = baseFont.getWidth(header, fontSizeTop);
        float temp = baseFont.getWidth(header2, fontSizeTop);
        if (temp > length) {
            length = temp;
        }
        temp = baseFont.getWidth(header3, fontSizeTop);
        if (temp > length) {
            length = temp;
        }
        int offsitePadding = Const.blankPadding;
        int x1 = topX + (int) length + offsitePadding;
        int y1 = (offsitePadding);
        int x2 = config.getTotalWidth() - offsitePadding;
        int y2 = (result.getPaddingTop() - offsitePadding);
        Point point1 = new Point(x1, y1);
        Point point2 = new Point(x2, y2);
        com.poc.pdf.model.Rectangle blank = new com.poc.pdf.model.Rectangle();
        blank.setPoint1(point1);
        blank.setPoint2(point2);
        blank.setName("TOP-BLANK");
        blankMap.put("TOP-BLANK", blank);
    }

    private static String getLimitMsg(PdfFont baseFont, String base, int width, int fontSize) {
        float limit = baseFont.getWidth(base, fontSize);
        String temp = TableUtil.randomText(width, fontSize);
        if (baseFont.getWidth(temp, fontSize) <= limit) {
            return temp;
        }
        int count = 3;
        while (count > 0) {
            temp = TableUtil.randomText(width, fontSize);
            if (baseFont.getWidth(temp, fontSize) <= limit) {
                return temp;
            }
            count--;
        }
        return TableUtil.randomText(width / 2, fontSize);
    }

    private static void makeNoiseLeft(GridLayoutConfig config, int padding, PdfCanvas canvas, Map<String, com.poc.pdf.model.Rectangle> blankMap, GridLayoutResult result) throws IOException {
        if (!TableUtil.randomTF(config.getNoiseLeftProbability())) {
            int offsitePadding = Const.blankPadding;
            int x1 = 0 + offsitePadding;
            int y1 = (offsitePadding + result.getPaddingTop());
            int x2 = result.getPaddingLeft() - offsitePadding;
            int y2 = (config.getTotalHeight() - offsitePadding - result.getPaddingBottom());
            Point point1 = new Point(x1, y1);
            Point point2 = new Point(x2, y2);
            com.poc.pdf.model.Rectangle blank = new com.poc.pdf.model.Rectangle();
            blank.setPoint1(point1);
            blank.setPoint2(point2);
            blank.setName("LEFT-BLANK");
            blankMap.put("LEFT-BLANK", blank);
            return;
        }
        int fontSize = TableUtil.smallFontSize();
        float leadingSize = 1.2f * fontSize;
        int gridWidth = config.getTotalHeight() - result.getPaddingTop() - result.getPaddingBottom();
        String leftText = TableUtil.randomText((int) (gridWidth / 1.5f), fontSize);
        String fontFamily = TableUtil.fontProgramSoft();
        int leftX = 1 * fontSize;
        int leftY = config.getTotalHeight() - 2 * padding;
        canvas.beginText()
                .setFontAndSize(FontUtil.createFont(fontFamily), fontSize)
                .setLeading(leadingSize)
                .moveText(leftX, leftY);
        int size = leftText.length();
        for (int i = 0; i < size; i++) {
            canvas.newlineShowText("" + leftText.charAt(i));

        }
        canvas.endText();
    }

    private static void makeNoiseRight(GridLayoutConfig config, int padding, PdfCanvas canvas, Map<String, com.poc.pdf.model.Rectangle> blankMap, GridLayoutResult result) throws IOException {
        if (!TableUtil.randomTF(config.getNoiseRightProbability())) {
            int offsitePadding = Const.blankPadding;
            int x1 = config.getTotalWidth() - result.getPaddingRight() + offsitePadding;
            int y1 = (offsitePadding + result.getPaddingTop());
            int x2 = config.getTotalWidth() - offsitePadding;
            int y2 = (config.getTotalHeight() - offsitePadding - result.getPaddingBottom());
            Point point1 = new Point(x1, y1);
            Point point2 = new Point(x2, y2);
            com.poc.pdf.model.Rectangle blank = new com.poc.pdf.model.Rectangle();
            blank.setPoint1(point1);
            blank.setPoint2(point2);
            blank.setName("RIGHT-BLANK");
            blankMap.put("RIGHT-BLANK", blank);
            return;
        }
        int fontSize = TableUtil.smallFontSize();
        float leadingSize = 1.2f * fontSize;
        int gridWidth = config.getTotalHeight() - result.getPaddingTop() - config.getPaddingBottom();
        String leftText = TableUtil.randomText((int) (gridWidth / 1.5f), fontSize);
        String fontFamily = TableUtil.fontProgramSoft();
        int leftX = config.getTotalWidth() - 2 * fontSize;
        int leftY = config.getTotalHeight() - 2 * padding;
        canvas.beginText()
                .setFontAndSize(FontUtil.createFont(fontFamily), fontSize)
                .setLeading(leadingSize)
                .moveText(leftX, leftY);
        int size = leftText.length();
        for (int i = 0; i < size; i++) {
            canvas.newlineShowText("" + leftText.charAt(i));

        }
        canvas.endText();
    }

    protected static void drawBlankArea(Map<String, com.poc.pdf.model.Rectangle> blankRectList, PdfCanvas canvas, SignatureConfig config) {
        for (com.poc.pdf.model.Rectangle blank : blankRectList.values()) {
            RectangleLine rectangleLine = new RectangleLine(blank, 2 * config.getBorderWidth());
            for (Line line : rectangleLine.getLineList()) {
                canvas.setLineWidth(line.getWidth()).setStrokeColor(config.getMarkBorderColor()).setLineDash(10, 10);
                drawLine(canvas, line, config);
                canvas.stroke();
            }
        }
    }

    public static List<com.poc.pdf.model.Rectangle> insertSignature(Map<String, com.poc.pdf.model.Rectangle> blankRectList, PdfCanvas canvas, SignatureConfig config, List<FileInfo> signatureList) throws MalformedURLException {
        Map<String, com.poc.pdf.model.Rectangle> blankRectNeedTextMap = new HashMap<>();
        if (config.getSignatureMax() < 1) {
            return new ArrayList(blankRectNeedTextMap.values());
        }
        List<RectPoint> pointRectList = new ArrayList<>();
        for (com.poc.pdf.model.Rectangle temp : blankRectList.values()) {
            List<Point> pList = randomStartPoint(temp, config);
            if (CollectionUtils.isNotEmpty(pList)) {
                pointRectList.addAll(createPoint(pList, temp));
            } else {
                blankRectNeedTextMap.put(temp.getName(), temp);
            }
        }
        int max = config.getSignatureMax();
        if (max > pointRectList.size()) {
            max = pointRectList.size();
        }
        int rd = TableUtil.randomRange(max, config.getSignatureMin());
        Collections.shuffle(pointRectList);

        List<Point> pointList = new ArrayList<>();
        List<String> markedSignRect = new ArrayList<>();
        for (int i = 0; i < pointRectList.size(); i++) {
            RectPoint rp = pointRectList.get(i);
            if (i < rd) {
                pointList.add(rp.point);
                markedSignRect.add(rp.rect.getName());
            } else {
                if (!markedSignRect.contains(rp.rect.getName())) {
                    blankRectNeedTextMap.put(rp.rect.getName(), rp.rect);
                }
            }
        }

        drawSelectedImage(pointList, signatureList, canvas, config);

        return new ArrayList(blankRectNeedTextMap.values());
    }

    public static List<RectPoint> createPoint(List<Point> pList, com.poc.pdf.model.Rectangle rect) {
        List<RectPoint> pointRectList = new ArrayList<>(pList.size());
        for (Point point : pList) {
            pointRectList.add(new RectPoint(rect, point));
        }
        return pointRectList;
    }

    static class RectPoint {
        com.poc.pdf.model.Rectangle rect;
        Point point;

        public RectPoint(com.poc.pdf.model.Rectangle rect, Point point) {
            this.rect = rect;
            this.point = point;
        }

        public com.poc.pdf.model.Rectangle getRect() {
            return rect;
        }

        public void setRect(com.poc.pdf.model.Rectangle rect) {
            this.rect = rect;
        }

        public Point getPoint() {
            return point;
        }

        public void setPoint(Point point) {
            this.point = point;
        }
    }

}