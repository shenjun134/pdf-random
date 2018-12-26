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
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.*;
import java.util.*;

public class JanusHendersonSimu extends SimulatorBase {

    private static final Logger logger = Logger.getLogger(JanusHendersonSimu.class);


    interface Constant {
        String output = "simulate/janus-henderson/";

        float lineWidth = 3.7f;
        Color lineColor = new DeviceRgb(146, 43, 33);
        Color markRectColor = new DeviceRgb(255, 0, 0);
        float markRectBorderWidth = 0.5f;
        //66, 73, 73
        //112, 123, 124
        //97, 106, 107
        Color fontColor = new DeviceRgb(66, 73, 73);
        //w: 595.0F, h: 842.0F
        Rectangle rectangle = PageSize.A4;
        float iconWidth = 183;
        float iconHeight = 37;

        float accountNameLeftRowMax = 239f;
        float accountNameRightRowMax = 186f;

        DateFormat fmtFull = new SimpleDateFormat("dd MMMM yyyy", Locale.US);
        DateFormat fmtshort = new SimpleDateFormat("dd/MM/yyyy", Locale.US);
        int detailMaxRow = 5;
        int noiseCountMax = 5;
        int noiseBorderMax = 2;
    }

    interface OffsizeRange {
        int topMax = 20;
        int topMin = 0;
        int left = 20;
    }

    static class Config extends BaseConfig {
        List<List<String>> table4Summary;
        List<List<String>> table4Detail;
        Point start1 = new Point(360, 222);
        Point end1 = new Point(546, 222);
        Point start2 = new Point(50, 396);
        Point end2 = new Point(338, 396);

        String icon = "simulate/janus-henderson/config/Janus-Henderson.png";
        Point iconStart = new Point(359, 87);
        Point accountNameStart = new Point(99, 172);
        Point headerStart = new Point(50, 417);
        Point top1Start = new Point(359, 109);
        Point top2Start = new Point(359, 236);

        Line lineRight = new Line(start1, end1);
        Line lineLeft = new Line(start2, end2);
        String accountName;
        String accountNumber;
        String fundStr;

        Date tradeDate;
        Date payDate;
        String unitPrice;
        String unitBalance;
        String netAmount;
        String unit;
    }


    private static Config createConfig() {
//        int topOffsize = RandomUtil.randomInt(OffsizeRange.top, 0) * (RandomUtil.randomBool() ? -1 : 1);
        int topOffsize = RandomUtil.randomInt(OffsizeRange.topMax, OffsizeRange.topMin) * (-1);
        int leftOffsize = RandomUtil.randomInt(OffsizeRange.left, 0) * (RandomUtil.randomBool() ? -1 : 1);
        System.out.println("Shack Top:" + topOffsize + ", Left:" + leftOffsize);
        Config config = new Config();
        setOffsize(config.start1, topOffsize, leftOffsize);
        setOffsize(config.end1, topOffsize, leftOffsize);
        setOffsize(config.start2, topOffsize, leftOffsize);
        setOffsize(config.end2, topOffsize, leftOffsize);

        setOffsize(config.iconStart, topOffsize, leftOffsize);
        setOffsize(config.accountNameStart, topOffsize, leftOffsize);
        setOffsize(config.headerStart, topOffsize, leftOffsize);
        setOffsize(config.top1Start, topOffsize, leftOffsize);
        setOffsize(config.top2Start, topOffsize, leftOffsize);

        config.tradeDate = randomDate();
        config.payDate = randomDate();

        /**
         * Account Name
         */
        config.accountName = randomFundNumber();
        config.accountNumber = RandomStringUtils.random(9, false, true);
        /**
         * Invest Fund
         */
        config.fundStr = randomFundStr();
        config.unit = randomNumber(5, 4, false);
        getTableDetail(config);
        getTableSummary(config);

        /***************set mark info*************/
        config.markColor = Constant.markRectColor;
        config.markBorderWidth = Constant.markRectBorderWidth;
//        config.markRectPaddingLeft = 5;
//        config.markRectPaddingRight = 5;
//        config.markRectPaddingTop = 5;
//        config.markRectPaddingBottom = 5;


        return config;
    }






    public static void main(String[] args) throws Exception {
        process();
    }

    public static void process(){
        Long startAt = System.currentTimeMillis();
        try {
            Properties properties = load();
            List<FileInfo> signatureList = FileUtil.loadDirImages(properties.getProperty("signature.dir"));
            int total = NumberUtils.toInt(properties.getProperty("janus.henderson.total"), 0);
            String prefix = properties.getProperty("janus.henderson.prefix");
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
//            testFundNumber();
        } catch (Exception e) {
            logger.error("JanusHendersonSimu process error", e);
        } finally {
            System.out.println("Total Used:" + (System.currentTimeMillis() - startAt));
        }
    }

    public static void testFundNumber() {
        for (int i = 0; i < 100; i++) {
            System.out.println(randomFundNumber());
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
        insertLeftTop(canvas, config);
        float tableBegin = insertLeftHeader(canvas, config);
        float tableEnd = drawTable(canvas, tableBegin, config);
        float footerEnd = insertLeftFooter(canvas, Constant.rectangle.getHeight() - tableEnd, config);
        insertNoise(canvas, config);
        drawSignature(canvas, config, footerEnd);

        insertRightTop1(canvas, config);
        insertRightTop2(canvas, config);

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


    private static String randomFundNumber() {
        //xxxxx xxxxxx Australia Ltd (Acf lbfs Re Ams Balanaced Fund)
        String pattern = "{0} {1} {2} Ltd ({3} {4} {5} {6} {7} Fund)";
        String corpaction1 = RandomUtil.randomStrOnly(5, 3);
        corpaction1 = RandomUtil.uppcaseFirst(corpaction1);
        String corpaction2 = RandomUtil.randomStrOnly(6, 4);
        corpaction2 = RandomUtil.uppcaseFirst(corpaction2);

        String country = RandomUtil.randomStrOnly(8, 5);
        country = RandomUtil.uppcaseFirst(country);

        String fundStr1 = RandomUtil.randomStrOnly(8, 5);
        fundStr1 = RandomUtil.uppcaseFirst(fundStr1);

        String fundStr2 = RandomUtil.randomStrOnly(7, 3);
        fundStr2 = RandomUtil.uppcaseFirst(fundStr2);

        String fundStr3 = RandomUtil.randomStrOnly(8, 4);
        fundStr3 = RandomUtil.uppcaseFirst(fundStr3);

        String fundStr4 = RandomUtil.randomStrOnly(8, 4);
        fundStr4 = RandomUtil.uppcaseFirst(fundStr4);

        String fundStr5 = RandomUtil.randomStrOnly(8, 4);
        fundStr5 = RandomUtil.uppcaseFirst(fundStr5);

        String fundStr6 = RandomUtil.randomStrOnly(8, 8);
        fundStr6 = RandomUtil.uppcaseFirst(fundStr6);

        String fundNumber = MessageFormat.format(pattern, corpaction1
                , corpaction2, country, fundStr1, fundStr2, fundStr3, fundStr4, fundStr5, fundStr6);
        System.out.println("xxxxxxxxxxxxxxxxxxxxxxxxx" + fundNumber);

        return fundNumber;
    }

    private static String randomFundStr() {
        //Janus Henderson Tactical Income Fund'
        String fund = "Janus Henderson";
        String param1 = RandomStringUtils.random(6, true, false);
        param1 = RandomUtil.uppcaseFirst(param1);
        String param2 = RandomStringUtils.random(5, true, false);
        param2 = RandomUtil.uppcaseFirst(param2);
        return StringUtils.join(new String[]{fund, param1, param2, "Fund"}, " ");
    }

    private static void drawBaseLine(PdfCanvas canvas, Config config) {
        canvas.setLineWidth(Constant.lineWidth).setStrokeColor(Constant.lineColor);

        drawLine(canvas, config.lineRight, Constant.rectangle.getHeight());
        drawLine(canvas, config.lineLeft, Constant.rectangle.getHeight());
        canvas.stroke();
    }

    private static void drawLogo(PdfCanvas canvas, Config config) throws MalformedURLException {
        File file = new File(config.icon);
        FileInfo iconF = new FileInfo(file);
        insertImage(iconF, config.iconStart, canvas, Constant.rectangle.getHeight(), Constant.iconWidth);

        Point start = new Point((int) (config.iconStart.getX() - 1f), (int) (config.iconStart.getY() - Constant.iconHeight));
        Point end = new Point((int) (config.iconStart.getX() + Constant.iconWidth), (int) (config.iconStart.getY() - 0f));

        addRectangle4Logo(start, end, "Logo Area", config);
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

    private static void drawSignature(PdfCanvas canvas, Config config, float footerEnd) throws MalformedURLException {
        FileInfo fileInfo = randomFile(config.signatureList);
//        float width = NumberUtils.toInt(config.properties.getProperty("signature.react.max.width"), 256);
        float width = 72f;
        int left = RandomUtil.randomInt(50, 0) * (RandomUtil.randomBool() ? -1 : 1);
//        int top = RandomUtil.randomInt(OffsizeRange.top, 0) * (RandomUtil.randomBool() ? -1 : 1);
        float height = Constant.rectangle.getHeight() - footerEnd;
        Point signPoint = new Point((int) (Constant.rectangle.getWidth() / 2 + left), (int) (height + 0 + 32));
        insertImage(fileInfo, signPoint, canvas, Constant.rectangle.getHeight(), width);
        //TODO
        Point start = new Point((int) (signPoint.getX() - 1f), (int) (signPoint.getY()  - width ));
        Point end = new Point((int) (signPoint.getX() + width + 1), (int) (signPoint.getY() + 0f));
        addRectangle4Sign(start, end, "Signature Area", config);
    }

    private static void insertLeftTop(PdfCanvas canvas, Config config) throws IOException {
        String fontFamily = FontConstants.HELVETICA;
        PdfFont baseFont = FontUtil.createFont(fontFamily);
        //TODO
        int fontSize = 10;
        float leading = 6f;
        int topX = config.accountNameStart.getX();
        float topY = Constant.rectangle.getHeight() - config.accountNameStart.getY();

        String fundNumber = config.accountName;
        float totalWidth = baseFont.getWidth(fundNumber, fontSize);
        List<String> list = new ArrayList<>();
        if (totalWidth > Constant.accountNameLeftRowMax) {
            int len = fundNumber.length();
            for (int i = 0; i < len; i++) {
                String temp = StringUtils.substring(fundNumber, 0, len - i);
                if (baseFont.getWidth(temp, fontSize) < Constant.accountNameLeftRowMax) {
                    String temp2 = StringUtils.substring(fundNumber, len - i, len);
                    list.add(temp);
                    list.add(temp2);
                    break;
                }
            }
        } else {
            list.add(fundNumber);
        }

        list.add("C/- Unlisted Investsment Department");
        list.add("Level 14/420 George St");
        list.add("SYDNEY NSW 2000");
        float currentTopY = 0;
        float maxRowWidth = 0f;
        for (int i = 0; i < list.size(); i++) {
            currentTopY = topY - i * 11;
            canvas.beginText()
                    .setFontAndSize(baseFont, fontSize)
                    .setColor(Constant.fontColor, true)
                    .setLeading(leading)
                    .moveText(topX, currentTopY);
            String text = list.get(i);
            canvas.newlineShowText(text);
            canvas.endText();
            float tempW = baseFont.getWidth(text, fontSize);
            if (tempW > maxRowWidth) {
                maxRowWidth = tempW;
            }
        }

        Point start = new Point((int) (config.accountNameStart.getX() - 1), (int) (config.accountNameStart.getY() - 2));
        Point end = new Point((int) (config.accountNameStart.getX() + maxRowWidth), (int) (Constant.rectangle.getHeight() - currentTopY + 8));
        addRectangle4Layout(start, end, "Left Top", config);
    }

    private static float insertLeftHeader(PdfCanvas canvas, Config config) throws IOException {
        PdfFont headerFont = FontUtil.createFont(FontConstants.HELVETICA_BOLD);
        PdfFont baseFont = FontUtil.createFont(FontConstants.HELVETICA);
        //TODO
        int fontSize1 = 14;
        int fontSize2 = 12;
        int fontSizeBase = 10;
        float leading = 6f;
        int topX = config.headerStart.getX();
        float topY = Constant.rectangle.getHeight() - config.headerStart.getY();

        List<String> list = new ArrayList<>();
        list.add("Distribution Statement");
        list.add("Period Ended " + Constant.fmtFull.format(config.payDate));
        list.add(config.fundStr);
        float maxRowWidth = 0;
        float currentTopY = topY;
        for (int i = 0; i < list.size(); i++) {
            currentTopY = topY - i * 15;
            int fontSize = fontSize2;
            if (i == 0) {
                fontSize = fontSize1;
            }
            canvas.beginText()
                    .setFontAndSize(headerFont, fontSize)
                    .setColor(Constant.fontColor, true)
                    .setLeading(leading)
                    .moveText(topX, currentTopY);
            String text = list.get(i);
            canvas.newlineShowText(text);
            canvas.endText();
            float tempW = headerFont.getWidth(text, fontSize);
            if (tempW > maxRowWidth) {
                maxRowWidth = tempW;
            }

        }
        Point start = new Point((int) (config.headerStart.getX() - 1), (int) (config.headerStart.getY() - 6));
        Point end = new Point((int) (config.headerStart.getX() + maxRowWidth + 1), (int) (Constant.rectangle.getHeight() - currentTopY + 10));
        addRectangle4Layout(start, end, "Distribution Statement", config);

        float dearInvestorBeginTop = currentTopY - 20;
        currentTopY = dearInvestorBeginTop;
        canvas.beginText()
                .setFontAndSize(baseFont, fontSizeBase)
                .setColor(Constant.fontColor, true)
                .setLeading(leading)
                .moveText(topX, currentTopY);
        canvas.newlineShowText("Dear Investor,");
        canvas.endText();

        currentTopY = currentTopY - 20;


        List<String> content = new ArrayList<>();
        content.add(MessageFormat.format("Thank you for investing in the {0}. Set out below are the details of your", config.fundStr));
        content.add("distribution. Please keep this statement for future reference.");
        maxRowWidth = 0;
        for (String row : content) {
            canvas.beginText()
                    .setFontAndSize(baseFont, fontSizeBase)
                    .setColor(Constant.fontColor, true)
                    .setLeading(leading)
                    .moveText(topX, currentTopY);
            canvas.newlineShowText(row);
            canvas.endText();
            currentTopY = currentTopY - 10;
            float tempW = baseFont.getWidth(row, fontSizeBase);
            if (tempW > maxRowWidth) {
                maxRowWidth = tempW;
            }
        }
        Point start2 = new Point((int) (config.headerStart.getX() - 1), (int) (Constant.rectangle.getHeight() - dearInvestorBeginTop - 2));
        Point end2 = new Point((int) (config.headerStart.getX() + maxRowWidth + 1), (int) (Constant.rectangle.getHeight() - currentTopY));
        addRectangle4Layout(start2, end2, "Left Dear", config);

        return currentTopY;
    }


    private static float insertLeftFooter(PdfCanvas canvas, float tableEnd, Config config) throws IOException {
        PdfFont baseFont = FontUtil.createFont(FontConstants.HELVETICA);
        int fontSize = 10;
        float leading = 6f;
        int topX = config.headerStart.getX();
        float topY = Constant.rectangle.getHeight() - tableEnd;

        List<String> list = new ArrayList<>();
        list.add("Distribution Amount: " + config.netAmount);
        list.add(MessageFormat.format("{0} of your distribution re-invested into {1} units in the {2}", config.netAmount, config.unit, config.fundStr));
        list.add(MessageFormat.format("at {0} effective on {1}.", config.unitPrice, Constant.fmtshort.format(config.payDate)));

        List<String> list2 = new ArrayList<>();
        list2.add("If you have any questions about your investment, please contact your financial adviser, or call us on");
        list2.add("1300 019 633.");

        List<String> list3 = new ArrayList<>();
        list3.add("Kind regards,");
        list3.add("Janus Henderson Investors");
        list3.add("Client Services");


        List<List<String>> ct = new ArrayList<>();
        ct.add(list);
        ct.add(list2);
        ct.add(list3);


        float currentTopY = topY;
        for (List<String> rows : ct) {
            float maxRowWidth = 0;
            Point start = new Point((int) (config.headerStart.getX() - 1), (int) ((Constant.rectangle.getHeight() - currentTopY - 2)));
            for (String row : rows) {
                canvas.beginText()
                        .setFontAndSize(baseFont, fontSize)
                        .setColor(Constant.fontColor, true)
                        .setLeading(leading)
                        .moveText(topX, currentTopY);
                canvas.newlineShowText(row);
                canvas.endText();
                currentTopY = currentTopY - 10;
                float tempW = baseFont.getWidth(row, fontSize);
                if (tempW > maxRowWidth) {
                    maxRowWidth = tempW;
                }
            }
            Point end = new Point((int) (config.headerStart.getX() + maxRowWidth + 1), (int) (Constant.rectangle.getHeight() - currentTopY - 2));
            addRectangle4Layout(start, end, "Footer Text", config);
            currentTopY = currentTopY - 12;
        }
        canvas.endText();

        return currentTopY;
    }

    private static void insertRightTop1(PdfCanvas canvas, Config config) throws IOException {
        PdfFont baseFont = FontUtil.createFont(FontConstants.HELVETICA);
        PdfFont baseFontBold = FontUtil.createFont(FontConstants.HELVETICA_BOLD);
        int fontSize = 8;
        float leading = 6f;
        int topX = config.top1Start.getX();
        float topY = Constant.rectangle.getHeight() - config.top1Start.getY();


        LinkedHashMap<String, String[]> tagValue = new LinkedHashMap<>();
        tagValue.put("Enquiries: 1300 019 633 or +61 3 9445 5067", null);
        tagValue.put("Mail:", new String[]{"GPO Box 804", "Melbourne VIC 3001"});
        tagValue.put("Fax:", new String[]{"1800 238 910"});
        tagValue.put("Web:", new String[]{"www.janushenderson.com/australia"});
        tagValue.put("Email:", new String[]{"clientservices.aus@janushenderson.com"});

        List<String> endList = new ArrayList<>();
        endList.add("Janus Henderson Investors (Australia)");
        endList.add("Funds Management Limited");
        endList.add("ABN 43 164 177 244 AFSL 444268");
        int i = 0;
        float currentTopY = topY;
        float maxRowWidth = 0;
        for (Map.Entry<String, String[]> entry : tagValue.entrySet()) {
            float tempX = topX + 40;
            String tag = entry.getKey();
            String[] values = entry.getValue();
            canvas.beginText()
                    .setFontAndSize(baseFontBold, fontSize)
                    .setColor(Constant.fontColor, true)
                    .setLeading(leading)
                    .moveText(topX, currentTopY);
            canvas.newlineShowText(tag);
            canvas.endText();

            if (values != null) {
                for (String value : values) {
                    canvas.beginText()
                            .setFontAndSize(baseFont, fontSize)
                            .setColor(Constant.fontColor, true)
                            .setLeading(leading)
                            .moveText(tempX, currentTopY);
                    canvas.newlineShowText(value);
                    canvas.endText();
                    currentTopY = currentTopY - 10;
                    float tempW = baseFont.getWidth(value, fontSize);
                    if (tempW > maxRowWidth) {
                        maxRowWidth = tempW;
                    }
                }
                currentTopY = currentTopY + 10;
            }
            currentTopY = currentTopY - 12.7f;
            i++;
        }
        for (i = 0; i < endList.size(); i++) {
            String value = endList.get(i);
            canvas.beginText()
                    .setFontAndSize(baseFontBold, fontSize)
                    .setColor(Constant.fontColor, true)
                    .setLeading(leading)
                    .moveText(topX, currentTopY);
            canvas.newlineShowText(value);
            canvas.endText();
            currentTopY = currentTopY - 10;
        }

        Point start = new Point((int) (config.top1Start.getX() - 1), (int) (config.top1Start.getY() - 2));
        Point end = new Point((int) (config.top1Start.getX() + maxRowWidth + 42), (int) (Constant.rectangle.getHeight() - currentTopY - 1));
        addRectangle4Layout(start, end, "Right Top1", config);
    }

    private static void insertRightTop2(PdfCanvas canvas, Config config) throws IOException {
        PdfFont baseFont = FontUtil.createFont(FontConstants.HELVETICA);
        PdfFont baseFontBold = FontUtil.createFont(FontConstants.HELVETICA_BOLD);
        int fontSize = 10;
        float leading = 6f;
        int topX = config.top2Start.getX();
        float topY = Constant.rectangle.getHeight() - config.top2Start.getY();
        LinkedHashMap<String, String[]> tagValue = new LinkedHashMap<>();
        tagValue.put("Page", new String[]{"1 of 1"});
        tagValue.put("Date", new String[]{Constant.fmtFull.format(config.tradeDate)});
        String fundNumber = config.accountName;
        List<String> fundList = new ArrayList<>();
        boolean wrap = false;
        float totalWidth = baseFont.getWidth(fundNumber, fontSize);
        if (totalWidth > Constant.accountNameRightRowMax) {
            wrap = true;
            int len = fundNumber.length();
            for (int i = 0; i < len; i++) {
                String temp = StringUtils.substring(fundNumber, 0, len - i);
                if (baseFont.getWidth(temp, fontSize) < Constant.accountNameRightRowMax) {
                    String temp2 = StringUtils.substring(fundNumber, len - i, len);
                    fundList.add(temp);
                    fundList.add(temp2);
                    break;
                }
            }
        } else {
            fundList.add(fundNumber);
        }

//        tagValue.put("Investor name", new String[]{"State Street Australia Ltd (Acf Ibfs Re", "Ams Balanced Fund)"});
        tagValue.put("Investor name", fundList.toArray(new String[]{}));
        tagValue.put("Investor number", new String[]{config.accountNumber});
        tagValue.put("TFN/ABN status", new String[]{"Supplied"});
        tagValue.put("Financial adviser", new String[]{"FMD", "C/- Janus Henderson", " VIC"});
        float tagBottom = 5f;
        float currentY = topY;
        float tagHeight = 12f;
        float valueHeight = 12f;
        float maxRowWidth = 0;
        for (Map.Entry<String, String[]> entry : tagValue.entrySet()) {
            String tag = entry.getKey();
            String[] values = entry.getValue();
            canvas.beginText()
                    .setFontAndSize(baseFontBold, fontSize)
                    .setColor(Constant.fontColor, true)
                    .setLeading(leading)
                    .moveText(topX, currentY);
            canvas.newlineShowText(tag);
            canvas.endText();
            currentY = currentY - tagHeight;
            for (int i = 0; i < values.length; i++) {
                String temp = values[i];
                canvas.beginText()
                        .setFontAndSize(baseFont, fontSize)
                        .setColor(Constant.fontColor, true)
                        .setLeading(leading)
                        .moveText(topX, currentY);
                canvas.newlineShowText(temp);
                canvas.endText();
                currentY = currentY - valueHeight;
                float tempW = baseFont.getWidth(temp, fontSize);
                if (tempW > maxRowWidth) {
                    maxRowWidth = tempW;
                }

            }
            currentY = currentY - tagBottom;
        }

        Point start = new Point((int) (config.top2Start.getX() - 1), (int) (config.top2Start.getY() - 2));
        Point end = new Point((int) (config.top2Start.getX() + maxRowWidth + 2), (int) (Constant.rectangle.getHeight() - currentY - 8));
        addRectangle4Layout(start, end, "Right Top2", config);
    }

    private static void getTableSummary(Config config) {
        String unitPrice = randomNumber(1, 4, true);
        String unitBalance = randomNumber(8, 4, false);
        List<List<String>> listList = new ArrayList<>();
        List<String> row = new ArrayList<>();
        row.add(Constant.fmtshort.format(config.payDate));
//        row.add("$1.0669");
        row.add(unitPrice);
        row.add(unitBalance);
        row.add(randomNumber(8, 2, true));

        listList.add(row);
        config.table4Summary = listList;
        config.unitPrice = unitPrice;
        config.unitBalance = unitBalance;
//        return listList;
    }

    private static void getTableDetail(Config config) {
        String netAmount = randomNumber(5, 2, true);
        List<List<String>> listList = new ArrayList<>();
        int count = RandomUtil.randomInt(Constant.detailMaxRow, 1);
        for (int i = 0; i < count; i++) {
            List<String> row = new ArrayList<>();
            row.add(Constant.fmtshort.format(config.tradeDate));
            row.add(randomNumber(0, 6, false));
            row.add(netAmount);
            row.add(randomNumber(0, 2, true));
            row.add(randomNumber(0, 2, true));
            row.add(netAmount);
            listList.add(row);
        }
        config.table4Detail = listList;
        config.netAmount = netAmount;
    }

    private static float drawTable(PdfCanvas canvas, float tableBegin, Config config) throws IOException {
        float currentY = tableBegin;
        String summaryTitle = "Holding Summary as at Period End Date";
        String detailTitle = "Distribution Details";
        List<List<String>> table4Summary = config.table4Summary;
        List<List<String>> table4Detail = config.table4Detail;
        float xBegin = config.headerStart.getX();
        PdfFont baseFontBold = FontUtil.createFont(FontConstants.HELVETICA_BOLD);
        PdfFont baseFont = FontUtil.createFont(FontConstants.HELVETICA);
        int fontSizeHeader = 10;
        int fontSizeBody = 10;
        float leading = 6f;
        int lineWidth = 495;
        float bottomHeight = 0.5f;

        currentY = currentY - 20;
        canvas.beginText()
                .setFontAndSize(baseFontBold, fontSizeHeader)
                .setColor(Constant.fontColor, true)
                .setLeading(leading)
                .moveText(xBegin, currentY);
        canvas.newlineShowText(summaryTitle);
        canvas.endText();

        float summaryW = baseFontBold.getWidth(summaryTitle, fontSizeHeader);
        Point startSTDate = new Point((int) (config.headerStart.getX() + 0f), (int) (Constant.rectangle.getHeight() - currentY - 3));
        Point endSTDate = new Point((int) (config.headerStart.getX() + summaryW + 3), (int) (Constant.rectangle.getHeight() - currentY + 9));
        addRectangle4Layout(startSTDate, endSTDate, summaryTitle, config);

        currentY = currentY - 20;

        float summaryHeaderHeight = 17f;
        canvas.setLineWidth(summaryHeaderHeight).setStrokeColor(Constant.fontColor);
        float summaryLineTop = Constant.rectangle.getHeight() - currentY;
        Point summaryTableFirstP = new Point((int) xBegin - 2, (int) summaryLineTop - 10);
        Point summaryHeaderStart = new Point((int) xBegin, (int) summaryLineTop);
        Point summaryHeaderEnd = new Point((int) xBegin + lineWidth, (int) summaryLineTop);
        Line summaryLine = new Line(summaryHeaderStart, summaryHeaderEnd);
        drawLine(canvas, summaryLine, Constant.rectangle.getHeight());
        canvas.stroke();

        /**
         * except the first header because it left align
         * other right align
         * Date
         * Unit Price
         * Units Held
         * Value
         */
        LinkedHashMap<String, Float> summaryHeaderXEnd = new LinkedHashMap<>();
        summaryHeaderXEnd.put("Unit Price", xBegin + 200);
        summaryHeaderXEnd.put("Units Held", xBegin + 350);
        summaryHeaderXEnd.put("Value", xBegin + 490);

        float summaryHeaderY = currentY + 4;
        canvas.beginText()
                .setFontAndSize(baseFontBold, fontSizeHeader)
                .setColor(Color.WHITE, true)
                .setLeading(leading)
                .moveText(xBegin + 1, summaryHeaderY);
        canvas.newlineShowText("Date");
        canvas.endText();
        float headerW = baseFontBold.getWidth("Date", fontSizeHeader);
        Point startSDate = new Point((int) (config.headerStart.getX() + 0), (int) (Constant.rectangle.getHeight() - summaryHeaderY - 4));
        Point endSDate = new Point((int) (config.headerStart.getX() + headerW + 3), (int) (Constant.rectangle.getHeight() - summaryHeaderY + 9));
        addRectangle4TableCell(startSDate, endSDate, "SH=Date", config);


        for (Map.Entry<String, Float> entry : summaryHeaderXEnd.entrySet()) {
            String header = entry.getKey();
            Float xEnd = entry.getValue();
            headerW = baseFontBold.getWidth(header, fontSizeHeader);
            xEnd = xEnd - headerW;

            canvas.beginText()
                    .setFontAndSize(baseFontBold, fontSizeHeader)
                    .setColor(Color.WHITE, true)
                    .setLeading(leading)
                    .moveText(xEnd, summaryHeaderY);
            canvas.newlineShowText(header);
            canvas.endText();
            Point start = new Point((int) (xEnd + 0), (int) (Constant.rectangle.getHeight() - summaryHeaderY - 4));
            Point end = new Point((int) (xEnd + headerW + 2), (int) (Constant.rectangle.getHeight() - summaryHeaderY + 9));
            addRectangle4TableCell(start, end, "SH=" + header, config);
        }

        /**
         * end of summary table header
         */
        float rowHeight = 12f;
        currentY = currentY - 12f;
        Point summaryTableLastP = null;
        for (int i = 0; i < table4Summary.size(); i++) {

            List<String> row = table4Summary.get(i);
            int col = 0;
            for (String cvalue : row) {
                if (col == 0) {
                    canvas.beginText()
                            .setFontAndSize(baseFont, fontSizeBody)
                            .setColor(Constant.fontColor, true)
                            .setLeading(leading)
                            .moveText(xBegin + 1, currentY);
                    canvas.newlineShowText(cvalue);
                    canvas.endText();
                    headerW = baseFont.getWidth(cvalue, fontSizeBody);
                    Point start = new Point((int) (xBegin + 0), (int) (Constant.rectangle.getHeight() - currentY - 4));
                    Point end = new Point((int) (xBegin + headerW + 3), (int) (Constant.rectangle.getHeight() - currentY + 9));
                    addRectangle4TableCell(start, end, "SB=row:" + i + ",col:" + col, config);
                } else {
                    Map.Entry<String, Float> entry = (Map.Entry<String, Float>) getEntry(summaryHeaderXEnd, col - 1);
                    Float xEnd = entry.getValue();
                    headerW = baseFont.getWidth(cvalue, fontSizeBody);
                    xEnd = xEnd - headerW;
                    canvas.beginText()
                            .setFontAndSize(baseFont, fontSizeBody)
                            .setColor(Constant.fontColor, true)
                            .setLeading(leading)
                            .moveText(xEnd, currentY);
                    canvas.newlineShowText(cvalue);
                    canvas.endText();
                    Point start = new Point((int) (xEnd + 0), (int) (Constant.rectangle.getHeight() - currentY - 4));
                    Point end = new Point((int) (xEnd + headerW + 3), (int) (Constant.rectangle.getHeight() - currentY + 9));
                    addRectangle4TableCell(start, end, "SB=row:" + i + ",col:" + col, config);
                    if(i == table4Summary.size() - 1){
                        summaryTableLastP = new Point((int) (xEnd + headerW + 3 + 3), (int) (Constant.rectangle.getHeight() - currentY + 9 + 2));
                    }
                }
                col++;
            }
            currentY = currentY - rowHeight;
        }
        //ADD summary table outline
        addRectangle4TableOutline(summaryTableFirstP, summaryTableLastP, "Summary Table-Outline", config);
        /**
         * end of summary table body
         */
        currentY = currentY - 2;

        canvas.setLineWidth(bottomHeight).setStrokeColor(Constant.fontColor);
        float summaryBottomLineTop = Constant.rectangle.getHeight() - currentY;

        Point summaryBottomStart = new Point((int) xBegin, (int) summaryBottomLineTop);
        Point summaryBottomEnd = new Point((int) xBegin + lineWidth, (int) summaryBottomLineTop);
        Line summaryBottomLine = new Line(summaryBottomStart, summaryBottomEnd);
        drawLine(canvas, summaryBottomLine, Constant.rectangle.getHeight());
        canvas.stroke();
        /**
         * begin to draw detail title
         */
        currentY = currentY - 10;
        canvas.beginText()
                .setFontAndSize(baseFontBold, fontSizeHeader)
                .setColor(Constant.fontColor, true)
                .setLeading(leading)
                .moveText(xBegin, currentY);
        canvas.newlineShowText(detailTitle);
        canvas.endText();

        float detailW = baseFontBold.getWidth(detailTitle, fontSizeHeader);

        Point startDTDate = new Point((int) (config.headerStart.getX() + 0), (int) (Constant.rectangle.getHeight() - currentY - 3));
        Point endDTDate = new Point((int) (config.headerStart.getX() + detailW + 3), (int) (Constant.rectangle.getHeight() - currentY + 8));
        addRectangle4Layout(startDTDate, endDTDate, summaryTitle, config);

        currentY = currentY - 24;
        /**
         * detail table
         */
        float detailHeaderHeight = 26f;
        canvas.setLineWidth(detailHeaderHeight).setStrokeColor(Constant.fontColor);
        float detailLineTop = Constant.rectangle.getHeight() - currentY;
        Point detailTableFirstP = new Point((int) xBegin - 2, (int) detailLineTop - 14);
        Point detailHeaderStart = new Point((int) xBegin, (int) detailLineTop);
        Point detailHeaderEnd = new Point((int) xBegin + lineWidth, (int) detailLineTop);
        Line detailLine = new Line(detailHeaderStart, detailHeaderEnd);
        drawLine(canvas, detailLine, Constant.rectangle.getHeight());
        canvas.stroke();


        /**
         * except the first header because it left align
         * other right align
         */
        LinkedHashMap<String, Float> detailHeaderXEnd = new LinkedHashMap<>();
        detailHeaderXEnd.put("Cents\nPer Unit", xBegin + 120);
        detailHeaderXEnd.put("Gross\nAmount", xBegin + 220);
        detailHeaderXEnd.put("TFN\nWithholding Tax", xBegin + 305);
        detailHeaderXEnd.put("Non-Resident\nWithholding Tax", xBegin + 400);
        detailHeaderXEnd.put("Net\nAmount", xBegin + 490);
        float detailHeaderY = currentY + 9;
        float detailHeaderY2 = currentY - 1;
        canvas.beginText()
                .setFontAndSize(baseFontBold, fontSizeHeader)
                .setColor(Color.WHITE, true)
                .setLeading(leading)
                .moveText(xBegin + 1, detailHeaderY);
        canvas.newlineShowText("Date");
        canvas.endText();
        canvas.beginText()
                .setFontAndSize(baseFontBold, fontSizeHeader)
                .setColor(Color.WHITE, true)
                .setLeading(leading)
                .moveText(xBegin + 1, detailHeaderY2);
        canvas.newlineShowText("Paid");
        canvas.endText();

        headerW = baseFontBold.getWidth("Paid", fontSizeHeader);
        Point startDDate = new Point((int) (xBegin + 0), (int) (Constant.rectangle.getHeight() - detailHeaderY2 - 13));
        Point endDDate = new Point((int) (xBegin + headerW + 3), (int) (Constant.rectangle.getHeight() - detailHeaderY2 + 9));
        addRectangle4TableCell(startDDate, endDDate, "DH=Paid", config);

        for (Map.Entry<String, Float> entry : detailHeaderXEnd.entrySet()) {
            String[] headerAr = entry.getKey().split("\n");
            Float xEnd = entry.getValue();
            float headerW1 = baseFontBold.getWidth(headerAr[0], fontSizeHeader);
            Float xEnd1 = xEnd - headerW1;

            float headerW2 = baseFontBold.getWidth(headerAr[1], fontSizeHeader);
            Float xEnd2 = xEnd - headerW2;

            canvas.beginText()
                    .setFontAndSize(baseFontBold, fontSizeHeader)
                    .setColor(Color.WHITE, true)
                    .setLeading(leading)
                    .moveText(xEnd1, detailHeaderY);
            canvas.newlineShowText(headerAr[0]);
            canvas.endText();
            /***************************/
            canvas.beginText()
                    .setFontAndSize(baseFontBold, fontSizeHeader)
                    .setColor(Color.WHITE, true)
                    .setLeading(leading)
                    .moveText(xEnd2, detailHeaderY2);
            canvas.newlineShowText(headerAr[1]);
            canvas.endText();
            Point start = new Point((int) (xEnd1 > xEnd2 ? xEnd2 : xEnd1 + 0), (int) (Constant.rectangle.getHeight() - detailHeaderY2 - 13));
            Point end = new Point((int) (xEnd.floatValue() + 1), (int) (Constant.rectangle.getHeight() - detailHeaderY2 + 9));
            addRectangle4TableCell(start, end, "DH=" + entry.getKey(), config);
        }
        currentY = currentY - 18;
        Point detailTableLastP = null;
        for (int i = 0; i < table4Detail.size(); i++) {

            List<String> row = table4Detail.get(i);
            int col = 0;
            for (String cvalue : row) {
                if (col == 0) {
                    canvas.beginText()
                            .setFontAndSize(baseFont, fontSizeBody)
                            .setColor(Constant.fontColor, true)
                            .setLeading(leading)
                            .moveText(xBegin + 1, currentY);
                    canvas.newlineShowText(cvalue);
                    canvas.endText();
                    headerW = baseFont.getWidth(cvalue, fontSizeBody);
                    Point start = new Point((int) (xBegin + 0), (int) (Constant.rectangle.getHeight() - currentY - 4));
                    Point end = new Point((int) (xBegin + headerW + 3), (int) (Constant.rectangle.getHeight() - currentY + 9));
                    addRectangle4TableCell(start, end, "DB=row:" + i + ",col:" + col, config);
                } else {
                    Map.Entry<String, Float> entry = (Map.Entry<String, Float>) getEntry(detailHeaderXEnd, col - 1);
                    Float xEnd = entry.getValue();
                    headerW = baseFont.getWidth(cvalue, fontSizeBody);
                    xEnd = xEnd - headerW;
                    canvas.beginText()
                            .setFontAndSize(baseFont, fontSizeBody)
                            .setColor(Constant.fontColor, true)
                            .setLeading(leading)
                            .moveText(xEnd, currentY);
                    canvas.newlineShowText(cvalue);
                    canvas.endText();
                    Point start = new Point((int) (xEnd + 0), (int) (Constant.rectangle.getHeight() - currentY - 4));
                    Point end = new Point((int) (xEnd + headerW + 3), (int) (Constant.rectangle.getHeight() - currentY + 9));
                    addRectangle4TableCell(start, end, "DB=row:" + i + ",col:" + col, config);
                    if(i == table4Detail.size() - 1){
                        detailTableLastP = new Point((int) (xEnd + headerW + 3 + 3), (int) (Constant.rectangle.getHeight() - currentY + 9 + 2));
                    }
                }
                col++;
            }
            currentY = currentY - rowHeight;
        }
        //ADD summary table outline
        addRectangle4TableOutline(detailTableFirstP, detailTableLastP, "Detail Table-Outline", config);

        currentY = currentY - 4;
        canvas.setLineWidth(bottomHeight).setStrokeColor(Constant.fontColor);
        float detailBottomLineTop = Constant.rectangle.getHeight() - currentY;
        Point detailBottomStart = new Point((int) xBegin, (int) detailBottomLineTop);
        Point detailBottomEnd = new Point((int) xBegin + lineWidth, (int) detailBottomLineTop);
        Line detailBottomLine = new Line(detailBottomStart, detailBottomEnd);
        drawLine(canvas, detailBottomLine, Constant.rectangle.getHeight());
        canvas.stroke();
        currentY = currentY - 10;


        return currentY;
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

}