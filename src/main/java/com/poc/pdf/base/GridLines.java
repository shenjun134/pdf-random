package com.poc.pdf.base;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.poc.pdf.model.GridLayoutConfig;
import com.poc.pdf.model.GridLayoutResult;
import com.poc.pdf.model.Line;
import com.poc.pdf.model.Point;
import com.poc.pdf.util.GridLayoutUtil;
import com.poc.pdf.util.PDFUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class GridLines {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(GridLines.class);


    public static final String DEST = "./grid_lines-";

    public static final String TYPE = ".pdf";


    interface Const {
        int baseFont = 16;
        int unitCharLength = 10;
        int unitCharHeight = 18;
        int offsiteX = 10;
        int offsiteY = 8;
        String layoutName = "./layout/";
    }

    public static void main(String args[]) throws IOException {
        File file = new File(Const.layoutName);
        file.getParentFile().mkdirs();
        GridLayoutConfig config = new GridLayoutConfig();
        List<List<GridLayoutResult>> listInList = new ArrayList<>();

        for (int index = 0; index < config.getNumberOfCategory(); index++) {
            List<GridLayoutResult> resultList = getResult(config);
            listInList.add(resultList);
            String path = Const.layoutName + "type-" + index + "/";
            File tempFile = new File(path);
            tempFile.mkdirs();

            for (int i = 0; i < resultList.size(); i++) {
                GridLayoutResult temp = resultList.get(i);
                String fileName = "type-" + index + "-" + i;
                String fullPath = path + fileName + TYPE;
                new GridLines().createPdf(fullPath, fileName, config, temp);
            }
        }

        for (int index = 0; index < config.getNumberOfCategory(); index++) {
            String path = Const.layoutName + "type-" + index + "/";
            List<GridLayoutResult> resultList = listInList.get(index);
            for (int i = 0; i < resultList.size(); i++) {
                String fileName = "type-" + index + "-" + i;
                String fullPath = path + fileName + TYPE;

                String originalPdf = fullPath;
                PDFUtil.splitPdf2Jpg(originalPdf, path, 72);
            }
        }

    }

    public static List<GridLayoutResult> getResult(GridLayoutConfig config) {
        List<GridLayoutResult> resultList = new ArrayList<>();
        config.getSplitConfigList().clear();
        GridLayoutResult result = GridLayoutUtil.randomGrid(config);
//        result.printRect();
        for (int i = 0; i < config.getEachCategoryTotal(); i++) {
            resultList.add(result);
//            GridLayoutResult temp = GridLayoutUtil.splitLoop(config);
//            resultList.add(temp);
//            temp.printRect();
        }
        return resultList;
    }

    public void createPdf(String dest, String fileName, GridLayoutConfig config, GridLayoutResult result) throws IOException {
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
            for (String text : textList) {
                canvas.newlineText();
                canvas.showText(text);
//                canvas.newlineShowText(text);
                logger.info("text:" + text);
            }
            canvas.endText();

            rectangle.setText(textList);
        }

        //make noise

        makeNoiseTop(config, padding, canvas, fileName);
        makeNoiseBottom(config, padding, canvas);
        makeNoiseLeft(config, padding, canvas);
        makeNoiseRight(config, padding, canvas);


        //Close document
        pdf.close();
    }

    private static void drawLine(PdfCanvas canvas, Line line, GridLayoutConfig config) {
        Point p1 = line.getReal1(config);
        Point p2 = line.getReal2(config);

        canvas.moveTo(p1.getX(), p1.getY()).lineTo(p2.getX(), p2.getY());
    }

    private static List<String> randomString(com.poc.pdf.model.Rectangle rectangle, int fontSize) {
        int baseFont = Const.baseFont;
        int unitCharLength = Const.unitCharHeight;
        int offsite = Const.offsiteY;

        int height = rectangle.height();
        int width = rectangle.width();
        int row = height / (unitCharLength * fontSize / baseFont) - offsite;
        if (row < 1) {
            row = 1;
        }

        List<String> list = new ArrayList<>(row);
//        list.add(rectangle.getName());
        for (int i = 0; i < row; i++) {
            list.add(randomText(rectangle.width(), fontSize));
        }
//        list.add("" + width + " X " + height);
        return list;
    }

    private static String randomText(int width, int fontSize) {
        int baseFont = Const.baseFont;
        int unitCharLength = Const.unitCharLength;
        int rd = (int) Math.random() * 10000 % 5;
        int offsite = Const.offsiteX + rd;
        int count = width / (unitCharLength * fontSize / baseFont) - offsite;
        if (count < 2) {
            count = 2;
        }
//        String range = "abcdefghijklmnopqrstuvwxyz0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ~!@#$%^&*()_+=-{}[];:'\"|\\/><,.`";
//        String range = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
//
//        return RandomStringUtils.random(count, 0, range.length(), true, true, range.toCharArray());
//        return RandomStringUtils.random(count, chars);
//        return RandomStringUtils.randomAscii(count);

        char[] possibleCharacters = (new String("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?")).toCharArray();
        String randomStr = RandomStringUtils.random(count, 0, possibleCharacters.length - 1, false, false, possibleCharacters, new SecureRandom());
        return randomStr;
    }


    private static void makeNoiseBottom(GridLayoutConfig config, int padding, PdfCanvas canvas) throws IOException {
        int fontSize = 26;
        float leadingSize = 1.2f * fontSize;
        int gridWidth = config.getTotalWidth() - config.getPaddingLeft() - config.getPaddingRight();
        String bottomText = randomText(gridWidth, fontSize);
        String fontFamily = fontProgram();
        int bottomX = config.getPaddingLeft() - padding;
        int bottomY = 2 * fontSize;
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(fontFamily), fontSize)
                .setLeading(leadingSize)
                .moveText(bottomX, bottomY);
        canvas.newlineShowText(bottomText);
        canvas.endText();
    }

    private static void makeNoiseTop(GridLayoutConfig config, int padding, PdfCanvas canvas, String fileName) throws IOException {
        int gridWidth = config.getTotalWidth() - config.getPaddingLeft() - config.getPaddingRight();
        int fontSizeTop = 60;
        float leadingSizeTop = 1.2f * fontSizeTop;
        String header = randomText(gridWidth / 4, fontSizeTop).toUpperCase() + " ---- " + new Date();
        String header2 = fileName;
        String header3 = randomText(gridWidth / 1, fontSizeTop).toUpperCase();
        String fontFamilyTop = FontConstants.HELVETICA_BOLD;
        int topX = config.getPaddingLeft() + 2 * padding;
        int topY = config.getTotalHeight() - padding;
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(fontFamilyTop), fontSizeTop)
                .setLeading(leadingSizeTop)
                .moveText(topX, topY);
        canvas.newlineShowText(header);
        canvas.newlineShowText(header2);
        canvas.newlineShowText(header3);
        canvas.endText();
    }

    private static void makeNoiseLeft(GridLayoutConfig config, int padding, PdfCanvas canvas) throws IOException {
        int fontSize = 26;
        float leadingSize = 1.2f * fontSize;
        int gridWidth = config.getTotalHeight() - config.getPaddingTop() - config.getPaddingBottom();
        String leftText = randomText((int) (gridWidth / 1.5f), fontSize);
        String fontFamily = fontProgram();
        int leftX = 1 * fontSize;
        int leftY = config.getTotalHeight() - 2 * padding;
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(fontFamily), fontSize)
                .setTextRenderingMode(1)
                .setLeading(leadingSize)
                .moveText(leftX, leftY);
        int size = leftText.length();
        for (int i = 0; i < size; i++) {
            canvas.newlineShowText("" + leftText.charAt(i));

        }
        canvas.endText();
    }

    private static void makeNoiseRight(GridLayoutConfig config, int padding, PdfCanvas canvas) throws IOException {
        int fontSize = 26;
        float leadingSize = 1.2f * fontSize;
        int gridWidth = config.getTotalHeight() - config.getPaddingTop() - config.getPaddingBottom();
        String leftText = randomText((int) (gridWidth / 1.5f), fontSize);
        String fontFamily = fontProgram();
        int leftX = config.getTotalWidth() - 2 * fontSize;
        int leftY = config.getTotalHeight() - 2 * padding;
        canvas.beginText()
                .setFontAndSize(PdfFontFactory.createFont(fontFamily), fontSize)
                .setTextRenderingMode(1)
                .setLeading(leadingSize)
                .moveText(leftX, leftY);
        int size = leftText.length();
        for (int i = 0; i < size; i++) {
            canvas.newlineShowText("" + leftText.charAt(i));

        }
        canvas.endText();
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
//                FontConstants.ZAPFDINGBATS,
                FontConstants.TIMES

        };
        int index = (int) (rd % fontArr.length - 1);
        return fontArr[index];
    }

}