package com.poc.pdf.simulate;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.poc.pdf.model.*;
import com.poc.pdf.util.RandomUtil;
import com.poc.pdf.util.TableUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.*;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SimulatorBase {

    private static final Logger logger = Logger.getLogger(SimulatorBase.class);

    public static class BaseConfig {
        String dest;
        String layoutXmlFile;
        String tableXmlFile;
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
        String userDir = System.getProperty("user.dir");
        String propName = "simulate-config.properties";
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
            }
        }
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
    protected static void addRectangle4Table(Point start, Point end, String name, BaseConfig config) {
        Rectangle rectangle = addRectangle(start, end, name, config);
        rectangle.setName("TABLE:" + rectangle.getName());
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

    protected static String generateXml(BaseConfig config, String filter, String fileName) {
        String enter = TableUtil.Const.enter;
        StringBuilder builder = new StringBuilder();
        builder.append("<annotation>").append(enter);
        builder.append(blank(2)).append("<folder>VOC2007</folder>").append(enter);
        builder.append(blank(2)).append("<filename>").append(fileName).append("</filename>").append(enter);
        builder.append(blank(2)).append("<owner>").append(enter);
        builder.append(blank(4)).append("<name>HengTian</name>").append(enter);
        builder.append(blank(2)).append("</owner>").append(enter);
        builder.append(blank(2)).append("<size>").append(enter);
        builder.append(blank(4)).append("<width>").append(config.layoutWidth).append("</width>").append(enter);
        builder.append(blank(4)).append("<height>").append(config.layoutWidth).append("</height>").append(enter);
        builder.append(blank(4)).append("<depth>3</depth>").append(enter);
        builder.append(blank(2)).append("</size>").append(enter);
        builder.append(blank(2)).append("<segmented>0</segmented>").append(enter);
        for (Rectangle rectangle : config.rectangleList) {
            if (StringUtils.startsWith(rectangle.getName(), filter)) {
                builder.append(blank(2)).append("<object>").append(enter);
                builder.append(blank(4)).append("<name>cell</name>").append(enter);
                builder.append(blank(4)).append("<difficult>0</difficult>").append(enter);
                builder.append(blank(4)).append("<bndbox>").append(enter);
                builder.append(blank(4)).append("<xmin>").append(rectangle.getPoint1().getX()).append("</xmin>").append(enter);
                builder.append(blank(4)).append("<ymin>").append(rectangle.getPoint1().getY()).append("</ymin>").append(enter);
                builder.append(blank(4)).append("<xmax>").append(rectangle.getPoint1().getX()).append("</xmax>").append(enter);
                builder.append(blank(4)).append("<ymax>").append(rectangle.getPoint1().getX()).append("</ymax>").append(enter);
                builder.append(blank(4)).append("</bndbox>").append(enter);
                builder.append(blank(2)).append("</object>").append(enter);
            } else {
                continue;
            }
        }
        builder.append("</annotation>").append(enter);
        return builder.toString();
    }

    public static String blank(int size) {
        return StringUtils.leftPad("", size, " ");
    }
}
