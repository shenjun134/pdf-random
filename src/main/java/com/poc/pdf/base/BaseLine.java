package com.poc.pdf.base;

import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.poc.pdf.model.*;
import com.poc.pdf.util.TableUtil;
import org.apache.log4j.Logger;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BaseLine {

    private static final Logger logger = Logger.getLogger(BaseLine.class);


    protected static void drawBlankArea(List<com.poc.pdf.model.Rectangle> blankRectList, PdfCanvas canvas, SignatureConfig config) {
        for (com.poc.pdf.model.Rectangle blank : blankRectList) {
            RectangleLine rectangleLine = new RectangleLine(blank);
            for (Line line : rectangleLine.getLineList()) {
                canvas.setLineWidth(line.getWidth()).setStrokeColor(config.getMarkBorderColor()).setLineDash(10, 10);
                drawLine(canvas, line, config);
                canvas.stroke();
            }
        }
    }

    protected static void drawLine(PdfCanvas canvas, Line line, SignatureConfig config) {
        Point p1 = line.getReal1(config.getTotalHeight());
        Point p2 = line.getReal2(config.getTotalHeight());

        canvas.moveTo(p1.getX(), p1.getY()).lineTo(p2.getX(), p2.getY());
    }


    protected static List<Point> randomStartPoint(com.poc.pdf.model.Rectangle rectangle, SignatureConfig config) {
        List<Point> list = new ArrayList<>();
        int width = rectangle.width() - 2 * config.getBorderWidth();
        int height = rectangle.height() - 2 * config.getBorderWidth();

        if (width < config.getSignatureWidth()) {
            return list;
        }
        if (height < config.getSignatureHeight()) {
            return list;
        }

        int imageArea = config.getSignatureWidth() * config.getSignatureHeight();
        int rectArea = width * height;
        int max = 2 * rectArea / imageArea;
        List<com.poc.pdf.model.Rectangle> rdList = new ArrayList<>();
        rdList.add(randomSignArea(rectangle, config));
        for (int i = 0; i < max; i++) {
            insertRect(rectangle, config, rdList);
        }
        for (com.poc.pdf.model.Rectangle rect : rdList) {
            list.add(rect.getPoint1());
        }

        return list;
    }

    protected static void insertRect(com.poc.pdf.model.Rectangle rectangle, SignatureConfig config, List<com.poc.pdf.model.Rectangle> rdList) {
        com.poc.pdf.model.Rectangle temp = randomSignArea(rectangle, config);
        if (!isRectCover(temp, rdList)) {
            rdList.add(temp);
            return;
        }
        int count = 2;
        while (count > 0) {
            temp = randomSignArea(rectangle, config);
            if (!isRectCover(temp, rdList)) {
                rdList.add(temp);
                return;
            }
            count--;
        }
    }

    protected static boolean isRectCover(com.poc.pdf.model.Rectangle temp, List<com.poc.pdf.model.Rectangle> rdList) {
        for (com.poc.pdf.model.Rectangle rectangle : rdList) {
            if (isRectCover2(rectangle, temp)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean isRectCover2(com.poc.pdf.model.Rectangle src, com.poc.pdf.model.Rectangle temp) {
        int w = src.getPoint1().getX() - temp.getPoint2().getX();
        int h = src.getPoint1().getY() - temp.getPoint2().getY();
        int totalW = src.width() + temp.width();
        int totalH = src.height() + temp.height();

        if (Math.abs(w) <= totalW && Math.abs(h) <= totalH) {
            return true;
        }

        return false;
    }

    protected static boolean isRectCover(com.poc.pdf.model.Rectangle src, com.poc.pdf.model.Rectangle temp) {
        Point a = temp.getPoint1();
        Point c = temp.getPoint2();
        Point b = new Point(c.getX(), a.getY());
        Point d = new Point(a.getX(), c.getY());
        List<Point> points = new ArrayList<>();
        points.add(a);
        points.add(b);
        points.add(c);
        points.add(d);
        for (Point p : points) {
            if (isPointCover(src, p)) {
                return true;
            }
        }
        return false;
    }

    protected static boolean isPointCover(com.poc.pdf.model.Rectangle rectangle, Point point) {
        Point a = rectangle.getPoint1();
        Point c = rectangle.getPoint2();
        return point.getX() >= a.getX() && point.getX() <= c.getX() && point.getY() >= a.getY() && point.getY() <= c.getY();
    }


    protected static com.poc.pdf.model.Rectangle randomSignArea(com.poc.pdf.model.Rectangle rectangle, SignatureConfig config) {
        int width = rectangle.width() - 2 * config.getBorderWidth();
        int height = rectangle.height() - 2 * config.getBorderWidth();
        int widthRange = width - config.getSignatureWidth();
        int heightRange = height - config.getSignatureHeight();

        int rdW = TableUtil.randomRange(widthRange, 0);
        int rdH = TableUtil.randomRange(heightRange, 0);

        int x = rectangle.getPoint1().getX() + rdW + config.getBorderWidth();
        int y = rectangle.getPoint1().getY() + rdH + config.getBorderWidth();

        int x2 = x + config.getSignatureWidth();
        int y2 = y + config.getSignatureHeight();
        Point p = new Point(x, y);
        Point p2 = new Point(x2, y2);

        com.poc.pdf.model.Rectangle temp = new com.poc.pdf.model.Rectangle();
        temp.setPoint1(p);
        temp.setPoint2(p2);
        return temp;
    }


    protected static void insertImage(FileInfo imageInfo, Point startPoint, PdfCanvas canvas, SignatureConfig config) throws MalformedURLException {
        ImageData image = ImageDataFactory.create(imageInfo.getFullpath());
        int border = config.getBorderWidth();
        float x = startPoint.getX() + border;
        float y = config.getTotalHeight() - startPoint.getY() - config.getSignatureHeight() - border;
        float width = config.getSignatureWidth();
        boolean asInline = true;

//        canvas.addImage(image, 1217, config.getTotalHeight() - 45 - config.getSignatureHeight(), width, asInline);
        canvas.addImage(image, x, y, width, asInline);
    }


    protected static void drawImage(List<Point> pointList, List<FileInfo> signatureList, PdfCanvas canvas, SignatureConfig config) throws MalformedURLException {
        List<FileInfo> selected = getSignature(signatureList, config);
        int size = selected.size() > pointList.size() ? pointList.size() : selected.size();

        logger.info("drawImage -- " + pointList);
        for (int i = 0; i < size; i++) {
            Point point = pointList.get(i);
            FileInfo imageInfo = selected.get(i);
            insertImage(imageInfo, point, canvas, config);
        }
    }


    protected static void drawSelectedImage(List<Point> pointList, List<FileInfo> signatureList, PdfCanvas canvas, SignatureConfig config) throws MalformedURLException {
        int size = pointList.size();
        logger.info("drawImage -- " + pointList);
        for (int i = 0; i < size; i++) {
            Point point = pointList.get(i);
            FileInfo imageInfo = randomSignature(signatureList);
            insertImage(imageInfo, point, canvas, config);
        }
    }

    /**
     * @param signatureList
     * @param config
     * @return
     */
    protected static List<FileInfo> getSignature(List<FileInfo> signatureList, SignatureConfig config) {
        int count = TableUtil.randomRange(config.getSignatureMax(), config.getSignatureMin());
        List<FileInfo> existed = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            randomDiffSignature(signatureList, existed);
        }
        return existed;
    }


    protected static void randomDiffSignature(List<FileInfo> signatureList, List<FileInfo> existed) {
        FileInfo temp = randomSignature(signatureList);
        if (existed.size() == 0) {
            existed.add(temp);
            return;
        }
        int count = 3;
        while (count > 0) {
            temp = randomSignature(signatureList);
            if (!existed.contains(temp)) {
                existed.add(temp);
                break;
            }
            count--;
        }
    }

    protected static FileInfo randomSignature(List<FileInfo> signatureList) {
        List<FileInfo> temp = new ArrayList<>(signatureList);
        Collections.shuffle(temp);
        int rd = TableUtil.randomRange(signatureList.size() - 1, 0);
        return temp.get(rd);
    }
}
