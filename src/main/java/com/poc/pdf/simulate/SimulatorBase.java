package com.poc.pdf.simulate;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.poc.pdf.model.*;
import com.poc.pdf.util.FontUtil;
import com.poc.pdf.util.PDFUtil;
import com.poc.pdf.util.RandomUtil;
import com.poc.pdf.util.TableUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.*;

public class SimulatorBase {

    private static final Logger logger = Logger.getLogger(SimulatorBase.class);

    public static final NumberFormat numFmt = new DecimalFormat("###,###,###,###,###.##");

    public static final int ORIGINAL_SCALE = 72;

    public static class BaseConfig {
        String dest;
        String layoutXmlFile;
        String tableXmlFile;
        String tableOutlineXmlFile;
        String signXmlFile;
        String logoXmlFile;
        String textFile;

        List<Rectangle> rectangleList = new ArrayList<>();
        float markBorderWidth = 1f;
        Color markColor = new DeviceRgb(255, 255, 255);
        float layoutHeight = PageSize.A4.getHeight();
        float layoutWidth = PageSize.A4.getWidth();
        int markRectPaddingLeft = 0;
        int markRectPaddingRight = 0;
        int markRectPaddingTop = 0;
        int markRectPaddingBottom = 0;
        boolean markDash = false;
        List<FileInfo> signatureList;
        Properties properties;
    }

    public static Properties load() {
        String propName = "simulate-config.properties";
        return load(propName);
    }
    public static Properties load(String propName) {
        String userDir = System.getProperty("user.dir");
        Properties properties = new Properties();
        File file = new File(userDir + "/" + propName);
        InputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("No " + propName + " found", e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("No " + propName + " found", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return properties;
    }

    protected static FileInfo randomFile(List<FileInfo> list) {
        return list.get(RandomUtil.randomInt(list.size() - 1, 0));
    }

    protected static void drawLine(PdfCanvas canvas, Line line, double totalHeight) {
        int th = (int) totalHeight;
        Point p1 = line.getReal1(th);
        Point p2 = line.getReal2(th);

        canvas.moveTo(p1.getX(), p1.getY()).lineTo(p2.getX(), p2.getY());
    }

    protected static float getPicWidth(BaseConfig config) {
        String value = config.properties.getProperty("picture.width");
        return NumberUtils.toFloat(value, -1);
    }

    protected static float getPicHeight(BaseConfig config) {
        String value = config.properties.getProperty("picture.height");
        return NumberUtils.toFloat(value, -1);
    }

    protected static float getScale(BaseConfig config) {
        float picWidth = getPicWidth(config);
        float picHeight = getPicHeight(config);
        float pdfWidth = config.layoutWidth;
        float pdfHeight = config.layoutHeight;
        String value = config.properties.getProperty("picture.scale.large");
        boolean largeScale = Boolean.valueOf(value);
        float scaleW = picWidth / pdfWidth;
        float scaleH = picHeight / pdfHeight;
        return largeScale ? Math.max(scaleW, scaleH) : Math.min(scaleW, scaleH);
    }


    protected static void insertImage(FileInfo imageInfo, Point startPoint, PdfCanvas canvas, float totalHeight, float imageWidth) throws MalformedURLException {
        ImageData image = ImageDataFactory.create(imageInfo.getFullpath());
        float x = startPoint.getX();
        float y = totalHeight - startPoint.getY();
        boolean asInline = true;
        canvas.addImage(image, x, y, imageWidth, asInline);
    }

    /**
     * @param canvas
     */
    protected static void drawRectange(PdfCanvas canvas, BaseConfig config) {
        if (CollectionUtils.isEmpty(config.rectangleList)) {
            logger.warn("No rectangle to draw");
            return;
        }
        String fontFamily = FontConstants.HELVETICA;
        PdfFont baseFont = null;
        try {
            baseFont = FontUtil.createFont(fontFamily);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //TODO
        int fontSize = 6;
        float leading = 6f;
        for (Rectangle rectangle : config.rectangleList) {
            System.out.println("Rect:" + rectangle.getName());
            RectangleLine rectangleLine = new RectangleLine(rectangle);
            for (Line line : rectangleLine.getLineList()) {
                if (config.markDash) {
                    canvas.setLineWidth(config.markBorderWidth).setStrokeColor(config.markColor).setLineDash(10, 10);
                } else {
                    canvas.setLineWidth(config.markBorderWidth).setStrokeColor(config.markColor);
                }
                drawLine(canvas, line, config.layoutHeight);
                canvas.stroke();

                canvas.beginText()
                        .setFontAndSize(baseFont, fontSize)
                        .setColor(config.markColor, true)
                        .setLeading(leading)
                        .moveText(rectangle.getPoint1().getX() + 2, config.layoutHeight - rectangle.getPoint1().getY());
                String text = rectangle2Str(rectangle);
                canvas.newlineShowText(text);
                canvas.endText();

            }
        }
    }

    protected static Date randomDate() {
        Calendar calendar = Calendar.getInstance();
        int days = RandomUtil.randomInt(365, 0) * (RandomUtil.randomBool() ? -1 : 1);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime();
    }

    protected static String randomDateStr() {
        Calendar calendar = Calendar.getInstance();
        int days = RandomUtil.randomInt(365, 0) * (RandomUtil.randomBool() ? -1 : 1);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        Date dt= calendar.getTime();
        if(RandomUtil.randomBool()){
            return JanusHendersonSimu.Constant.fmtFull.format(dt);
        }
        return JanusHendersonSimu.Constant.fmtshort.format(dt);
    }

    protected static void setOffsize(Point point, int topOffsize, int leftOffsize) {
        point.setY(point.getY() + topOffsize);
        point.setX(point.getX() + leftOffsize);
    }

    protected static String rectangle2Str(Rectangle rectangle) {
        StringBuilder builder = new StringBuilder();
        builder.append("x1:").append(rectangle.getPoint1().getX()).append(",");
        builder.append("y1:").append(rectangle.getPoint1().getY()).append(",");
        builder.append("x2:").append(rectangle.getPoint2().getX()).append(",");
        builder.append("y2:").append(rectangle.getPoint2().getY()).append(",");
        return builder.toString();
    }

    protected static String randomNumber(int intLen, int deciLen, boolean withDollar) {
        String intVal = intLen == 0 ? "0" : RandomStringUtils.random(intLen, false, true);
        int intNum = NumberUtils.toInt(intVal, 0);
        String deciVal = "";
        if (deciLen > 0) {
            deciVal = "." + RandomStringUtils.random(deciLen, false, true);
        }
        String prefix = withDollar ? "$" : "";
        return StringUtils.join(new String[]{prefix, numFmt.format(intNum), deciVal});
    }

    /**
     * @param start
     * @param end
     * @param name
     */
    protected static Rectangle addRectangle(Point start, Point end, String name, BaseConfig config) {
        Rectangle rectangle = new Rectangle();
        Point point1 = new Point(start.getX() - config.markRectPaddingLeft, start.getY() + config.markRectPaddingTop);
        Point point2 = new Point(end.getX() + config.markRectPaddingRight, end.getY() - config.markRectPaddingBottom);
        rectangle.setName(name);
        rectangle.setPoint1(point1);
        rectangle.setPoint2(point2);
        config.rectangleList.add(rectangle);
        return rectangle;
    }

    /**
     * @param start
     * @param end
     * @param name
     * @param config
     */
    protected static void addRectangle4TableCell(Point start, Point end, String name, BaseConfig config) {
        Rectangle rectangle = addRectangle(start, end, name, config);
        rectangle.setName("TABLE:" + rectangle.getName());
    }

    protected static void addRectangle4TableOutline(Point start, Point end, String name, BaseConfig config) {
        Rectangle rectangle = addRectangle(start, end, name, config);
        rectangle.setName("TABLE-OUTLINE:" + rectangle.getName());
    }

    protected static void addRectangle4Sign(Point start, Point end, String name, BaseConfig config) {
        Rectangle rectangle = addRectangle(start, end, name, config);
        rectangle.setName("SIGNATURE:" + rectangle.getName());
    }

    protected static void addRectangle4Logo(Point start, Point end, String name, BaseConfig config) {
        Rectangle rectangle = addRectangle(start, end, name, config);
        rectangle.setName("LOGO:" + rectangle.getName());
    }

    protected static void addRectangle4Layout(Point start, Point end, String name, BaseConfig config) {
        Rectangle rectangle = addRectangle(start, end, name, config);
        rectangle.setName("LAYOUT:" + rectangle.getName());
    }

    protected static void addRectangle4HumanLng(Point start, Point end, String name, BaseConfig config) {
        Rectangle rectangle = addRectangle(start, end, name, config);
        rectangle.setName("HUMAN-LANG:" + rectangle.getName());
    }

    protected static String generateXml(BaseConfig config, String filter, String fileName) {
        float scale = getScale(config);
        String enter = TableUtil.Const.enter;
        StringBuilder builder = new StringBuilder();
        builder.append("<annotation>").append(enter);
        builder.append(blank(2)).append("<folder>VOC2007</folder>").append(enter);
        builder.append(blank(2)).append("<filename>").append(fileName).append("</filename>").append(enter);
        builder.append(blank(2)).append("<owner>").append(enter);
        builder.append(blank(4)).append("<name>HengTian</name>").append(enter);
        builder.append(blank(2)).append("</owner>").append(enter);
        builder.append(blank(2)).append("<size>").append(enter);
        builder.append(blank(4)).append("<width>").append(round(config.layoutWidth * scale)).append("</width>").append(enter);
        builder.append(blank(4)).append("<height>").append(round(config.layoutHeight * scale)).append("</height>").append(enter);
        builder.append(blank(4)).append("<depth>3</depth>").append(enter);
        builder.append(blank(2)).append("</size>").append(enter);
        builder.append(blank(2)).append("<segmented>0</segmented>").append(enter);
        for (Rectangle rectangle : config.rectangleList) {
            if (StringUtils.startsWith(rectangle.getName(), filter)) {
                builder.append(blank(2)).append("<object>").append(enter);
                builder.append(blank(4)).append("<name>cell</name>").append(enter);
                builder.append(blank(4)).append("<difficult>0</difficult>").append(enter);
                builder.append(blank(4)).append("<bndbox>").append(enter);
                builder.append(blank(4)).append("<xmin>").append(round(rectangle.getPoint1().getX() * scale)).append("</xmin>").append(enter);
                builder.append(blank(4)).append("<ymin>").append(round(rectangle.getPoint1().getY() * scale)).append("</ymin>").append(enter);
                builder.append(blank(4)).append("<xmax>").append(round(rectangle.getPoint2().getX() * scale)).append("</xmax>").append(enter);
                builder.append(blank(4)).append("<ymax>").append(round(rectangle.getPoint2().getY() * scale)).append("</ymax>").append(enter);
                builder.append(blank(4)).append("</bndbox>").append(enter);
                builder.append(blank(2)).append("</object>").append(enter);
            } else {
                continue;
            }
        }
        builder.append("</annotation>").append(enter);
        return builder.toString();
    }

    protected static void splitPdf2Jpg(String originalPdf, String output, BaseConfig config) {
        PDFUtil.splitPdf2Jpg(originalPdf, output, ORIGINAL_SCALE * getScale(config));
    }

    private static int round(float value) {
        return Math.round(value);
    }

    public static String blank(int size) {
        return StringUtils.leftPad("", size, " ");
    }
}