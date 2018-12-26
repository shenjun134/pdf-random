package com.poc.pdf.simulate;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.poc.pdf.model.FileInfo;
import com.poc.pdf.model.Line;
import com.poc.pdf.model.Point;
import com.poc.pdf.util.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.text.*;
import java.util.*;

public class LazardTranSimu extends SimulatorBase {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(LazardTranSimu.class);

    interface Constant {
        String output = "simulate/lazard-tran/";

        float line1Width = 1.2f;
        float line2Width = 1f;
        Color lineColor = new DeviceRgb(0, 0, 0);
        Color markRectColor = new DeviceRgb(255, 0, 0);
        float markRectBorderWidth = 0.5f;
        //66, 73, 73
        //112, 123, 124
        //97, 106, 107
        Color fontColor = new DeviceRgb(0, 0, 0);
        //w: 595.0F, h: 842.0F
        Rectangle rectangle = PageSize.A4;
        float iconWidth = 115;
        float iconHeight = 37;

        int noiseCountMax = 5;
        int noiseBorderMax = 2;
    }

    public static void main(String[] args) {
        process();
    }

    interface OffsizeRange {
        int topMax = 20;
        int topMin = 0;
        int left = 10;
    }

    static class Config extends BaseConfig {
        String icon = "simulate/lazard-tran/config/LAZARD.png";
        Point iconStart = new Point(60, 93);

        Point line1Start = new Point(21, 319);
        Point line1End = new Point(554, 319);
        Line line1 = new Line(line1Start, line1End);

        Point line2Start = new Point(21, 351);
        Point line2End = new Point(554, 351);
        Line line2 = new Line(line2Start, line2End);

        Point topLeft = new Point(97, 133);
        Point topMiddle = new Point(207, 237);

        Point footerBegin = new Point(386, 705);

        Date statementDate;
        String accountNumber = rdAccNumber();
        String accountName = rdAccName();
        String investFund = rdInvestFund();
        String totalUnit;
        List<List<String>> tableCt;
    }

    private static Config createConfig() throws ParseException {
        int topOffsize = RandomUtil.randomInt(OffsizeRange.topMax, OffsizeRange.topMin) * (-1);
        int leftOffsize = RandomUtil.randomInt(OffsizeRange.left, 0) * (RandomUtil.randomBool() ? -1 : 1);
        System.out.println("Shack Top:" + topOffsize + ", Left:" + leftOffsize);
        Config config = new Config();

        /***************set mark info*************/
        config.markColor = Constant.markRectColor;
        config.markBorderWidth = Constant.markRectBorderWidth;
        config.statementDate = randomDate();

        setOffsize(config.iconStart, topOffsize, leftOffsize);
        setOffsize(config.line1Start, topOffsize, leftOffsize);
        setOffsize(config.line1End, topOffsize, leftOffsize);
        setOffsize(config.line2Start, topOffsize, leftOffsize);
        setOffsize(config.line2End, topOffsize, leftOffsize);
        setOffsize(config.topLeft, topOffsize, leftOffsize);
        setOffsize(config.topMiddle, topOffsize, leftOffsize);
        setOffsize(config.footerBegin, topOffsize, leftOffsize);
        mockTable(config);
        return config;
    }

    public static void process() {
        Long startAt = System.currentTimeMillis();
        try {
            Properties properties = load();
            List<FileInfo> signatureList = FileUtil.loadDirImages(properties.getProperty("signature.dir"));
            int total = NumberUtils.toInt(properties.getProperty("lazard.tran.total"), 0);
            String prefix = properties.getProperty("lazard.tran.prefix");
            for (int i = 0; i < total; i++) {
                String index = StringUtils.leftPad("" + i, 5, "0");
                Config config = createConfig();
                config.properties = properties;
                config.dest = prefix + "-" + index + ".pdf";
                config.layoutXmlFile = prefix + "-layout-" + index + ".xml";
                config.tableXmlFile = prefix + "-table-" + index + ".xml";
                config.tableOutlineXmlFile = prefix + "-table-outline-" + index + ".xml";
                config.signXmlFile = prefix + "-signature-" + index + ".xml";
                config.logoXmlFile = prefix + "-logo-" + index + ".xml";
                config.textFile = prefix + "-text-" + index + ".txt";
                config.signatureList = signatureList;
                process(config);
            }
        } catch (Exception e) {
            logger.error("LazardTranSimu process error", e);
        } finally {
            System.out.println("Total Used:" + (System.currentTimeMillis() - startAt));
        }
    }

    public static void process(Config config) throws Exception {
        PdfWriter writer = new PdfWriter(Constant.output + config.dest);
        PdfDocument pdf = new PdfDocument(writer);
        PageSize ps = new PageSize(Constant.rectangle);
        PdfPage page = pdf.addNewPage(ps);

        PdfCanvas canvas = new PdfCanvas(page);

        drawBaseLine(canvas, config);
        drawLogo(canvas, config);
        drawTopLeft(canvas, config);
        drawMiddleInfo(canvas, config);

        drawTable(canvas, config);

        drawSignature(canvas, config);

        drawFooterInfo(canvas, config);

        insertNoise(canvas, config);

        String markEnableStr = config.properties.getProperty("mark.enable");
        if (Boolean.valueOf(markEnableStr)) {
            drawRectange(canvas, config);
        }

        pdf.close();

        String fullPath = Constant.output + config.dest;
        String originalPdf = fullPath;
        PDFUtil.splitPdf2Jpg(originalPdf, Constant.output, 72);

        String signXml = generateXml(config, "SIGNATURE:", config.signXmlFile);
        String tableXml = generateXml(config, "TABLE:", config.tableXmlFile);
        String tableOutlineXml = generateXml(config, "TABLE-OUTLINE:", config.tableOutlineXmlFile);
        String layoutXml = generateXml(config, "LAYOUT:", config.layoutXmlFile);
        String logoXml = generateXml(config, "LOGO:", config.logoXmlFile);
        FileUtil.write(signXml, Constant.output + config.signXmlFile);
        FileUtil.write(tableXml, Constant.output + config.tableXmlFile);
        FileUtil.write(tableOutlineXml, Constant.output + config.tableOutlineXmlFile);
        FileUtil.write(layoutXml, Constant.output + config.layoutXmlFile);
        FileUtil.write(logoXml, Constant.output + config.logoXmlFile);
    }

    private static void drawBaseLine(PdfCanvas canvas, Config config) {
        canvas.setLineWidth(Constant.line1Width).setStrokeColor(Constant.lineColor);
        drawLine(canvas, config.line1, Constant.rectangle.getHeight());
        canvas.stroke();
        canvas.setLineWidth(Constant.line2Width).setStrokeColor(Constant.lineColor);
        drawLine(canvas, config.line2, Constant.rectangle.getHeight());
        canvas.stroke();
    }

    private static void drawLogo(PdfCanvas canvas, Config config) throws MalformedURLException {
        File file = new File(config.icon);
        FileInfo iconF = new FileInfo(file);
        insertImage(iconF, config.iconStart, canvas, Constant.rectangle.getHeight(), Constant.iconWidth);

        Point start = new Point((int) (config.iconStart.getX() - 1f), (int) (config.iconStart.getY() - Constant.iconHeight + 2));
        Point end = new Point((int) (config.iconStart.getX() + Constant.iconWidth + 1), (int) (config.iconStart.getY() + 2f));

        addRectangle4Logo(start, end, "Logo Area", config);
    }

    private static void drawTopLeft(PdfCanvas canvas, Config config) throws IOException {
        PdfFont baseFont = FontUtil.createFont(FontConstants.HELVETICA);
        int fontSize = 9;
        float leading = 8f;
        List<String> list = new ArrayList<>();
        list.add("XXXXX XXXXXX Australia Limited");
        list.add("ACF Emergency Services Super Board");
        list.add("ATF Emergency Services Super Scheme");
        list.add("C/- Unlisted Investments Team");
        list.add("Level 14 420 George Street");
        list.add("SYDNEY  NSW 2000");

        float topX = config.topLeft.getX();
        float currentTopY = Constant.rectangle.getHeight() - config.topLeft.getY();
        float maxRowWidth = 0;
        for (int i = 0; i < list.size(); i++) {
            String value = list.get(i);
            canvas.beginText()
                    .setFontAndSize(baseFont, fontSize)
                    .setColor(Constant.fontColor, true)
                    .setLeading(leading)
                    .moveText(topX, currentTopY);
            canvas.newlineShowText(value);
            canvas.endText();
            currentTopY = currentTopY - 11;
            float tempW = baseFont.getWidth(value, fontSize);
            if (tempW > maxRowWidth) {
                maxRowWidth = tempW;
            }
        }
        Point start = new Point((int) (config.topLeft.getX() - 1f), (int) (config.topLeft.getY() - 2f));
        Point end = new Point((int) (config.topLeft.getX() + maxRowWidth + 1f), (int) (Constant.rectangle.getHeight() - currentTopY - 1));
        addRectangle4Layout(start, end, "Top Left", config);
    }


    private static void drawMiddleInfo(PdfCanvas canvas, Config config) throws IOException {
        String statementInfo = "STATEMENT DATE:" + DateFormatUtils.format(config.statementDate, "MM/dd/yyyy");
        Point statementP = new Point(config.topMiddle.getX() + 5, config.topMiddle.getY() + 24);

        String accountNumInfo = "ACCOUNT NUMBER:  " + config.accountNumber;
        String accountNameInfo = "ACCOUNT NAME:       " + config.accountName;
        Point accNumP = new Point(config.line1Start.getX(), config.line1Start.getY() - 38);
        Point accNameP = new Point(config.line1Start.getX(), config.line1Start.getY() - 20);


        Point dailyP = new Point(config.topMiddle.getX() + 2, config.line1Start.getY() + 10);

        PdfFont baseFont = FontUtil.createFont(FontConstants.HELVETICA);

        printInfo(canvas, config, "TRANSACTION CONFIRMATION", config.topMiddle, 10, baseFont);
        printInfo(canvas, config, statementInfo, statementP, 10, baseFont);
        printInfo(canvas, config, accountNumInfo, accNumP, 9, baseFont);
        printInfo(canvas, config, accountNameInfo, accNameP, 9, baseFont);
        printInfo(canvas, config, "DAILY TRANSACTION SUMMARY", dailyP, 11, baseFont);
    }

    private static void drawFooterInfo(PdfCanvas canvas, Config config) throws IOException {
        List<String> list1 = new ArrayList<>();
        list1.add("Issuer:");
        list1.add("Lazard Asset Management Pacific Co.");
        list1.add("ABN 13 064 523 619 AFSL No. 238432");
        list1.add("Level 39, Gateway");
        list1.add("1 Macquarie Place");
        list1.add("Sydney, NSW 2000");

        List<String> list2 = new ArrayList<>();
        list2.add("Ph: 1800 825 287");
        list2.add("Email: investorqueries@lazard.com");
        list2.add("Website: www.lazardassetmanagement.com.au");

        PdfFont baseFont = FontUtil.createFont(FontConstants.HELVETICA);
        PdfFont baseFontBold = FontUtil.createFont(FontConstants.HELVETICA_BOLD);
        float maxRowWidth = 0;
        int fontSize1 = 6;
        Point beginP = config.footerBegin;
        float currentTop = beginP.getY();
        for (int i = 0; i < list1.size(); i++) {
            Point tempP = new Point(beginP.getX(), (int) currentTop);
            String text = list1.get(i);
            printInfoWithoutRect(canvas, config, text, tempP, fontSize1, baseFont);
            currentTop = currentTop + 8;
            float tempW = baseFont.getWidth(text, fontSize1);
            if (tempW > maxRowWidth) {
                maxRowWidth = tempW;
            }
        }
        for (int i = 0; i < list2.size(); i++) {
            Point tempP = new Point(beginP.getX(), (int) currentTop);
            String text = list2.get(i);
            printInfoWithoutRect(canvas, config, text, tempP, fontSize1, baseFontBold);
            currentTop = currentTop + 8;
            float tempW = baseFontBold.getWidth(text, fontSize1);
            if (tempW > maxRowWidth) {
                maxRowWidth = tempW;
            }
        }
        Point start = new Point((int) (beginP.getX() - 1f), (int) (beginP.getY() + 0f));
        Point end = new Point((int) (beginP.getX() + maxRowWidth + 3f), (int) (currentTop));
        addRectangle4Layout(start, end, "Footer Area", config);
    }

    private static void drawTable(PdfCanvas canvas, Config config) throws IOException {
        float xBegin = config.line2Start.getX();
        float yBegin = config.line2Start.getY() + 27f;
        LinkedHashMap<String, Float> leftMap = new LinkedHashMap<>();
        leftMap.put("TRADE DATE", xBegin);
        leftMap.put("TRANSACTION DESCRIPTION", xBegin + 90f);
        Point tableOutlineStart = new Point((int)(xBegin - 5), (int)(yBegin - 5));
        Point tableOutlineEnd = new Point(config.line2End.getX(), (int)(yBegin - 2));

        LinkedHashMap<String, Float> rightMap = new LinkedHashMap<>();
        rightMap.put("PRICE PER UNIT (A$)", xBegin + 330f);
        rightMap.put("NUMBER OF\nUNITS", xBegin + 430f);
        rightMap.put("AMOUNT (A$)", config.line2End.getX() - 5f);

        PdfFont baseFont = FontUtil.createFont(FontConstants.HELVETICA);
        PdfFont baseFontBold = FontUtil.createFont(FontConstants.HELVETICA_BOLD);

        for (Map.Entry<String, Float> entry : leftMap.entrySet()) {
            String text = entry.getKey();
            float xStart = entry.getValue();
            Point tempP = new Point((int) xStart, (int) yBegin);
            printInfo4Table(canvas, config, text, tempP, 9, baseFont);
        }

        for (Map.Entry<String, Float> entry : rightMap.entrySet()) {
            String text = entry.getKey();
            float xStart = entry.getValue();
            Point tempP = new Point((int) xStart, (int) yBegin);
            printInfo4TableRight(canvas, config, text, tempP, 9, baseFont);
        }
        yBegin = yBegin + 22;
        Point investFundP = new Point(config.line2Start.getX(), (int) (yBegin));
        printInfo(canvas, config, config.investFund, investFundP, 9, baseFontBold);
        yBegin = yBegin + 18;
        for (int i = 0; i < config.tableCt.size(); i++) {
            List<String> row = config.tableCt.get(i);
            for (int j = 0; j < row.size(); j++) {
                String text = row.get(j);
                if (j < leftMap.size()) {
                    Map.Entry<String, Float> entry = (Map.Entry<String, Float>) getEntry(leftMap, j);
                    float xStart = entry.getValue();
                    Point tempP = new Point((int) xStart, (int) yBegin);
                    printInfo4Table(canvas, config, text, tempP, 9, baseFont);
                } else {
                    Map.Entry<String, Float> entry = (Map.Entry<String, Float>) getEntry(rightMap, j - leftMap.size());
                    float xStart = entry.getValue();
                    Point tempP = new Point((int) xStart, (int) yBegin);
                    printInfo4TableRight(canvas, config, text, tempP, 9, baseFont);
                }
            }
            yBegin = yBegin + 13;
        }
        yBegin = yBegin + 8;

        String totalUnitsOwned = "TOTAL UNITS OWNED";
        Point tuoPoint = new Point((int) (xBegin + 92f), (int) yBegin);
        printInfo4Table(canvas, config, totalUnitsOwned, tuoPoint, 9, baseFontBold);

        Point tuPoint = new Point((int) (xBegin + 430f), (int) yBegin);
        printInfo4TableRight(canvas, config, config.totalUnit, tuPoint, 9, baseFontBold);

        yBegin = yBegin + 13;

        String asAt = "AS AT " + DateFormatUtils.format(config.statementDate, "MM/dd/yyyy");
        Point aaPoint = new Point((int) (xBegin + 82f), (int) yBegin);
        printInfo4Table(canvas, config, asAt, aaPoint, 9, baseFontBold);
        tableOutlineEnd.setY((int)(yBegin + 10));
        addRectangle4TableOutline(tableOutlineStart, tableOutlineEnd, "Table-Outline", config);
    }

    private static void drawSignature(PdfCanvas canvas, Config config) throws MalformedURLException {
        boolean isTop = RandomUtil.randomBool();
        float footerEnd = config.footerBegin.getY() + 80;
        if (!isTop) {
            footerEnd = Constant.rectangle.getHeight() - footerEnd;
        }
        FileInfo fileInfo = randomFile(config.signatureList);
        float width = 72f;
        int left = RandomUtil.randomInt(200, 0);
//        int top = RandomUtil.randomInt(OffsizeRange.top, 0) * (RandomUtil.randomBool() ? -1 : 1);
        float height = Constant.rectangle.getHeight() - footerEnd;
        Point signPoint = new Point((int) (Constant.rectangle.getWidth() / 2 - left * (isTop ? -1 : 1)), (int) (height + 0 + 32));
        insertImage(fileInfo, signPoint, canvas, Constant.rectangle.getHeight(), width);
        //TODO
        Point start = new Point((int) (signPoint.getX() - 1f), (int) (signPoint.getY() - width));
        Point end = new Point((int) (signPoint.getX() + width + 1), (int) (signPoint.getY() + 0f));
        addRectangle4Sign(start, end, "Signature Area", config);
    }


    private static void printInfo(PdfCanvas canvas, Config config, String text, Point startP, int fontSize, PdfFont baseFont) throws IOException {
        float leading = 6f;
        float topX = startP.getX();
        float currentTopY = Constant.rectangle.getHeight() - startP.getY();
        float maxRowWidth = baseFont.getWidth(text, fontSize);
        canvas.beginText()
                .setFontAndSize(baseFont, fontSize)
                .setColor(Constant.fontColor, true)
                .setLeading(leading)
                .moveText(topX, currentTopY);
        canvas.newlineShowText(text);
        canvas.endText();
        Point start = new Point((int) (startP.getX() - 1f), (int) (startP.getY() - 3f));
        Point end = new Point((int) (startP.getX() + maxRowWidth + 1f), (int) (startP.getY() + fontSize - 1f));
        addRectangle4Layout(start, end, text, config);
    }

    private static void printInfo4Table(PdfCanvas canvas, Config config, String text, Point startP, int fontSize, PdfFont baseFont) throws IOException {
        float leading = 6f;
        float topX = startP.getX();
        float currentTopY = Constant.rectangle.getHeight() - startP.getY();
        float maxRowWidth = baseFont.getWidth(text, fontSize);
        canvas.beginText()
                .setFontAndSize(baseFont, fontSize)
                .setColor(Constant.fontColor, true)
                .setLeading(leading)
                .moveText(topX, currentTopY);
        canvas.newlineShowText(text);
        canvas.endText();
        Point start = new Point((int) (startP.getX() - 1f), (int) (startP.getY() - 3f));
        Point end = new Point((int) (startP.getX() + maxRowWidth + 1f), (int) (startP.getY() + fontSize - 1f));
        addRectangle4TableCell(start, end, text, config);
    }

    private static void printInfo4TableRight(PdfCanvas canvas, Config config, String text, Point startP, int fontSize, PdfFont baseFont) throws IOException {
        float leading = 6f;
        float topX = startP.getX();
        float currentTopY = Constant.rectangle.getHeight() - startP.getY();
        float maxRowWidth = 0;
        String[] arr = text.split("\n");
        for (String value : arr) {
            float tempW = baseFont.getWidth(value, fontSize);
            if (tempW > maxRowWidth) {
                maxRowWidth = tempW;
            }
            canvas.beginText()
                    .setFontAndSize(baseFont, fontSize)
                    .setColor(Constant.fontColor, true)
                    .setLeading(leading)
                    .moveText(topX - tempW, currentTopY);
            canvas.newlineShowText(value);
            canvas.endText();
            currentTopY = currentTopY - fontSize - 1;
        }
        Point start = new Point((int) (startP.getX() - maxRowWidth - 1f), (int) (startP.getY() - 3f));
        Point end = new Point((int) (startP.getX() + 1f), (int) (startP.getY() + arr.length * fontSize - 1f));
        addRectangle4TableCell(start, end, text, config);
    }

    private static void printInfoWithoutRect(PdfCanvas canvas, Config config, String text, Point startP, int fontSize, PdfFont baseFont) throws IOException {
        float leading = 6f;
        float topX = startP.getX();
        float currentTopY = Constant.rectangle.getHeight() - startP.getY();
        canvas.beginText()
                .setFontAndSize(baseFont, fontSize)
                .setColor(Constant.fontColor, true)
                .setLeading(leading)
                .moveText(topX, currentTopY);
        canvas.newlineShowText(text);
        canvas.endText();
    }

    private static void insertNoise(PdfCanvas canvas, Config config) {
        String noiseEnable = config.properties.getProperty("noise.enable");
        if (!Boolean.valueOf(noiseEnable)) {
            return;
        }
        List<Line> lineList = TableUtil.randomLine(Constant.noiseCountMax, (int) Constant.rectangle.getWidth(), (int) Constant.rectangle.getHeight(), Constant.noiseBorderMax);
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
            drawLine(canvas, line, Constant.rectangle.getHeight());
            canvas.stroke();
        }
        canvas.setStrokeColor(Color.BLACK);
    }

    private static void mockTable(Config config) throws ParseException {
        List<String> descList = tranDesc();
        List<List<String>> tableCt = new ArrayList<>();
        int row = RandomUtil.randomInt(descList.size(), 2);
        BigDecimal totalUnit = BigDecimal.ZERO;
        String date = DateFormatUtils.format(config.statementDate, "MM-dd-yy");
        for (int i = 0; i < row; i++) {
            int intLen = RandomUtil.randomInt(8, 5);
            String perUnit = randomNumber(1, 4, false);
            String units = randomNumber(intLen, 3, false);
            String amount = randomNumber(intLen, 2, false);

            List<String> list = new ArrayList<>();
            list.add(date);
            list.add(descList.get(i));
            list.add(perUnit);
            list.add(units);
            list.add(amount);
            tableCt.add(list);
            Double unitsD = numFmt.parse(units).doubleValue();
            totalUnit = totalUnit.add(BigDecimal.valueOf(unitsD));
        }
        config.totalUnit = numFmt.format(totalUnit.doubleValue());
        config.tableCt = tableCt;
    }

    private static List<String> tranDesc() {
        List<String> list = new ArrayList<>();
        list.add("OPENING BALANCE");
        list.add("Fee Rebate (Reinvest)");
        list.add("CLOSING BALANCE");
        list.add("TOTAL EQUITY");
        list.add("EXCESS CASH EQUITY");
        list.add("OPEN TRADE EQUITY");
        list.add("EXCHANGE FEES");
        list.add("GROSS PROFIT OR LOSS");
        list.add("CASH AND ADJUSTMENTS");
        Collections.shuffle(list);
        return list;
    }

    private static Map.Entry<?, ?> getEntry(LinkedHashMap<?, ?> linkedHashMap, int index) {
        int i = 0;
        for (Map.Entry<?, ?> entry : linkedHashMap.entrySet()) {
            if (i == index) {
                return entry;
            }
            i++;
        }
        return null;
    }

    private static String rdAccNumber() {
        return RandomUtil.randomStrOnly(9, 9).toUpperCase();
    }

    private static String rdAccName() {
        String template = "{0} {1} Australia Limited {2} {3} {4} {5} {6}";
        int count = 7;
        String[] param = new String[7];
        for (int i = 0; i < count; i++) {
            param[i] = RandomUtil.randomStrOnly(8, 6);
            param[i] = RandomUtil.uppcaseFirst(param[i]);
        }
        return MessageFormat.format(template, param);
    }

    private static String rdInvestFund() {
        String template = "{0} {1} {2} {3} Fund";
        int count = 7;
        String[] param = new String[7];
        for (int i = 0; i < count; i++) {
            param[i] = RandomUtil.randomStrOnly(8, 6);
            param[i] = RandomUtil.uppcaseFirst(param[i]);
        }
        return MessageFormat.format(template, param);
    }

}
