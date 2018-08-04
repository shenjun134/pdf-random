package com.poc.pdf.util;

import com.poc.pdf.model.*;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;


public class GridLayoutUtil {

    private static final Logger logger = Logger.getLogger(GridLayoutUtil.class);

    /**
     * random true or false
     *
     * @param probability
     * @return
     */
    public static boolean randomTF(int probability) {
        double rd = Math.random() * 100;
        return rd <= probability;
    }

    /**
     * 3,4,5,6,7
     *
     * @return
     */
    public static int randomMod() {
        String rd = RandomStringUtils.random(1, "34567");
        return NumberUtils.toInt(rd, 3);
    }

    public static int randomSize(int totalSize) {
        int mod = randomMod();
        return totalSize * mod / 10;
    }


    public static SplitConfig randomBlank(GridLayoutConfig config) {
        SplitConfig splitConfig = new SplitConfig();
        boolean isExisted = randomTF(config.getBlankProbability());
        int layoutW = config.getLayoutW();
        int layoutH = config.getLayoutH();
        boolean isHorizontal = layoutH > layoutW;
        int offSite = isHorizontal ? randomSize(layoutH) : randomSize(layoutW);
        splitConfig.setExisted(isExisted);
        splitConfig.setHorizontal(isHorizontal);
        splitConfig.setOffSite(offSite);
        splitConfig.setBlanWidth(config.getBlankRowHeight());
        return splitConfig;
    }

    public static SplitConfig getSplitConfig(GridLayoutConfig config, boolean isBlank, Rectangle rectangle) {
        SplitConfig splitConfig = new SplitConfig();
        boolean isExisted = isBlank ? randomTF(config.getBlankProbability()) : randomTF(config.getSplitProbability());
        int layoutW = rectangle.width();
        int layoutH = rectangle.height();
        boolean isHorizontal = layoutH > layoutW;
        int offSite = isHorizontal ? randomSize(layoutH) : randomSize(layoutW);
        splitConfig.setExisted(isExisted);
        splitConfig.setHorizontal(isHorizontal);
        splitConfig.setOffSite(offSite);
        if (isBlank) {
            splitConfig.setBlanWidth(config.getBlankRowHeight());
        }
        return splitConfig;
    }

    public static Rectangle getTotalRect(GridLayoutConfig config) {
        Rectangle rectangle = new Rectangle();
        Point startP = startP(config);
        Point endP = endP(config);
        rectangle.setPoint1(startP);
        rectangle.setPoint2(endP);
        return rectangle;
    }

    public static SplitRect splitAB(GridLayoutConfig config, Rectangle origRect, boolean isBlank) {
        SplitRect splitRect = new SplitRect();
        SplitConfig splitConfig = getSplitConfig(config, isBlank, origRect);
        splitRect.setConfig(splitConfig);
        if (!isBlank && !splitConfig.isExisted()) {
            return splitRect;
        }
        Point startP = origRect.getPoint1();
        Point endP = origRect.getPoint2();

        int Ax = 0;
        int Ay = 0;
        int Bx = 0;
        int By = 0;
        int __Bx = 0;
        int __By = 0;

        if (splitConfig.isHorizontal()) {
            Ax = endP.getX();
            Ay = startP.getY() + splitConfig.getOffSite();
            Bx = startP.getX();
            By = Ay;
            __Bx = Bx;
            __By = Ay;
            if (isBlank && splitConfig.isExisted()) {
                By = Ay + splitConfig.getBlanWidth();
            }
        } else {
            Ax = startP.getX() + splitConfig.getOffSite();
            Ay = endP.getY();
            Bx = Ax;
            By = startP.getY();
            __Bx = Bx;
            __By = By;
            if (isBlank && splitConfig.isExisted()) {
                Bx = Ax + splitConfig.getBlanWidth();
            }
        }

        Point pointA = new Point(Ax, Ay);
        Point pointB = new Point(Bx, By);

        Point pointB__ = new Point(__Bx, __By);
        Rectangle A = new Rectangle();
        Rectangle B = new Rectangle();
        A.setPoint1(startP);
        A.setPoint2(pointA);
        B.setPoint2(endP);
        if (isBlank) {
            B.setPoint1(pointB);
        } else {
            B.setPoint1(pointB__);
        }

        if (!isRectangleValid(A, config) || !isRectangleValid(B, config)) {
            return splitRect;
        }

        Line splitLine = new Line(pointA, pointB__);

        splitRect.setA(A);
        splitRect.setB(B);
        splitRect.setSplitLine(splitLine);

        return splitRect;
    }

    public static boolean isRectangleValid(Rectangle rectangle, GridLayoutConfig config) {
        return rectangle.width() > config.getRectMinSize() && rectangle.height() > config.getRectMinSize();
    }


    public static void splitRect(GridLayoutResult result, GridLayoutConfig config, Rectangle origRect) {
        int width = origRect.width();
        int height = origRect.height();
        if (width <= config.getRectMinSize() || height <= config.getRectMinSize()) {
            logger.info("Can not be split anymore - " + origRect);
            return;
        }
        SplitRect splitRect = splitAB(config, origRect, false);
        if (splitRect.getSplitLine() == null) {
            logger.info("Oops, no split - " + origRect + " | " + splitRect);
            return;
        }
        origRect.setSplit(true);
        result.addRect(splitRect.getA());
        result.addRect(splitRect.getB());
        splitRect(result, config, splitRect.getA());
        splitRect(result, config, splitRect.getB());
        result.addLine(splitRect.getSplitLine());

    }


    public static Point startP(GridLayoutConfig config) {
        return new Point(config.getPaddingLeft(), config.getPaddingTop());
    }


    public static Point endP(GridLayoutConfig config) {
        return new Point(config.getTotalWidth() - config.getPaddingRight(), config.getTotalHeight() - config.getPaddingBottom());
    }

    public static boolean isBlankExist(GridLayoutConfig config) {
        return randomTF(config.getBlankProbability());
    }


    public static GridLayoutResult randomGrid(GridLayoutConfig config) {
        GridLayoutResult result = new GridLayoutResult();
        Rectangle totalRect = getTotalRect(config);
        SplitRect firstSplit = splitAB(config, totalRect, true);

        if (firstSplit.getSplitLine() != null) {
            splitRect(result, config, firstSplit.getA());
            splitRect(result, config, firstSplit.getB());
            result.addRect(firstSplit.getA());
            result.addRect(firstSplit.getB());

            RectangleLine rectangleALine = new RectangleLine(firstSplit.getA());
            RectangleLine rectangleBLine = new RectangleLine(firstSplit.getB());
            result.getLineList().addAll(rectangleALine.getLineList());
            result.getLineList().addAll(rectangleBLine.getLineList());

        }
        return result;
    }

    public static GridLayoutResult generateGrid() {
        GridLayoutConfig config = new GridLayoutConfig();
        GridLayoutResult result = randomGrid(config);
        return result;
    }


    public static void main(String[] args) {
//        int probability = 80;
//        int falseC = 0;
//        for (int i = 0; i < 100; i++) {
//            boolean result = randomTF(probability);
//            falseC = falseC + (result ? 0 : 1);
//        }
//        System.out.println(falseC);
//
//        for (int i = 0; i < 100; i++) {
//            int result = randomMod();
//            System.out.println(result);
//        }
        GridLayoutResult result = generateGrid();

        for (Rectangle rectangle : result.getRectList()) {
            if (!rectangle.isSplit()) {
                System.out.println(rectangle);
            }
        }

        System.out.println(result.getLineList().size());

    }
}
