package com.poc.pdf.temp;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.poc.pdf.simulate.SimulatorBase;
import com.poc.pdf.util.FontUtil;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.PDFRenderer;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class PIMCOSimu extends SimulatorBase {
    private Random random = new Random();

    private String FOLDER_SIGNATURES = "signatures";

    private String PATH_LOGO = "simulate/pimco/config/PIMCO.png";
    private String PATH_SIGNATURES;

    private Color COLOR_TEXT = new DeviceRgb(0, 0, 0);
    private Color COLOR_TABLE_HEADER = new DeviceRgb(91, 138, 181);
    private Color COLOR_TABLE_HEADER_TEXT = new DeviceRgb(255, 255, 255);
    private Color COLOR_TABLE_FOOTER_LINE = new DeviceRgb(183, 183, 183);

    private float PADDING_BOX_LEFT = 0.5f;
    private float PADDING_BOX_RIGHT = 0.5f;
    private float PADDING_BOX_TOP = 0f;
    private float PADDING_BOX_BOTTOM = 2.5f;

    private float WIDTH_SIGNATURE = 50f;
    private float WIDTH_LOGO = 138f;
    private float HEIGHT_LOGO = 20f;
    private float WIDTH_R_TITLE = 175f;
    private float WIDTH_L_TITLE_REPORT_DESC = 310f;

    private float[] LINE_DASH = {5f, 0f};
    private float LINE_DASH_PHASE = 0f;

    private float[] BOX_LINE_DASH = {5f, 5f};
    private float BOX_LINE_DASH_PHASE = 1f;

    private float X_START = 43f;
    private float X_END = 555f;
    private float X_SIGNATURE = 398f;
    private float X_RIGHT_TITLE = 398f;
    private float X_RIGHT_TITLE_VALUE = 440f;
    private float X_LEFT_TITLE_ADDRESS = 100f;

    private float Y_LOGO = 794f;
    private float Y_SIGNATURE = 20f;
    private float Y_LEFT_TITLE_REPORT_TITLE = 506;
    private float Y_LEFT_TITLE_REPORT_DATE = 475;
    private float Y_LEFT_TITLE_REPORT_DESC = 449;
    private float Y_RIGHT_TITLE = 798;
    private float Y_LEFT_TITLE_ADDRESS = 654;

    private float HEIGHT_TABLE_HEADER = 17f;
    private float HEIGHT_TABLE_TITLE_HEADER = 22f;
    private float HEIGHT_R_TITLE_GROUP = 20f;
    private float HEIGHT_R_TITLE_ITEMS = 14.5f;
    private float HEIGHT_R_TITLE_INLINE = 13f;
    private float HEIGHT_R_TITLE_INLINE_S = 10f;
    private float HEIGHT_L_TITLE_REPORT_DESC = 12f;
    private float HEIGHT_L_TITLE_ADDRESS = 13f;
    private float HEIGHT_TABLE_FIRST_ROW = 17f;
    private float HEIGHT_TABLE_ROW = 16f;
    private float HEIGHT_TABLE_CELL = 11f;
    private float HEIGHT_TABLE_FOOTER = 12f;
    private float HEIGHT_TABLE_FOOTER_LINE = 0.5f;
    private float HEIGHT_TABLE_2_TITLE = 18f;
    private float HEIGHT_TITLE_TABLE1 = 40f;
    private float HEIGHT_TABLE1_TABLE2 = 20f;
    private float HEIGHT_TABLE2_SUMMARY = 14f;
    private float HEIGHT_TABLE2_SUMMARY_NOTE1_NOTE2 = 18f;
    private float HEIGHT_TABLE2_SUMMARY_NOTE2_REGARDS = 20f;

    private float FS_DEFAULT = 8f;
    private float FS_L_TITLE_ADDRESS = 10f;
    private float FS_L_TITLE_REPORT_TITLE = 18f;
    private float FS_L_TITLE_REPORT_DATE = 14f;
    private float FS_L_TITLE_REPORT_DESC = 10f;
    private float FS_TABLE_1_TITLE = 10f;
    private float FS_TABLE_HEADER = 9f;

    private float ZOOM_SCALE = 1;

    private String ALIGN_LEFT = "LEFT";
    private String ALIGN_RIGHT = "RIGHT";

    private PdfFont HELVETICA;
    private PdfFont HELVETICA_BOLD;
    private PdfFont HELVETICA_OBLIQUE;

    private PdfDocument doc;
    private PdfCanvas canvas;
    private PageSize pageSize;

    private XmlResult table;
    private XmlResult tableOutline;
    private XmlResult layout;
    private XmlResult signature;

    private Properties properties;

    private boolean isShowMarkRect() {
        return Boolean.valueOf(properties.getProperty("mark.enable"));
    }

    private boolean isShowNoise() {
        return Boolean.valueOf(properties.getProperty("noise.enable"));
    }

    interface Constant {
        String output = "simulate/pimco";
    }

    public PIMCOSimu(Properties properties) {
        try {
            HELVETICA = FontUtil.createFont(FontConstants.HELVETICA);
            HELVETICA_BOLD = FontUtil.createFont(FontConstants.HELVETICA_BOLD);
            HELVETICA_OBLIQUE = FontUtil.createFont(FontConstants.HELVETICA_OBLIQUE);
            FOLDER_SIGNATURES = properties.getProperty("signature.dir");
            this.properties = properties;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        Properties properties = load();
        int total = NumberUtils.toInt(properties.getProperty("pimco.total"), 0);
        String prefix = properties.getProperty("pimco.prefix");
        for (int i = 0; i < total; i++) {
            process(prefix + "-" + StringUtils.leftPad("" + i, 5, "0"), properties);
        }
    }

    public static void process(String fileName, Properties properties) throws Exception {
        PIMCOSimu sample1 = new PIMCOSimu(properties);
        sample1.doProcess(Constant.output, fileName);
    }

    private void doProcess(String targetFolder, String fileName) throws Exception {
        File file = new File(targetFolder);
        if (!file.exists()) {
            file.mkdir();
        }

        String pdfFileName = targetFolder + "/" + fileName + ".pdf";
        String imageFileName = targetFolder + "/" + fileName + ".jpg";
        String layoutFileName = targetFolder + "/" + fileName + "-layout.xml";
        String tableFileName = targetFolder + "/" + fileName + "-table.xml";
        String tableOutlineFileName = targetFolder + "/" + fileName + "-table-outline.xml";
        String signatureFileName = targetFolder + "/" + fileName + "-signature.xml";

        initial(pdfFileName);

        table = new XmlResult(targetFolder, fileName + "-table.xml", pageSize.getWidth(), pageSize.getHeight());
        tableOutline = new XmlResult(targetFolder, fileName + "-table-outline.xml", pageSize.getWidth(), pageSize.getHeight());
        layout = new XmlResult(targetFolder, fileName + "-layout.xml", pageSize.getWidth(), pageSize.getHeight());
        signature = new XmlResult(targetFolder, fileName + "-signature.xml", pageSize.getWidth(), pageSize.getHeight());

        double y = drawTitle();
        y = drawTable1(y);
        y = drawTable2(y);
        y = drawSummary(y);
        drawSignature();
        drawInterference();
        doc.close();


        PDDocument apachePdfDoc = PDDocument.load(new File(pdfFileName));
        PDFRenderer renderer = new PDFRenderer(apachePdfDoc);

//        BufferedImage image = new BufferedImage((int)pageSize.getWidth(), (int)pageSize.getHeight(), BufferedImage.TYPE_INT_RGB);
//        renderer.renderPageToGraphics(0, (Graphics2D)image.getGraphics());
        BufferedImage image = renderer.renderImage(0, 3);
        writeImage(image, new File(imageFileName));

        String output = toXml(table);
        FileUtils.write(new File(tableFileName), output);

        output = toXml(tableOutline);
        FileUtils.write(new File(tableOutlineFileName), output);

        output = toXml(layout);
        FileUtils.write(new File(layoutFileName), output);

        output = toXml(signature);
        FileUtils.write(new File(signatureFileName), output);
    }

    private void writeImage(BufferedImage image, File file) throws IOException {
        ImageIO.write(image, "PNG", file);
    }

    private void drawInterference() {
        if (!isShowNoise()) {
            return;
        }
        int lineCount = random.nextInt(4);
        for (int i = 0; i < lineCount; i++) {
            float x1 = random.nextFloat() * pageSize.getWidth();
            float x2 = random.nextFloat() * pageSize.getWidth();
            float y1 = random.nextFloat() * pageSize.getHeight();
            float y2 = random.nextFloat() * pageSize.getHeight();
            canvas.setColor(new DeviceRgb(random.nextFloat(), random.nextFloat(), random.nextFloat()), false)
                    .setLineWidth(0.5f)
                    .moveTo(x1, y1)
                    .lineTo(x2, y2)
                    .stroke();
        }
    }

    private void drawSignature() throws MalformedURLException {
        canvas.addImage(ImageDataFactory.create(PATH_SIGNATURES), new Rectangle(X_SIGNATURE, Y_SIGNATURE, WIDTH_SIGNATURE, WIDTH_SIGNATURE), true);
        drawSignatureBox(X_SIGNATURE, Y_SIGNATURE, WIDTH_SIGNATURE, WIDTH_SIGNATURE);
    }

    private double drawTitle() throws Exception {
        drawLogo();
        double y1 = drawRightTitle();
        double y2 = drawLeftTitle();
        return y1 < y2 ? y1 : y2;
    }

    private double drawSummary(double y) {
        String note1 = "* This is the latest available unit price for the date(s) selected";
        String note2 = "If you have any questions about your investment, please contact your adviser, or call us on " + randomPhone();
        String note3 = "Regards,";
        String note4 = randomText(20, 24) + ".";

        double x = X_START;
        canvas.beginText()
                .setFontAndSize(HELVETICA_OBLIQUE, FS_TABLE_1_TITLE)
                .moveText(x, y)
                .newlineShowText(note1)
                .endText();
        double width = HELVETICA_OBLIQUE.getWidth(note1, FS_TABLE_1_TITLE);
        drawLayoutBox(x, y, width, getLineHeight(HELVETICA_OBLIQUE, FS_TABLE_1_TITLE));

        y -= HEIGHT_TABLE2_SUMMARY_NOTE1_NOTE2;
        canvas.beginText()
                .setFontAndSize(HELVETICA, FS_TABLE_1_TITLE)
                .moveText(x, y)
                .newlineShowText(note2)
                .endText();
        width = HELVETICA.getWidth(note2, FS_TABLE_1_TITLE);
        drawLayoutBox(x, y, width, getLineHeight(HELVETICA, FS_TABLE_1_TITLE));

        y -= HEIGHT_TABLE2_SUMMARY_NOTE2_REGARDS;
        canvas.beginText()
                .setFontAndSize(HELVETICA, FS_TABLE_1_TITLE)
                .moveText(x, y)
                .newlineShowText(note3)
                .endText();
        width = HELVETICA.getWidth(note3, FS_TABLE_1_TITLE);
        drawLayoutBox(x, y, width, getLineHeight(HELVETICA, FS_TABLE_1_TITLE));

        y -= HEIGHT_TABLE2_SUMMARY_NOTE2_REGARDS;
        canvas.beginText()
                .setFontAndSize(HELVETICA, FS_TABLE_1_TITLE)
                .moveText(x, y)
                .newlineShowText(note4)
                .endText();
        width = HELVETICA.getWidth(note4, FS_TABLE_1_TITLE);
        drawLayoutBox(x, y, width, getLineHeight(HELVETICA, FS_TABLE_1_TITLE));
        return 0;
    }

    private double drawTable2(double y) {
        String reportDate = "Fund Transactions";
        double x = X_START;
        double startY = y;
        double maxWidth = 0;
        canvas.beginText()
                .setFontAndSize(HELVETICA_BOLD, FS_TABLE_1_TITLE)
                .moveText(x, y)
                .newlineShowText(reportDate)
                .endText();
        maxWidth = HELVETICA_BOLD.getWidth(reportDate, FS_TABLE_1_TITLE);
        drawTableBox(x, y, maxWidth, getLineHeight(HELVETICA_BOLD, FS_TABLE_1_TITLE));

        y -= HEIGHT_TABLE_2_TITLE;
        double startTableY = y - PADDING_BOX_BOTTOM;

        String groupName = randomText(50, 60);
        canvas.beginText()
                .setFontAndSize(HELVETICA_BOLD, FS_TABLE_1_TITLE)
                .moveText(x, y)
                .newlineShowText(groupName)
                .endText();
        maxWidth = HELVETICA_BOLD.getWidth(groupName, FS_TABLE_1_TITLE);
        drawTableBox(x, y, maxWidth, getLineHeight(HELVETICA_BOLD, FS_TABLE_1_TITLE));

        y -= HEIGHT_TABLE_TITLE_HEADER;
        canvas.setColor(COLOR_TABLE_HEADER, true)
                .rectangle(X_START, y, X_END - X_START, HEIGHT_TABLE_HEADER)
                .fill();

        Object[][] columns = {
                {"Date", HELVETICA_BOLD, X_START, 55f, ALIGN_LEFT},
                {"Transaction", HELVETICA_BOLD, X_START + 55f, 120f, ALIGN_LEFT},
                {"Unit Price *", HELVETICA_BOLD, X_START + 175f, 82f, ALIGN_RIGHT},
                {"Transaction Units", HELVETICA_BOLD, X_START + 257f, 85f, ALIGN_RIGHT},
                {"Amount", HELVETICA_BOLD, X_START + 342f, 85f, ALIGN_RIGHT},
                {"Unit Balance", HELVETICA_BOLD, X_START + 427, 85f, ALIGN_RIGHT}
        };

        int rowcount = random.nextInt(5);
        rowcount += 2;

        List<String[]> rowsRandom = new ArrayList<>();
        for (int i = 0; i < rowcount; i++) {
            if (i == 0) {
                rowsRandom.add(new String[]{
                        randomDate("MM/dd/yyyy"),
                        "Opening Balance",
                        "$" + randomDecimal(1, 4),
                        "",
                        "$" + randomDecimal(5, 2),
                        "" + randomDecimal(5, 4)
                });
            } else if (i == rowcount - 1) {
                rowsRandom.add(new String[]{
                        randomDate("MM/dd/yyyy"),
                        "Closing Balance",
                        "$" + randomDecimal(1, 4),
                        "",
                        "$" + randomDecimal(5, 2),
                        "" + randomDecimal(5, 4)
                });
            } else if (random.nextBoolean()) {
                rowsRandom.add(new String[]{
                        randomDate("MM/dd/yyyy"),
                        randomText(10, 40),
                        "$" + randomDecimal(1, 4),
                        "" + randomDecimal(5, 4),
                        "$" + randomDecimal(5, 2),
                        "" + randomDecimal(5, 4)
                });
            } else {
                rowsRandom.add(new String[]{
                        randomDate("MM/dd/yyyy"),
                        randomText(10, 40),
                        "$" + randomDecimal(1, 4),
                        "(" + randomDecimal(5, 4) + ")",
                        "($" + randomDecimal(5, 2) + ")",
                        "" + randomDecimal(5, 4)
                });
            }
        }
        String[][] rows = rowsRandom.toArray(new String[0][]);

        y += 6;
        for (Object[] column : columns) {
            String text = (String) column[0];
            PdfFont font = (PdfFont) column[1];
            float cx = (float) column[2];
            float cw = (float) column[3];
            String align = (String) column[4];
            if (ALIGN_LEFT.equals(align)) {
                canvas.beginText()
                        .setColor(COLOR_TABLE_HEADER_TEXT, true)
                        .setFontAndSize(font, FS_TABLE_HEADER)
                        .moveText(cx, y)
                        .newlineShowText(text)
                        .endText();
                maxWidth = font.getWidth(text, FS_TABLE_HEADER);
                drawTableBox(cx, y, maxWidth, getLineHeight(font, FS_TABLE_HEADER));
            } else {
                float textWidth = font.getWidth(text, FS_TABLE_HEADER);
                float tx = cw - textWidth + cx;
                canvas.beginText()
                        .setColor(COLOR_TABLE_HEADER_TEXT, true)
                        .setFontAndSize(font, FS_TABLE_HEADER)
                        .moveText(tx, y)
                        .newlineShowText(text)
                        .endText();
                maxWidth = font.getWidth(text, FS_TABLE_HEADER);
                drawTableBox(tx, y, maxWidth, getLineHeight(font, FS_TABLE_HEADER));
            }
        }

        y -= HEIGHT_TABLE_FIRST_ROW;
        canvas.setColor(COLOR_TEXT, true);
        for (int iRow = 0; iRow < rows.length; iRow++) {
            String[] row = rows[iRow];
            if (iRow != 0) {
                y -= HEIGHT_TABLE_ROW;
            }
            PdfFont font = iRow == 0 || iRow == rows.length - 1 ? HELVETICA_BOLD : HELVETICA;
            double lowset = y;
            double startLineLocation = y;
            for (int i = 0; i < row.length; i++) {
                startLineLocation = y;
                maxWidth = 0;
                double minX = Double.MAX_VALUE;
                startY = y;

                Object[] column = columns[i];
                String cellText = row[i];
                float cx = (float) column[2];
                float cw = (float) column[3];
                String align = (String) column[4];
                if (!StringUtils.isBlank(cellText)) {
                    List<String> lines = getLines(font, cellText, FS_TABLE_HEADER, cw);
                    for (int inLineIdex = 0; inLineIdex < lines.size(); inLineIdex++) {
                        if (inLineIdex != 0) {
                            startLineLocation -= HEIGHT_TABLE_CELL;
                        }
                        if (ALIGN_LEFT.equals(align)) {
                            canvas.beginText()
                                    .setFontAndSize(font, FS_TABLE_HEADER)
                                    .moveText(cx, startLineLocation)
                                    .newlineShowText(lines.get(inLineIdex))
                                    .endText();
                            minX = cx < minX ? cx : minX;
                            double width = font.getWidth(lines.get(inLineIdex), FS_TABLE_HEADER);
                            maxWidth = width > maxWidth ? width : maxWidth;
                        } else {
                            float textWidth = font.getWidth(lines.get(inLineIdex), FS_TABLE_HEADER);
                            float tx = cw - textWidth + cx;
                            canvas.beginText()
                                    .setFontAndSize(font, FS_TABLE_HEADER)
                                    .moveText(tx, startLineLocation)
                                    .newlineShowText(lines.get(inLineIdex))
                                    .endText();
                            minX = tx < minX ? tx : minX;
                            double width = font.getWidth(lines.get(inLineIdex), FS_TABLE_HEADER);
                            maxWidth = width > maxWidth ? width : maxWidth;
                        }
                    }
                    lowset = startLineLocation < lowset ? startLineLocation : lowset;
                    drawTableBox(minX, startLineLocation, maxWidth, startY - startLineLocation + getLineHeight(font, FS_TABLE_HEADER));
                }
            }
            y = lowset;
        }

        y -= HEIGHT_TABLE_FOOTER;
        canvas.setStrokeColor(COLOR_TABLE_FOOTER_LINE)
                .setLineWidth(HEIGHT_TABLE_FOOTER_LINE)
                .setLineDash(LINE_DASH, LINE_DASH_PHASE)
                .moveTo(X_START, y)
                .lineTo(X_END, y)
                .stroke();

        drawTableOutline(X_START, y, X_END-X_START, startTableY - y);
        return y - HEIGHT_TABLE2_SUMMARY;
    }

    private double drawTable1(double y) {
        String reportDate = "Investment Summary - " + randomDate("dd MMMM yyyy");
        double x = X_START;
        double startY = y;
        double maxWidth = 0;
        canvas.beginText()
                .setFontAndSize(HELVETICA_BOLD, FS_TABLE_1_TITLE)
                .moveText(x, y)
                .newlineShowText(reportDate)
                .endText();
        maxWidth = HELVETICA_BOLD.getWidth(reportDate, FS_TABLE_1_TITLE);
        drawTableBox(x, y, maxWidth, startY - y + getLineHeight(HELVETICA_BOLD, FS_TABLE_1_TITLE));

        double startTableY = y - PADDING_BOX_BOTTOM;
        y -= HEIGHT_TABLE_TITLE_HEADER;

        canvas.setColor(COLOR_TABLE_HEADER, true)
                .rectangle(X_START, y, X_END - X_START, HEIGHT_TABLE_HEADER)
                .fill();

        Object[][] columns = {
                {"Date", HELVETICA_BOLD, X_START, 55f, ALIGN_LEFT},
                {"Investment Fund", HELVETICA_BOLD, X_START + 55f, 120f, ALIGN_LEFT},
                {"Unit Price *", HELVETICA_BOLD, X_START + 175f, 82f, ALIGN_RIGHT},
                {"Units Held", HELVETICA_BOLD, X_START + 257f, 85f, ALIGN_RIGHT},
                {"Market Value", HELVETICA_BOLD, X_START + 342f, 85f, ALIGN_RIGHT},
                {"%", HELVETICA_BOLD, X_START + 427, 57f, ALIGN_RIGHT}
        };

        String[][] rows = {
                {
                        randomDate("dd/MM/yyyy"),
                        randomText(40, 50),
                        "$" + randomDecimal(1, 4),
                        randomDecimal(5, 4),
                        "$" + randomDecimal(5, 4),
                        randomDecimal(3, 2)
                }
        };

        String[] total = {
                randomDate("dd/MM/yyyy"),
                "Total Investments",
                "",
                "",
                "$" + randomDecimal(5, 4),
                randomDecimal(3, 2)
        };


        y += 6;
        for (Object[] column : columns) {
            String text = (String) column[0];
            PdfFont font = (PdfFont) column[1];
            float cx = (float) column[2];
            float cw = (float) column[3];
            String align = (String) column[4];
            if (ALIGN_LEFT.equals(align)) {
                canvas.beginText()
                        .setColor(COLOR_TABLE_HEADER_TEXT, true)
                        .setFontAndSize(font, FS_TABLE_HEADER)
                        .moveText(cx, y)
                        .newlineShowText(text)
                        .endText();
                maxWidth = font.getWidth(text, FS_TABLE_HEADER);
                drawTableBox(cx, y, maxWidth, getLineHeight(font, FS_TABLE_HEADER));
            } else {
                float textWidth = font.getWidth(text, FS_TABLE_HEADER);
                float tx = cw - textWidth + cx;
                canvas.beginText()
                        .setColor(COLOR_TABLE_HEADER_TEXT, true)
                        .setFontAndSize(font, FS_TABLE_HEADER)
                        .moveText(tx, y)
                        .newlineShowText(text)
                        .endText();
                maxWidth = font.getWidth(text, FS_TABLE_HEADER);
                drawTableBox(tx, y, maxWidth, getLineHeight(font, FS_TABLE_HEADER));
            }
        }
        y -= HEIGHT_TABLE_FIRST_ROW;
        canvas.setColor(COLOR_TEXT, true);

        for (int iRow = 0; iRow < rows.length; iRow++) {
            String[] row = rows[iRow];
            if (iRow != 0) {
                y -= HEIGHT_TABLE_ROW;
            }
            double lowset = y;
            double startLineLocation = y;
            for (int i = 0; i < row.length; i++) {
                startLineLocation = y;
                maxWidth = 0;
                double minX = Double.MAX_VALUE;
                startY = y;

                Object[] column = columns[i];
                String cellText = row[i];
                float cx = (float) column[2];
                float cw = (float) column[3];
                String align = (String) column[4];

                List<String> lines = getLines(HELVETICA, cellText, FS_TABLE_HEADER, cw);
                for (int inLineIdex = 0; inLineIdex < lines.size(); inLineIdex++) {
                    if (inLineIdex != 0) {
                        startLineLocation -= HEIGHT_TABLE_CELL;
                    }
                    if (ALIGN_LEFT.equals(align)) {
                        canvas.beginText()
                                .setFontAndSize(HELVETICA, FS_TABLE_HEADER)
                                .moveText(cx, startLineLocation)
                                .newlineShowText(lines.get(inLineIdex))
                                .endText();
                        minX = cx < minX ? cx : minX;
                        double width = HELVETICA.getWidth(lines.get(inLineIdex), FS_TABLE_HEADER);
                        maxWidth = width > maxWidth ? width : maxWidth;
                    } else {
                        float textWidth = HELVETICA.getWidth(lines.get(inLineIdex), FS_TABLE_HEADER);
                        float tx = cw - textWidth + cx;
                        canvas.beginText()
                                .setFontAndSize(HELVETICA, FS_TABLE_HEADER)
                                .moveText(tx, startLineLocation)
                                .newlineShowText(lines.get(inLineIdex))
                                .endText();
                        minX = tx < minX ? tx : minX;
                        double width = HELVETICA.getWidth(lines.get(inLineIdex), FS_TABLE_HEADER);
                        maxWidth = width > maxWidth ? width : maxWidth;
                    }
                }
                lowset = startLineLocation < lowset ? startLineLocation : lowset;
                drawTableBox(minX, startLineLocation, maxWidth, startY - startLineLocation + getLineHeight(HELVETICA, FS_TABLE_HEADER));
            }
            y = lowset;
        }

        y -= HEIGHT_TABLE_ROW;
        for (int i = 0; i < total.length; i++) {
            Object[] column = columns[i];
            String text = total[i];
            float cx = (float) column[2];
            float cw = (float) column[3];
            String align = (String) column[4];
            if (!StringUtils.isBlank(text)) {
                if (ALIGN_LEFT.equals(align)) {
                    canvas.beginText()
                            .setFontAndSize(HELVETICA_BOLD, FS_TABLE_HEADER)
                            .moveText(cx, y)
                            .newlineShowText(text)
                            .endText();
                    double width = HELVETICA_BOLD.getWidth(text, FS_TABLE_HEADER);
                    drawTableBox(cx, y, width, getLineHeight(HELVETICA, FS_TABLE_HEADER));
                } else {
                    float textWidth = HELVETICA_BOLD.getWidth(text, FS_TABLE_HEADER);
                    float tx = cw - textWidth + cx;
                    canvas.beginText()
                            .setFontAndSize(HELVETICA_BOLD, FS_TABLE_HEADER)
                            .moveText(tx, y)
                            .newlineShowText(text)
                            .endText();
                    double width = HELVETICA_BOLD.getWidth(text, FS_TABLE_HEADER);
                    drawTableBox(tx, y, width, getLineHeight(HELVETICA_BOLD, FS_TABLE_HEADER));
                }
            }
        }

        y -= HEIGHT_TABLE_FOOTER;
        canvas.setStrokeColor(COLOR_TABLE_FOOTER_LINE)
                .setLineWidth(HEIGHT_TABLE_FOOTER_LINE)
                .setLineDash(LINE_DASH, LINE_DASH_PHASE)
                .moveTo(X_START, y)
                .lineTo(X_END, y)
                .stroke();

        drawTableOutline(X_START, y, X_END-X_START, startTableY - y);
        return y - HEIGHT_TABLE1_TABLE2;
    }

    private double drawLeftTitle() {

        // region Values Defined
        String[] titleItems = {
                randomText(45, 50),
                randomText(45, 50),
                randomText(10, 20),
                randomText(5, 10)
        };
        String reportName = "Transaction Statement";
        String reportDate = randomDate("dd MMMM yyyy") + " to " + randomDate("dd MMMM yyyy");
        String reportDesc = "Set out below are the details of your investment and a transaction history for the period. Please keep this statement for future reference.";

        // endregion

        // region Fund Name and Address

        double x = X_LEFT_TITLE_ADDRESS;
        double y = Y_LEFT_TITLE_ADDRESS;
        double startY = y;
        double maxWidth = 0;
        for (int i = 0; i < titleItems.length; i++) {
            if (i != 0) {
                y -= HEIGHT_L_TITLE_ADDRESS;
            }
            String item = titleItems[i];
            canvas.beginText()
                    .setFontAndSize(HELVETICA, FS_L_TITLE_ADDRESS)
                    .moveText(x, y)
                    .newlineShowText(item)
                    .endText();

            double width = HELVETICA.getWidth(item, FS_L_TITLE_ADDRESS);
            maxWidth = width > maxWidth ? width : maxWidth;
        }
        drawLayoutBox(x, y, maxWidth, startY - y + getLineHeight(HELVETICA, FS_L_TITLE_ADDRESS));

        // endregion

        // region Report Name, Date and Description

        x = X_START;
        y = Y_LEFT_TITLE_REPORT_TITLE;
        canvas.beginText()
                .setFontAndSize(HELVETICA_BOLD, FS_L_TITLE_REPORT_TITLE)
                .moveText(x, y)
                .newlineShowText(reportName)
                .endText();
        maxWidth = HELVETICA_BOLD.getWidth(reportName, FS_L_TITLE_REPORT_TITLE);
        drawLayoutBox(x, y, maxWidth, getLineHeight(HELVETICA_BOLD, FS_L_TITLE_REPORT_TITLE));


        x = X_START;
        y = Y_LEFT_TITLE_REPORT_DATE;
        canvas.beginText()
                .setFontAndSize(HELVETICA_BOLD, FS_L_TITLE_REPORT_DATE)
                .moveText(x, y)
                .newlineShowText(reportDate)
                .endText();
        maxWidth = HELVETICA_BOLD.getWidth(reportDate, FS_L_TITLE_REPORT_DATE);
        drawLayoutBox(x, y, maxWidth, getLineHeight(HELVETICA_BOLD, FS_L_TITLE_REPORT_DATE));


        x = X_START;
        y = Y_LEFT_TITLE_REPORT_DESC;
        startY = y;
        maxWidth = 0;
        List<String> lines = getLines(HELVETICA, reportDesc, FS_L_TITLE_REPORT_DESC, WIDTH_L_TITLE_REPORT_DESC);
        for (int i = 0; i < lines.size(); i++) {
            if (i != 0) {
                y -= HEIGHT_L_TITLE_REPORT_DESC;
            }
            canvas.beginText()
                    .setFontAndSize(HELVETICA, FS_L_TITLE_REPORT_DESC)
                    .moveText(x, y)
                    .newlineShowText(lines.get(i))
                    .endText();

            double width = HELVETICA.getWidth(lines.get(i), FS_L_TITLE_ADDRESS);
            maxWidth = width > maxWidth ? width : maxWidth;
        }
        drawLayoutBox(x, y, maxWidth, startY - y + getLineHeight(HELVETICA, FS_L_TITLE_ADDRESS));

        // endregion

        return y - HEIGHT_TITLE_TABLE1;
    }

    private double drawRightTitle() {

        // region Value Defined
        String[][] keyValueItems = {
                {"Enquiries", randomPhone()},
                {"Mail:", randomText(10, 12) + "\n" + randomText(12, 18)},
                {"Fax:", randomFax()},
                {"Website", randomWebsite()},
                {"Email", randomEmail()}
        };

        String[] list1Items = {
                randomText(20, 25),
                randomText(18),
                randomText(18),
        };
        Object[][] list2Items = {
                {"Page", HELVETICA_BOLD},
                {"1 of 1", HELVETICA},
                {"Date", HELVETICA_BOLD},
                {randomDate("dd MMMM yyyy"), HELVETICA},
                {"Account Name", HELVETICA_BOLD},
                {randomText(45, 50), HELVETICA},
                {"Account Number", HELVETICA_BOLD},
                {randomNum(9), HELVETICA_BOLD},
                {"TFN/ABN Status", HELVETICA_BOLD},
                {randomStr(5, 6).toUpperCase(), HELVETICA},
                {"Dealer Group", HELVETICA_BOLD},
                {randomText(18, 22).toUpperCase(), HELVETICA},
                {"Financial Adviser", HELVETICA_BOLD},
                {randomText(18, 22), HELVETICA}
        };

        // endregion

        // region Left-Right 5 fields


        double x = X_RIGHT_TITLE;
        double y = Y_RIGHT_TITLE;
        double startY = y;
        double valueX = X_RIGHT_TITLE_VALUE;
        double maxWidth = 0;
        for (int i = 0; i < keyValueItems.length; i++) {
            if (i != 0) {
                y -= HEIGHT_R_TITLE_ITEMS;
            }
            String[] line = keyValueItems[i];
            String key = line[0];
            String value = line[1];

            canvas.beginText()
                    .setFontAndSize(HELVETICA_BOLD, FS_DEFAULT)
                    .moveText(x, y)
                    .newlineShowText(key)
                    .endText();

            String[] valueLines = value.split("\n");

            for (int iLine = 0; iLine < valueLines.length; iLine++) {
                if (iLine != 0) {
                    y -= HEIGHT_R_TITLE_INLINE;
                }
                String valueLine = valueLines[iLine];
                canvas.beginText()
                        .setFontAndSize(HELVETICA, FS_DEFAULT)
                        .moveText(valueX, y)
                        .newlineShowText(valueLine)
                        .endText();

                double keyValueWidth = HELVETICA.getWidth(valueLine, FS_DEFAULT) + valueX - x;
                maxWidth = maxWidth < keyValueWidth ? keyValueWidth : maxWidth;
            }
        }

        drawLayoutBox(x, y, maxWidth, startY - y + getLineHeight(HELVETICA, FS_DEFAULT));

        // endregion

        // region Middle 3 fields
        y = y - HEIGHT_R_TITLE_GROUP;
        startY = y;
        maxWidth = 0;
        for (int i = 0; i < list1Items.length; i++) {
            if (i != 0) {
                y -= HEIGHT_R_TITLE_ITEMS;
            }
            String item = list1Items[i];
            canvas.beginText()
                    .setFontAndSize(HELVETICA_BOLD, FS_DEFAULT)
                    .moveText(x, y)
                    .newlineShowText(item)
                    .endText();
            double width = HELVETICA_BOLD.getWidth(item, FS_DEFAULT);
            maxWidth = width > maxWidth ? width : maxWidth;
        }

        drawLayoutBox(x, y, maxWidth, startY - y + getLineHeight(HELVETICA_BOLD, FS_DEFAULT));

        // endregion

        // region Bottom Key Value Items
        y = y - HEIGHT_R_TITLE_GROUP;
        startY = y;
        maxWidth = 0;
        for (int i = 0; i < list2Items.length; i++) {
            if (i != 0) {
                y -= HEIGHT_R_TITLE_ITEMS;
            }
            Object[] objItem = list2Items[i];
            String item = (String) objItem[0];
            PdfFont font = (PdfFont) objItem[1];
            List<String> lines = getLines(font, item, FS_DEFAULT, WIDTH_R_TITLE);
            if (lines.size() > 1) {
                for (int iLine = 0; iLine < lines.size(); iLine++) {
                    if (iLine != 0) {
                        y -= HEIGHT_R_TITLE_INLINE_S;
                    }
                    canvas.beginText()
                            .setFontAndSize(font, FS_DEFAULT)
                            .moveText(x, y)
                            .newlineShowText(lines.get(iLine))
                            .endText();
                    double width = font.getWidth(lines.get(iLine), FS_DEFAULT);
                    maxWidth = width > maxWidth ? width : maxWidth;
                }
            } else {
                canvas.beginText()
                        .setFontAndSize(font, FS_DEFAULT)
                        .moveText(x, y)
                        .newlineShowText(item)
                        .endText();
                double width = font.getWidth(item, FS_DEFAULT);
                maxWidth = width > maxWidth ? width : maxWidth;
            }
        }
        drawLayoutBox(x, y, maxWidth, startY - y + getLineHeight(HELVETICA_BOLD, FS_DEFAULT));

        // endregion

        return y - HEIGHT_TITLE_TABLE1;
    }

    // region Drawing Box

    private void drawLayoutBox(double x, double y, double w, double h) {
        x = x - PADDING_BOX_LEFT;
        y = y - PADDING_BOX_BOTTOM;
        w = PADDING_BOX_LEFT + PADDING_BOX_RIGHT + w;
        h = PADDING_BOX_TOP + PADDING_BOX_BOTTOM + h;

        if (isShowMarkRect()) {
            canvas.setColor(Color.RED, false);
            canvas.setLineWidth(0.5f);
            canvas.setLineDash(BOX_LINE_DASH, BOX_LINE_DASH_PHASE);
            canvas.rectangle(x, y, w, h);
            canvas.stroke();
            canvas.setColor(COLOR_TEXT, true);
        }

        double xmin = x;
        double ymin = pageSize.getHeight() - y - h;
        double xmax = x + w;
        double ymax = pageSize.getHeight() - y;


        addXmlItem(layout, "cell", xmin, ymin, xmax, ymax);
    }

    private void drawTableBox(double x, double y, double w, double h) {
        x = x - PADDING_BOX_LEFT;
        y = y - PADDING_BOX_BOTTOM;
        w = PADDING_BOX_LEFT + PADDING_BOX_RIGHT + w;
        h = PADDING_BOX_TOP + PADDING_BOX_BOTTOM + h;

        if (isShowMarkRect()) {
            canvas.setColor(Color.RED, false);
            canvas.setLineWidth(0.5f);
            canvas.setLineDash(BOX_LINE_DASH, BOX_LINE_DASH_PHASE);
            canvas.rectangle(x, y, w, h);
            canvas.stroke();
            canvas.setColor(COLOR_TEXT, true);
        }

        double xmin = x;
        double ymin = pageSize.getHeight() - y - h;
        double xmax = x + w;
        double ymax = pageSize.getHeight() - y;

        addXmlItem(table, "cell", xmin, ymin, xmax, ymax);
    }

    private void drawTableOutline(double x, double y, double w, double h) {
        x = x - PADDING_BOX_LEFT;
        y = y - PADDING_BOX_BOTTOM;
        w = PADDING_BOX_LEFT + PADDING_BOX_RIGHT + w;
        h = PADDING_BOX_TOP + PADDING_BOX_BOTTOM + h;

        if (isShowMarkRect()) {
            canvas.setColor(Color.RED, false);
            canvas.setLineWidth(0.5f);
            canvas.setLineDash(BOX_LINE_DASH, BOX_LINE_DASH_PHASE);
            canvas.rectangle(x, y, w, h);
            canvas.stroke();
            canvas.setColor(COLOR_TEXT, true);
        }

        double xmin = x;
        double ymin = pageSize.getHeight() - y - h;
        double xmax = x + w;
        double ymax = pageSize.getHeight() - y;

        addXmlItem(tableOutline, "cell", xmin, ymin, xmax, ymax);
    }

    private void drawSignatureBox(double x, double y, double w, double h) {

        if (isShowMarkRect()) {
            canvas.setColor(Color.RED, false);
            canvas.setLineWidth(0.5f);
            canvas.setLineDash(BOX_LINE_DASH, BOX_LINE_DASH_PHASE);
            canvas.rectangle(x, y, w, h);
            canvas.stroke();
            canvas.setColor(COLOR_TEXT, true);
        }

        double xmin = x;
        double ymin = pageSize.getHeight() - y - h;
        double xmax = x + w;
        double ymax = pageSize.getHeight() - y;

        addXmlItem(signature, "cell", xmin, ymin, xmax, ymax);
    }

    private void addXmlItem(XmlResult result, String name, double xmin, double ymin, double xmax, double ymax) {
        result.addItem(name, xmin * ZOOM_SCALE, ymin * ZOOM_SCALE, xmax * ZOOM_SCALE, ymax * ZOOM_SCALE);
    }

    // endregion

    private float getLineHeight(PdfFont font, float fontSize) {
        return fontSize;
    }

    private void initial(String targetFileName) throws IOException {

        File targetFile = new File(targetFileName);
        File parent = targetFile.getParentFile();
        if (!parent.exists()) parent.mkdir();
        PdfWriter writer = new PdfWriter(targetFile);
        doc = new PdfDocument(writer);
        pageSize = new PageSize(PageSize.A4);
        PdfPage page = doc.addNewPage(pageSize);

        canvas = new PdfCanvas(page);

        String value = properties.getProperty("picture.width");
        float picWidth = NumberUtils.toFloat(value, -1);

        value = properties.getProperty("picture.height");
        float picHeight = NumberUtils.toFloat(value, -1);

        value = properties.getProperty("picture.scale.large");
        boolean largeScale = Boolean.valueOf(value);

        float pdfHeight = PageSize.A4.getHeight();
        float pdfWidth = PageSize.A4.getWidth();
        float scaleW = picWidth / pdfWidth;
        float scaleH = picHeight / pdfHeight;
        ZOOM_SCALE = largeScale ? Math.max(scaleW, scaleH) : Math.min(scaleW, scaleH);

        randomValue();
    }

    private void drawLogo() throws MalformedURLException {
        canvas.addImage(ImageDataFactory.create(PATH_LOGO), new Rectangle(X_START, Y_LOGO, WIDTH_LOGO, HEIGHT_LOGO), true);
    }

    private List<String> getLines(PdfFont font, String strValue, float fontSize, float maxWidth) {
        List<String> lines = new ArrayList<>();

        String remain = strValue;
        while (true) {
            float strWidth = font.getWidth(remain, fontSize);
            if (strWidth > maxWidth) {
                int charCount = remain.length();
                float widthPerChar = strWidth / charCount;
                int charsInline = (int) (maxWidth / widthPerChar);
                int lastSpace = remain.lastIndexOf(' ', charsInline);
                int splitChar = lastSpace == -1 ? charsInline : lastSpace + 1;
                lines.add(remain.substring(0, splitChar - 1));
                remain = remain.substring(splitChar);
            } else {
                lines.add(remain);
                break;
            }
        }
        return lines;
    }

    // region Random Functions

    private String randomEmail() {
        return randomStr(2, 20) + "@" + randomStr(2, 3) + "." + randomStr(2, 3);
    }

    private String randomWebsite() {
        return "www." + randomStr(2, 3) + "." + randomStr(2, 7) + "." + randomStr(2, 3);
    }

    private String randomPhone() {
        StringBuilder phone = new StringBuilder();
        phone.append(random.nextInt(9) + 1);
        phone.append(StringUtils.leftPad("" + random.nextInt(1000), 3, "0"));
        phone.append(' ');
        phone.append(StringUtils.leftPad("" + random.nextInt(1000), 3, "0"));
        phone.append(' ');
        phone.append(StringUtils.leftPad("" + random.nextInt(1000), 3, "0"));
        return phone.toString();
    }

    private String randomFax() {
        StringBuilder fax = new StringBuilder();
        fax.append(StringUtils.leftPad("" + random.nextInt(10), 1, "0"));
        fax.append(' ');
        fax.append(StringUtils.leftPad("" + random.nextInt(10000), 4, "0"));
        fax.append(' ');
        fax.append(StringUtils.leftPad("" + random.nextInt(10000), 4, "0"));
        return fax.toString();
    }

    private String randomNum(int count) {
        StringBuilder num = new StringBuilder();
        for (int i = 0; i < count; i++) {
            num.append("" + random.nextInt(10));
        }
        return num.toString();
    }

    private String randomStr(int min, int max) {
        int count = min + random.nextInt(max - min + 1);

        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < count; i++) {
            int intValue = random.nextInt(26 * 2);
            if (intValue < 26) {
                intValue = intValue + 'A';
            } else {
                intValue = intValue - 26 + 'a';
            }
            char c = (char) intValue;
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    private String randomText(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        boolean isLastSpace = true;
        for (int i = 0; i < count; i++) {
            boolean isSpace = random.nextInt(5) == 0;
            if (!isLastSpace && isSpace) {
                stringBuilder.append(' ');
            } else {
                int intValue = random.nextInt(26 * 2);
                if (intValue < 26) {
                    intValue = intValue + 'A';
                } else {
                    intValue = intValue - 26 + 'a';
                }
                char c = (char) intValue;
                stringBuilder.append(c);
            }
            isLastSpace = isSpace;
        }
        return stringBuilder.toString();
    }

    private String randomText(int min, int max) {
        int count = min + random.nextInt(max - min + 1);
        return randomText(count);
    }

    private String randomDecimal(int scale, int precision) {
        double d = random.nextDouble();
        for (int i = 0; i < scale; i++) {
            d = d * 10;
        }
        String format = "#,###,###,##0.";
        for (int i = 0; i < precision; i++) {
            format = format + "0";
        }
        DecimalFormat df2 = new DecimalFormat(format);
        return df2.format(d);
    }

    private String randomDate(String format) {
        long time = new Date().getTime();
        long offset = random.nextLong() % (5 * 24 * 60 * 60 * 1000);
        time = time - offset;
        return new SimpleDateFormat(format).format(new Date(time));
    }

    private float randomOffset(float v) {
        return randomOffset(v, 10f);
    }

    private float randomOffset(float v, float offset) {
        return v - offset + (random.nextFloat() * (offset * 2));
    }

    private float randomRange(float min, float max) {
        return min + (random.nextFloat() * (max - min));
    }

    // endregion

    private void randomValue() {
        float xOffset = randomRange(-30f, 15f);
        X_START = 43 + xOffset;
        X_END = 555f + xOffset;
        X_RIGHT_TITLE = 398f + xOffset;
        X_RIGHT_TITLE_VALUE = 440f + xOffset;
        X_LEFT_TITLE_ADDRESS = 100f + xOffset;

        float yOffset = randomRange(-24f, 30f);
        Y_LOGO = 794f + yOffset;
        Y_LEFT_TITLE_REPORT_TITLE = 506 + yOffset;
        Y_LEFT_TITLE_REPORT_DATE = 475 + yOffset;
        Y_LEFT_TITLE_REPORT_DESC = 449 + yOffset;
        Y_RIGHT_TITLE = 798 + yOffset;
        Y_LEFT_TITLE_ADDRESS = 654 + yOffset;

        X_SIGNATURE = randomOffset(398f, 50f);
        Y_SIGNATURE = randomOffset(20f, 10f);

        if (random.nextBoolean()) {
            BOX_LINE_DASH = LINE_DASH;
            BOX_LINE_DASH_PHASE = LINE_DASH_PHASE;
        }

        File file = new File(FOLDER_SIGNATURES);
        String[] items = file.list();
        int index = random.nextInt(items.length);
        PATH_SIGNATURES = FOLDER_SIGNATURES + "/" + items[index];
    }

    /**
     * @param result
     * @return
     */
    public static String toXml(XmlResult result) {
//        XStream xstream = new XStream();
//        xstream.alias("annotation", XmlResult.class);
//        xstream.alias("owner", Owner.class);
//        xstream.alias("object", Item.class);
//        xstream.alias("size", Size.class);
//        xstream.alias("bndBox", BndBox.class);
//        return xstream.toXML(result);
        return result.toXML(0);
    }

    // endregion
}
