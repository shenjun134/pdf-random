package com.poc.pdf.util;

import com.poc.pdf.model.*;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import java.util.Map;
import java.util.TreeMap;


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

    public static int randomPerSize(int totalSize) {
        int mod = randomMod();
        return totalSize * mod / 10;
    }

    public static int randomRange(int max, int min) {
        double rd = Math.random() * 10000000;
        int temp = (int) rd % max;
        if (temp < min) {
            temp = min;
        }
        if (randomTF(50)) {
            return temp;
        }
        return -temp;
    }


    public static SplitConfig randomBlank(GridLayoutConfig config) {
        SplitConfig splitConfig = new SplitConfig();
        boolean isExisted = randomTF(config.getBlankProbability());
        int layoutW = config.getLayoutW();
        int layoutH = config.getLayoutH();
        boolean isHorizontal = layoutH > layoutW;
        int offSite = isHorizontal ? randomPerSize(layoutH) : randomPerSize(layoutW);
        splitConfig.setExisted(isExisted);
        splitConfig.setHorizontal(isHorizontal);
        splitConfig.setOffSite(offSite);
        splitConfig.setBlanWidth(config.getBlankRowHeight());
        return splitConfig;
    }

    public static SplitConfig getSplitConfig(GridLayoutConfig config, boolean isBlank, Rectangle rectangle) {
        SplitConfig splitConfig = new SplitConfig();
        splitConfig.setId(rectangle.getId());
        splitConfig.setName(rectangle.getName());
        boolean isExisted = isBlank ? randomTF(config.getBlankProbability()) : randomTF(config.getSplitProbability());
        int layoutW = rectangle.width();
        int layoutH = rectangle.height();
        boolean isHorizontal = layoutH > layoutW;
        int offSite = isHorizontal ? randomPerSize(layoutH) : randomPerSize(layoutW);
        splitConfig.setExisted(isExisted);
        splitConfig.setHorizontal(isHorizontal);
        splitConfig.setOffSite(offSite);
        if (isBlank) {
            splitConfig.setBlanWidth(config.getBlankRowHeight());
        }
        return splitConfig;
    }

    public static Rectangle getTotalRect(GridLayoutConfig config, GridLayoutResult result) {
        Rectangle rectangle = new Rectangle();
        int rd = randomRange(config.getParentOffsize(), 0);
        rd = Math.abs(rd);
        result.setPaddingTop(config.getPaddingTop() + rd);
        result.setPaddingBottom(config.getPaddingBottom() + rd);
        result.setPaddingLeft(config.getPaddingLeft() + rd);
        result.setPaddingRight(config.getPaddingRight() + rd);


        Point startP = startP(config, rd);
        Point endP = endP(config, rd);
        rectangle.setPoint1(startP);
        rectangle.setPoint2(endP);
        rectangle.setId(0L);
        rectangle.setName("A");
        return rectangle;
    }

    public static GridLayoutResult splitLoop(GridLayoutConfig config) {
        TreeMap<String, Rectangle> map = new TreeMap<>();
        GridLayoutResult result = new GridLayoutResult();
        Rectangle totalRect = getTotalRect(config, result);
        SplitConfig firstSplitConfig = config.getSplitConfigList().get(0);
        config.getSplitConfigList().add(firstSplitConfig);
        SplitRect firstSplit = splitAB(config, totalRect, true, firstSplitConfig);
        map.put(totalRect.getName(), totalRect);


        if (firstSplit.getSplitLine() != null) {
//            result.addRect(firstSplit.getA());
//            result.addRect(firstSplit.getB());
            map.put(firstSplit.getA().getName(), firstSplit.getA());
            map.put(firstSplit.getB().getName(), firstSplit.getB());

            RectangleLine rectangleALine = new RectangleLine(firstSplit.getA());
            RectangleLine rectangleBLine = new RectangleLine(firstSplit.getB());
            result.getLineList().addAll(rectangleALine.getLineList());
            result.getLineList().addAll(rectangleBLine.getLineList());
            for (int i = 1; i < config.getSplitConfigList().size(); i++) {
                SplitConfig tempConfig = config.getSplitConfigList().get(i);
                Rectangle matchedRect = map.get(tempConfig.getName());
                if (matchedRect == null) {
                    logger.warn("No matched splitConfig-" + tempConfig);
                    continue;
                }
                tempConfig.setShock(0);
                if (config.getShockSize() > 0) {
                    int shock = randomRange(config.getShockSize(), 0);
                    if (shock != 0) {
                        tempConfig.setShock(shock);
                        logger.info("shock-" + shock);
                    }
                }
                SplitRect splitRect = splitAB(config, matchedRect, false, tempConfig);
                if (splitRect.getSplitLine() == null) {
                    logger.info("Oops, no split - " + matchedRect + " | " + splitRect);
                    continue;
                }
                matchedRect.setSplit(true);
                result.addLine(splitRect.getSplitLine());
                if (map.get(splitRect.getA().getName()) == null) {
                    map.put(splitRect.getA().getName(), splitRect.getA());
                } else {
                    logger.warn("Something wrong map-" + map);
                }
                if (map.get(splitRect.getB().getName()) == null) {
                    map.put(splitRect.getB().getName(), splitRect.getB());
                } else {
                    logger.warn("Something wrong map-" + map);
                }
            }
            result.getRectList().addAll(map.values());
        }
        return result;
    }


    public static void doSplit(GridLayoutConfig config, SplitConfig splitConfig, Map<String, Rectangle> map, GridLayoutResult result) {
        Rectangle matchedRect = map.get(splitConfig.getName());
        if (matchedRect == null) {
            logger.warn("No matched splitConfig-" + splitConfig);
            return;
        }
        SplitRect splitRect = splitAB(config, matchedRect, false, splitConfig);
        if (splitRect.getSplitLine() == null) {
            logger.info("Oops, no split - " + matchedRect + " | " + splitRect);
            return;
        }
        matchedRect.setSplit(true);
        result.addRect(splitRect.getA());
        result.addRect(splitRect.getB());
        result.addLine(splitRect.getSplitLine());
        map.put(splitRect.getA().getName(), splitRect.getA());
        map.put(splitRect.getB().getName(), splitRect.getB());
    }


    public static SplitRect splitAB(GridLayoutConfig config, Rectangle origRect, boolean isBlank, SplitConfig splitConfig) {
        SplitRect splitRect = new SplitRect();
//        SplitConfig splitConfig = getSplitConfig(config, isBlank, origRect);
//        config.getSplitConfigList().add(splitConfig);
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
            Ay = startP.getY() + splitConfig.getMergeOffSite();
            Bx = startP.getX();
            By = Ay;
            __Bx = Bx;
            __By = Ay;
            if (isBlank && splitConfig.isExisted()) {
                By = Ay + splitConfig.getBlanWidth();
            }
        } else {
            Ax = startP.getX() + splitConfig.getMergeOffSite();
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

        Long origId = origRect.getId();
        A.setName(origRect.getName() + "-A");
        A.setId(origId + 1L);
        B.setName(origRect.getName() + "-B");
        B.setId(origId + 2L);


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
        SplitConfig splitConfig = getSplitConfig(config, false, origRect);
        config.getSplitConfigList().add(splitConfig);
        SplitRect splitRect = splitAB(config, origRect, false, splitConfig);
        if (splitRect.getSplitLine() == null) {
            logger.info("Oops, no split - " + origRect + " | " + splitRect);
            return;
        }
        origRect.setSplit(true);
        result.addRect(splitRect.getA());
        result.addRect(splitRect.getB());
        result.addLine(splitRect.getSplitLine());
        splitRect(result, config, splitRect.getA());
        splitRect(result, config, splitRect.getB());

    }


    public static Point startP(GridLayoutConfig config, int rd) {
        return new Point(config.getPaddingLeft() + rd, config.getPaddingTop() + rd);
    }


    public static Point endP(GridLayoutConfig config, int rd) {
        return new Point(config.getTotalWidth() - config.getPaddingRight() - rd, config.getTotalHeight() - config.getPaddingBottom() - rd);
    }

    public static boolean isBlankExist(GridLayoutConfig config) {
        return randomTF(config.getBlankProbability());
    }


    public static GridLayoutResult randomGrid(GridLayoutConfig config) {
        GridLayoutResult result = new GridLayoutResult();
        Rectangle totalRect = getTotalRect(config, result);
        SplitConfig splitConfig = getSplitConfig(config, true, totalRect);
        config.getSplitConfigList().add(splitConfig);
        SplitRect firstSplit = splitAB(config, totalRect, true, splitConfig);

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
