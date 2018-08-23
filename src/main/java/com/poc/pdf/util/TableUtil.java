package com.poc.pdf.util;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.poc.pdf.model.*;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateFormatUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.security.SecureRandom;
import java.text.MessageFormat;
import java.util.*;

public class TableUtil {

    /**
     * logger
     */
    private static final Logger logger = Logger.getLogger(TableUtil.class);

    interface Const {
        int baseFont = 16;
        int unitCharLength = 10;
        int unitCharHeight = 18;
        int offsiteX = 5;
        int offsiteY = 3;
        float lineHeightPer = 1.2f;

        int multiRetry = 3;

        int defRetry = 3;

        int markRectOffLeft = -2;
        int markRectOffBottom = -2;
        int markRectOffRight = 2;
        int markRectOffTop = 6;

        String enter = "\n";

        String[] dateFormat = new String[]{
                "yyyy/MM/dd", "yyyy/MM/dd hh:mm:ss",
                "dd/MM/yy", "dd/MM/yyyy",
                "MMM/dd/yyyy", "MMM/dd/yy",
                "MMM/dd/yyyy hh:mm:ss", "MMM/dd/yy hh:mm",
        };

        Color[] colors = new Color[]{
                Color.BLACK,
                Color.BLUE,
                Color.CYAN,
                Color.DARK_GRAY,
                Color.GRAY,
                Color.GREEN,
                Color.LIGHT_GRAY,
                Color.MAGENTA,
                Color.ORANGE,
                Color.YELLOW,
                Color.PINK,
                Color.RED,
        };

        String numberChar = "0123456789 .";
        String punctuation = "~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/? ";
        String lowcaseChar = "abcdefghijklmnopqrstuvwxyz ,.";
        String uppcaseChar = "ABCDEFGHIJKLMNOPQRSTUVWXYZ ,.";
        String upperAndLow = uppcaseChar + lowcaseChar;
        String numberAndChar = upperAndLow + numberChar;
        String mixChar = numberAndChar + punctuation;


        TextRandom lowcaseRandom = new TextRandom() {
            @Override
            public boolean probability(int count) {
                return randomTF(80);
            }

            @Override
            public String text(int count) {
                return randomSample(lowcaseChar, count);
            }
        };

        TextRandom numberRandom = new TextRandom() {
            @Override
            public boolean probability(int count) {
                return randomTF(60);
            }

            @Override
            public String text(int count) {
                return randomSample(numberChar, count);
            }
        };

        TextRandom uppcaseRandom = new TextRandom() {
            @Override
            public boolean probability(int count) {
                return randomTF(40);
            }

            @Override
            public String text(int count) {
                float real = count * 0.5f;
                return randomSample(uppcaseChar, (int) real);
            }
        };

        TextRandom upperAndLowRandom = new TextRandom() {
            @Override
            public boolean probability(int count) {
                return randomTF(50);
            }

            @Override
            public String text(int count) {
                float real = count * 0.8f;
                return randomSample(upperAndLow, (int) real);
            }
        };

        TextRandom punctuationRandom = new TextRandom() {
            @Override
            public boolean probability(int count) {
                return randomTF(10);
            }

            @Override
            public String text(int count) {
                float real = count * 0.8f;
                return randomSample(punctuation, (int) real);
            }
        };

        TextRandom numberAndCharRandom = new TextRandom() {
            @Override
            public boolean probability(int count) {
                return randomTF(30);
            }

            @Override
            public String text(int count) {
                float real = count * 0.8f;
                return randomSample(numberAndChar, (int) real);
            }
        };

        TextRandom mixCharRandom = new TextRandom() {
            @Override
            public boolean probability(int count) {
                return randomTF(20);
            }

            @Override
            public String text(int count) {
                float real = count * 0.9f;
                return randomSample(mixChar, (int) real);
            }
        };

        TextRandom dateRandom = new TextRandom() {
            @Override
            public boolean probability(int count) {
                for (String format : dateFormat) {
                    if (format.length() == count) {
                        return true;
                    }
                }
                return false;
            }

            @Override
            public String text(int count) {
                List<String> list = Arrays.asList(dateFormat);
                Collections.shuffle(list);
                String mFormat = list.get(0);
                for (String format : dateFormat) {
                    if (format.length() == count) {
                        mFormat = format;
                        break;
                    }
                }
                return randomDate(mFormat);
            }
        };

        TextRandom defaultRandom = new TextRandom() {
            @Override
            public boolean probability(int count) {
                return true;
            }

            @Override
            public String text(int count) {
                StringBuilder builder = new StringBuilder();
                int part1 = randomRange(count, 1);
                if (part1 == 1) {
                    builder.append(randomSample(uppcaseChar, 1));
                } else {
                    builder.append(randomSample(lowcaseChar, part1));
                }
                builder.append(" ");
                int rest = count - part1 - 1;
                if (rest < 0) {
                    return builder.toString();
                }
                int part2 = randomRange(rest, 1);
                if (part2 == 1) {
                    builder.append(randomSample(uppcaseChar, 1));
                } else {
                    builder.append(randomSample(lowcaseChar, part2));
                }
                builder.append(" ");
                rest = rest - part2 - 1;
                if (rest < 0) {
                    return builder.toString();
                }
                if (rest == 1) {
                    builder.append(randomSample(uppcaseChar, 1));
                } else {
                    builder.append(randomSample(lowcaseChar, rest));
                }
                return builder.toString();
            }
        };

        List<TextRandom> randomList = new ArrayList<TextRandom>() {
            {
                add(lowcaseRandom);
                add(numberRandom);
                add(uppcaseRandom);
                add(upperAndLowRandom);
                add(punctuationRandom);
                add(numberAndCharRandom);
                add(mixCharRandom);
                add(dateRandom);
                add(defaultRandom);
            }
        };

    }

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
     * @param max
     * @param min
     * @return >=min and < max
     */
    public static int randomRange(int max, int min) {
        if (max == 0) {
            return min;
        }
        double rd = Math.random() * 10000000;
        int temp = (int) rd % max;
        if (temp < min) {
            temp = min;
        }
        return temp;
    }

    public static Point randomPoint(TableLayoutConfig config) {
        int left = config.getPaddingLeft();
        int top = config.getPaddingTop();

        int restWidth = config.getTableStartXMaxOff();
        int restHeight = config.getTableStartYMaxOff();

        int rdPer = randomRange(50, 0);

        int x = left + restWidth * rdPer / 100;
        int y = top + restHeight * rdPer / 100;

        return new Point(x, y);
    }

    public static List<Integer> randomInt(int totalInt, int paddingBefore, int paddingAfter, int base, int minInt) {
        List<Integer> list = new ArrayList<>();
        SplitIntConfig config = new SplitIntConfig();
        config.setSrcTotal(totalInt);
        config.setTotalInt(totalInt);
        config.setInitCount(totalInt / base);
        config.setBase(base);
        config.setMinInt(minInt);
        config.setPaddingAfter(paddingAfter);
        config.setPaddingBefore(paddingBefore);

        splitInt(config, list);
        return list;
    }

    /**
     * @param begin
     * @param end
     * @param direction true: begin, false: end
     * @return
     */
    public static int lottery(int begin, int end, boolean direction) {
        int size = end - begin + 1;
        Long mod = 100L;
        Long total = 0L;
        int mult = 2;
        List<LotteryRange> rangeList = new ArrayList<>(size);
        LotteryRange previous = new LotteryRange(0L, 0L);

        for (int i = 0; i < size; i++) {
            Long start = previous.getEnd();
            Long distance = mod * (i + 1) * mult;
            Long finish = start + distance;

            previous.setEnd(finish);
            previous.setBegin(start);

            int real = begin + i;
            if (!direction) {
                real = end - i;
            }
            LotteryRange temp = new LotteryRange(start, finish);
            temp.setRealNum(real);
            rangeList.add(temp);

            total = total + distance;
        }

        Long lottery = ((int) (Math.random() * mod) * System.currentTimeMillis()) % total;
        for (LotteryRange range : rangeList) {
            if (range.getBegin() <= lottery && range.getEnd() >= lottery) {
                return range.getRealNum();
            }
        }
        return direction ? begin : end;
    }

    public static void splitInt(SplitIntConfig config, List<Integer> list) {
        if (config.getTotalInt() < config.getMinInt()) {
            logger.debug(MessageFormat.format("no split anymore -- level-1 -- totalInt: {0}, minInt: {1}", config.getTotalInt(), config.getMinInt()));
            return;
        }
        int count = config.getTotalInt() / config.getBase();
        if (count < 2) {
            logger.debug(MessageFormat.format("no split anymore -- level-2 -- totalInt: {0}, minInt: {1}", config.getTotalInt(), config.getMinInt()));
            return;
        }
        config.setBaseCount(count);
        config.setRetry(Const.multiRetry);
        int rdInt = randomMult(config);

        list.add(rdInt);
        int restInt = config.getTotalInt() - rdInt;
        config.setTotalInt(restInt);

        splitInt(config, list);
    }

    public static int randomMult(SplitIntConfig config) {
        if (config.getRetry() < 0) {
            return config.getMinInt() + config.getPaddingBefore() + config.getPaddingAfter();
        }
//        int integer = randomRange(config.getBaseCount(), 1);

        int integer = lottery(1, config.getBaseCount(), false);
        int decimal = randomRange(9, 1);


//        int begin = config.getMinInt();
//        int end = config.getTotalInt();
//        int width = randomRange(end, begin);

        int width = (int) (1.0f * config.getBase() * (integer * 10 + decimal) / 10);

        if (width > config.getMinInt() && width < config.getTotalInt()) {
            return width;
        }
        config.setRetry(config.getRetry() - 1);
        return randomMult(config);
    }


    public static TableStructure getTableStructure() {
        TableLayoutConfig config = new TableLayoutConfig();
        System.out.println("--------------config---------------");
        System.out.println(config);
        return getFulfillStructure(config);
    }

    /**
     * @param config
     * @return
     */
    public static TableStructure getFulfillStructure(TableLayoutConfig config) {
        TableStructure structure = randomStructure(config);
        int colSize = structure.getCellWidthList().size();
        int rowSize = structure.getCellHeightList().size();
        if (colSize >= config.getCellColumnLimit() && rowSize >= config.getCellRowLimit()) {
            return structure;
        }
        return getFulfillStructure(config);
    }


    public static TableStructure randomStructure(TableLayoutConfig config) {
        TableStructure structure = new TableStructure();
        boolean noiseTop = randomTF(config.getNoiseTopProbability());
        boolean noiseBottom = randomTF(config.getNoiseBottomProbability());
        boolean noiseLeft = randomTF(config.getNoiseLeftProbability());
        boolean noiseRight = randomTF(config.getNoiseRightProbability());


        int borderWidth = randomRange(config.getBorderMaxWidth(), config.getBorderMinWidth());
        int innerBorderWidth = randomRange(config.getBorderMaxWidth(), config.getBorderMinWidth());
        if (borderWidth - innerBorderWidth != 0) {
            logger.warn("border diff - borderWidth:" + borderWidth + ", innerBorderWidth:" + innerBorderWidth);
        }
        structure.setInnerBorderWidth(innerBorderWidth);

        boolean isMerge = randomTF(config.getMergeProbability());
        int fontSize = fontSize();
        float lintHeight = fontSize * Const.lineHeightPer;
        String fontFamily = fontProgram();

        Point startPoint = randomPoint(config);

        int cellMinWidth = config.getCellMinWidth() + config.getCellPaddingLeft() + config.getCellPaddingRight();
        int cellMinHeight = config.getCellMinHeight() + config.getCellPaddingBottom() + config.getCellPaddingTop();

        int widthRange = config.getTotalWidth() - startPoint.getX() - config.getPaddingRight();
        int heightRange = config.getTotalHeight() - startPoint.getY() - config.getPaddingBottom();

        int widthBase = fontSize > config.getCellMinWidth() ? fontSize : config.getCellMinWidth();
        int heightBase = lintHeight > config.getCellMinHeight() ? (int) lintHeight : config.getCellMinHeight();

        float fontMutl = (1.0f * fontSize) / Const.baseFont;

        List<Integer> widthList = randomInt(widthRange, config.getCellPaddingLeft(), config.getCellPaddingRight(), widthBase, (int) (fontMutl * cellMinWidth));
        List<Integer> heightList = randomInt(heightRange, config.getCellPaddingTop(), config.getCellPaddingBottom(), heightBase, (int) (fontMutl * cellMinHeight));


        int tableWidth = sum(widthList);
        int tableHeight = sum(heightList);
        /*******************************setter*********************************/
        structure.setStartPoint(startPoint);

        structure.setBorderWidth(borderWidth);

        structure.setCellMinWidth(cellMinWidth);
        structure.setCellMinHeight(cellMinHeight);

        structure.setFontSize(fontSize);
        structure.setLineHeight(lintHeight);
        structure.setFontFamily(fontFamily);

        structure.setTableWidth(tableWidth);
        structure.setTableHeight(tableHeight);

        structure.setCellLeftPadding(config.getCellPaddingLeft());
        structure.setCellRightPadding(config.getCellPaddingRight());
        structure.setCellTopPadding(config.getCellPaddingTop());
        structure.setCellBottomPadding(config.getCellPaddingBottom());

        structure.setMergeCell(isMerge);

        structure.setCellWidthList(widthList);
        structure.setCellHeightList(heightList);

        structure.setNoiseTop(noiseTop);
        structure.setNoiseBottom(noiseBottom);
        structure.setNoiseLeft(noiseLeft);
        structure.setNoiseRight(noiseRight);

        /**
         * generate cell
         */
        generateCell(structure);

        /**
         * generate line
         */
        generateLine(structure);

        return structure;
    }


    public static void generateCell(TableStructure structure) {
        int colSize = structure.getCellWidthList().size();
        int rowSize = structure.getCellHeightList().size();

        Map<String, TableCell> map = new HashMap<>();
        for (int i = 0; i < rowSize; i++) {
            for (int j = 0; j < colSize; j++) {
                TableCell cell = createCell(structure, i, j);
                map.put(cell.getId(), cell);
            }
        }
        CellMergeConfig cellMergeConfig = randomMerge(structure);
        if (cellMergeConfig == null) {
            structure.setCellList(new ArrayList<>(map.values()));
            return;
        }
        String startMergeCellId = getId(cellMergeConfig.getStartRow(), cellMergeConfig.getStartCol());
        String endMergeCellId = getId(cellMergeConfig.getEndRow(), cellMergeConfig.getEndCol());

        TableCell startCell = map.get(startMergeCellId);
        TableCell endCell = map.get(endMergeCellId);
        if (startCell == null || endCell == null) {
            logger.error(MessageFormat.format("Merge error structure: {0}, cellMergeConfig: {1}, map:{2}", structure, cellMergeConfig, map));
            throw new RuntimeException("Merge error");
        }
        int width = endCell.getPoint2().getX() - startCell.getPoint1().getX();
        int height = endCell.getPoint2().getY() - startCell.getPoint1().getY();
        int innerWidth = width - structure.getCellLeftPadding() - structure.getCellRightPadding();
        int innerHeight = height - structure.getCellTopPadding() - structure.getCellBottomPadding();
        startCell.setMergeBegin(true);
        startCell.setPoint2(endCell.getPoint2());
        startCell.setWidth(width);
        startCell.setHeight(height);
        startCell.setContentWidth(innerWidth);
        startCell.setContentHeight(innerHeight);

        List<TableCell> cellList = new ArrayList<>();
        cellList.add(startCell);
        for (int i = cellMergeConfig.getStartRow(); i <= cellMergeConfig.getEndRow(); i++) {
            for (int j = cellMergeConfig.getStartCol(); j <= cellMergeConfig.getEndCol(); j++) {
                String id = getId(i, j);
                Position pos = getPos(i, j);
                structure.getMergePosition().add(pos);
                TableCell mergedCell = map.get(id);
                mergedCell.setMerge(true);
                startCell.getCellList().add(mergedCell);
            }
        }
        for (TableCell tableCell : map.values()) {
            if (!tableCell.isMerge()) {
                cellList.add(tableCell);
            } else {
                structure.getMergedCell().add(tableCell);
            }
        }
        structure.setCellList(cellList);
    }

    private static Position getPos(int x, int y) {
        return new Position(x, y);
    }

    public static void generateLine(TableStructure structure) {
        Set<Line> lineSet = new HashSet<>();
        for (TableCell tableCell : structure.getCellList()) {
            List<Line> lines = getLines(tableCell, structure);
            lineSet.addAll(lines);
        }
        structure.setLineList(new ArrayList<>(lineSet));
    }

    public static List<Line> getLines(TableCell tableCell, TableStructure structure) {
        Point a = tableCell.getPoint1();
        Point c = tableCell.getPoint2();
        Point b = new Point(c.getX(), a.getY());
        Point d = new Point(a.getX(), c.getY());

        int outBorder = structure.getBorderWidth();
        int innerBorder = structure.getInnerBorderWidth();
        if (structure.isMergeOnBorder()) {
            outBorder = structure.getInnerBorderWidth();
        }

        Line top = new Line(a, b);
        top.setWidth(tableCell.isTop() ? outBorder : innerBorder);
        Line right = new Line(b, c);
        right.setWidth(tableCell.isRight() ? outBorder : innerBorder);
        Line bottom = new Line(c, d);
        bottom.setWidth(tableCell.isBottom() ? outBorder : innerBorder);
        Line left = new Line(d, a);
        left.setWidth(tableCell.isLeft() ? outBorder : innerBorder);

        List<Line> lineList = new ArrayList<>();
        lineList.add(top);
        lineList.add(right);
        lineList.add(bottom);
        lineList.add(left);
        return lineList;
    }

    public static List<Line> createMarkRect(TableStructure structure, TableLayoutConfig config) {
        List<Line> lineList = new ArrayList<>();

        for (TableCell tableCell : structure.getCellList()) {
            lineList.addAll(getInnerLines(tableCell, structure, config));
        }
        return lineList;
    }

    public static List<Line> getInnerLines(TableCell tableCell, TableStructure structure, TableLayoutConfig config) {
        float maxLength = 0;
        int row = tableCell.getTextList().size();

        PdfFont baseFont;
        try {
            baseFont = PdfFontFactory.createFont(structure.getFontFamily());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        for (String text : tableCell.getTextList()) {
            float temp = baseFont.getWidth(text, structure.getFontSize());
            if (temp > maxLength) {
                maxLength = temp;
            }
        }
//        float unit = 1f * structure.getFontSize() / Const.baseFont * Const.unitCharLength;
//        int x2 = x1 + (int) (maxLength * unit) + (maxLength > Const.markRectOffRightLimit ? 0 : Const.markRectOffRight);

        int baseX = tableCell.getPoint1().getX() + structure.getCellLeftPadding() + Const.markRectOffLeft;
        int baseY = tableCell.getPoint1().getY() + structure.getCellTopPadding() + Const.markRectOffTop;
        int baseX2 = baseX + (int) maxLength + Const.markRectOffRight;
//        int baseY2 = baseY + (int) (row * structure.getLineHeight()) + Const.markRectOffBottom;
        int baseY2 = baseY + getTextHeight(row, structure.getLineHeight());

        int x1 = baseX - config.getMarkRectPaddingLeft();
        int y1 = baseY - config.getMarkRectPaddingTop();
        int x2 = baseX2 + config.getMarkRectPaddingRight();
        int y2 = baseY2 + config.getMarkRectPaddingBottom();

        Point point1 = new Point(x1, y1);
        Point point2 = new Point(x2, y2);
        tableCell.setTextP1(point1);
        tableCell.setTextP2(point2);
        List<Line> lineList = getLines(point1, point2);
        return lineList;
    }

    /**
     * @param row
     * @param lineHeight
     * @param maxLength
     * @param x
     * @param y
     * @return
     */
    public static Rectangle createRect(int row, float lineHeight, float maxLength, int x, int y, TableLayoutConfig config) {
        logger.info("createRect - row:" + row + ", lineHeight:" + lineHeight + ", maxLength:" + maxLength + ", x:" + x + ", y:" + y);
        int x1 = x;
        int y1 = y;

        int x2 = x1 + (int) maxLength + 1;
        int y2 = y1 + getTextHeight(row, lineHeight) + 10;
        x2 = x2 > config.getTotalWidth() ? config.getTotalWidth() - 10 : x2;
        y2 = y2 > config.getTotalHeight() ? config.getTotalHeight() - 10 : y2;

        Point point1 = new Point(x1, y1);
        Point point2 = new Point(x2, y2);
        com.poc.pdf.model.Rectangle rectangle = new com.poc.pdf.model.Rectangle();
        rectangle.setPoint1(point1);
        rectangle.setPoint2(point2);
        return rectangle;
    }

    /**
     * @param row
     * @param lineHeight
     * @return
     */
    public static int getTextHeight(int row, float lineHeight) {
        return (int) (row * lineHeight + Const.markRectOffBottom);
    }


    public static List<Line> getLines(Point point1, Point point2) {
        Point a = point1;
        Point c = point2;
        Point b = new Point(c.getX(), a.getY());
        Point d = new Point(a.getX(), c.getY());

        Line top = new Line(a, b);
        Line right = new Line(b, c);
        Line bottom = new Line(c, d);
        Line left = new Line(d, a);

        List<Line> lineList = new ArrayList<>();
        lineList.add(top);
        lineList.add(right);
        lineList.add(bottom);
        lineList.add(left);
        return lineList;
    }

    public static CellMergeConfig randomMerge(TableStructure structure) {
        if (!structure.isMergeCell()) {
            return null;
        }
        CellMergeConfig config = new CellMergeConfig();
        int colSize = structure.getCellWidthList().size();
        int rowSize = structure.getCellHeightList().size();

        int offSite = 1;

        int startCol = randomRange(colSize - offSite, 0);

        int startRow = randomRange(rowSize - offSite, 0);

        int endCol = randomRange(colSize - offSite, startCol);

        int endRow = randomRange(rowSize - offSite, startRow);

        config.setStartCol(startCol);
        config.setStartRow(startRow);
        config.setEndCol(endCol);
        config.setEndRow(endRow);

        if (startCol == endCol && startRow == endRow) {
            return null;
        }

        return config;
    }

    public static TableCell createCell(TableStructure structure, int row, int col) {
        TableCell cell = new TableCell();
        String id = getId(row, col);
        cell.setCol(col);
        cell.setRow(row);
        Point startPoint = structure.getStartPoint();

        int width1 = sum(structure.getCellWidthList(), 0, col);
        int width2 = sum(structure.getCellWidthList(), 0, col + 1);
        int height1 = sum(structure.getCellHeightList(), 0, row);
        int height2 = sum(structure.getCellHeightList(), 0, row + 1);

        int x1 = startPoint.getX() + width1;
        int y1 = startPoint.getY() + height1;

        int x2 = startPoint.getX() + width2;
        int y2 = startPoint.getY() + height2;

        Point point1 = new Point(x1, y1);
        Point point2 = new Point(x2, y2);


        boolean isTop = row == 0;
        boolean isBottom = row == structure.getCellHeightList().size() - 1;

        boolean isLeft = col == 0;
        boolean isRight = col == structure.getCellWidthList().size() - 1;


        int height = height2 - height1;
        int width = width2 - width1;

        int contentHeight = height - structure.getCellTopPadding() - structure.getCellBottomPadding();
        int contentWidth = width - structure.getCellLeftPadding() - structure.getCellRightPadding();

        cell.setId(id);
        cell.setRow(row);
        cell.setCol(col);
        cell.setHeight(height);
        cell.setWidth(width);
        cell.setContentHeight(contentHeight);
        cell.setContentWidth(contentWidth);
        cell.setPoint1(point1);
        cell.setPoint2(point2);

        cell.setTop(isTop);
        cell.setBottom(isBottom);
        cell.setLeft(isLeft);
        cell.setRight(isRight);

        return cell;
    }

    public static String getId(int row, int col) {
        return "" + row + "-" + col;
    }


    public static int sum(List<Integer> list, int start, int end) {
        int sum = 0;
        for (int i = 0; i < end; i++) {
            sum += list.get(i);
        }
        return sum;
    }

    public static int sum(List<Integer> list) {
        int sum = 0;
        for (Integer i : list) {
            sum += i;
        }
        return sum;
    }

    public static int fontSize() {
        double rd = Math.random() * 1000;
        return Const.baseFont + (int) (rd % 20);
    }

    public static int smallFontSize() {
        double rd = Math.random() * 1000;
        return Const.baseFont + (int) (rd % 5);
    }

    public static String fontProgram() {
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

    public static String fontProgramNoneBold() {
        double rd = Math.random() * 1000;
        String[] fontArr = new String[]{
                FontConstants.COURIER,
                FontConstants.COURIER_OBLIQUE,
                FontConstants.HELVETICA,
                FontConstants.HELVETICA_OBLIQUE,
                FontConstants.SYMBOL,
                FontConstants.TIMES_ROMAN,
                FontConstants.TIMES_ITALIC,
                FontConstants.TIMES

        };
        int index = (int) (rd % fontArr.length - 1);
        return fontArr[index];
    }

    public static String fontProgramSoft() {
        double rd = Math.random() * 1000;
        String[] fontArr = new String[]{
                FontConstants.TIMES_ROMAN,
                FontConstants.TIMES_ITALIC,
                FontConstants.TIMES

        };
        int index = (int) (rd % fontArr.length - 1);
        return fontArr[index];
    }

    public static Color randomColor() {
        double rd = Math.random() * 1000;
        int index = (int) (rd % Const.colors.length - 1);
        return Const.colors[index];
    }

    public static List<String> randomString(TableCell rectangle, int fontSize, TableLayoutConfig config) {
        int baseFont = Const.baseFont;
        int unitCharHeight = Const.unitCharHeight;
        float currentUnit = (1.0f * Const.unitCharHeight * fontSize / baseFont);
        int offsite = Const.offsiteY;

        int height = rectangle.getContentHeight();
        int width = rectangle.getContentWidth();
//        int row = height / (unitCharLength * fontSize / baseFont) - offsite;
        int total = (int) (1.0f * height / (currentUnit) - offsite);
        int row = randomRange(total, 1);

        List<String> list = new ArrayList<>(row);
//        list.add(rectangle.getName());
        for (int i = 0; i < row; i++) {
            list.add(randomText(rectangle.getContentWidth(), fontSize));
        }
        if (config.isShowRectangleInfo()) {
            list.add("" + width + "-" + height);
        }
        return list;
    }

    public static List<String> randomString(com.poc.pdf.model.Rectangle rectangle, int fontSize, GridLayoutConfig config) {
        int baseFont = Const.baseFont;
        int unitCharHeight = Const.unitCharHeight;
        float currentUnit = (1.0f * Const.unitCharHeight * fontSize / baseFont);
        int offsite = Const.offsiteY;

        int offsitePadding = 20;
        int height = rectangle.height() - offsitePadding * 2;
        int width = rectangle.width() - offsitePadding * 2;
        int total = (int) (1.0f * height / (currentUnit) - offsite);
        int row = randomRange(total, 1);

        List<String> list = new ArrayList<>(row);
//        list.add(rectangle.getName());
        for (int i = 0; i < row; i++) {
            list.add(randomText(width, fontSize));
        }
        return list;
    }

    /**
     * @param maxCount
     * @param width
     * @param height
     * @param borderMax
     * @return
     */
    public static List<Line> randomLine(int maxCount, int width, int height, int borderMax) {
        int rdCount = randomRange(maxCount, 1);
        List<Line> list = new ArrayList<>();
        for (int i = 0; i < rdCount; i++) {
            Line line = randomLine(width, height, borderMax);
            if (line != null) {
                list.add(line);
            }
        }
        return list;
    }

    public static Line randomLine(int width, int height, int borderMax) {
        Point start = randomPoint(width, height);

        Point end = randomPoint(width, height);
        int i = Const.defRetry;
        while (i > 0 && start.equals(end)) {
            end = randomPoint(width, height);
            i--;
        }
        if (start.equals(end)) {
            return null;
        }
        int border = randomRange(borderMax, 1);

        Line line = new Line(start, end);
        line.setWidth(border);
        return line;
    }

    public static Point randomPoint(int width, int height) {
        int x = randomRange(width, 0);
        int y = randomRange(height, 0);

        return new Point(x, y);
    }

    public static String randomText(int width, int fontSize) {
        int baseFont = Const.baseFont;
        int unitCharLength = Const.unitCharLength;
        int total = width / (unitCharLength * fontSize / baseFont);
        total = total == 0 ? 1 : total;
        int rd = (int) Math.random() * 10000 % total;
        int offsite = Const.offsiteX + rd;
        int count = total - offsite;
        if (count < 2) {
            count = 2;
        }
        Collections.shuffle(Const.randomList);
        for (TextRandom textRandom : Const.randomList) {
            if (textRandom.probability(count)) {
                return textRandom.text(count);
            }
        }
        return Const.defaultRandom.text(count);
    }


    public static String randomSample(String sample, int count) {
        char[] possibleCharacters = (sample).toCharArray();
        String randomStr = RandomStringUtils.random(count, 0, possibleCharacters.length - 1, false, false, possibleCharacters, new SecureRandom());
        return randomStr;
    }


    public static String randomDate(String format) {
        int day = randomDayInt();
        int month = randomMonthInt();
        int year = randomYearInt();
        Calendar calendar = Calendar.getInstance();
        calendar.set(year, month, day);
        return DateFormatUtils.format(calendar.getTime(), format);
    }


    public static int randomDayInt() {
        return randomRange(28, 1);
    }

    /**
     * 01 ~ 12
     *
     * @return
     */
    public static int randomMonthInt() {
        return randomRange(11, 0);
    }

    /**
     * 2017 ~ 2018
     *
     * @return
     */
    public static int randomYearInt() {
        return 2000 + randomRange(18, 17);
    }


    public static TableStructureText structure2Xml(TableStructure structure, String fileName, SignatureConfig config) {
        String enter = Const.enter;
        StringBuilder builder = new StringBuilder();
        builder.append("<annotation>").append(enter);
        builder.append(blank(2)).append("<folder>VOC2007</folder>").append(enter);
        builder.append(blank(2)).append("<filename>").append(fileName).append("</filename>").append(enter);
        builder.append(blank(2)).append("<owner>").append(enter);
        builder.append(blank(4)).append("<name>HengTian</name>").append(enter);
        builder.append(blank(2)).append("</owner>").append(enter);
        builder.append(blank(2)).append("<size>").append(enter);
        builder.append(blank(4)).append("<width>").append(config.getTotalWidth()).append("</width>").append(enter);
        builder.append(blank(4)).append("<height>").append(config.getTotalHeight()).append("</height>").append(enter);
        builder.append(blank(4)).append("<depth>3</depth>").append(enter);
        builder.append(blank(2)).append("</size>").append(enter);
        builder.append(blank(2)).append("<segmented>0</segmented>").append(enter);
        TableStructureText tableStructureText = new TableStructureText();
        for (TableCell cell : structure.getCellList()) {
            appendCell(cell, builder);
            getCellText(cell, tableStructureText);
        }
        builder.append("</annotation>").append(enter);
        tableStructureText.setStructureXml(builder.toString());
        return tableStructureText;
    }

    public static void appendCell(TableCell cell, StringBuilder builder) {
        String enter = Const.enter;
        builder.append(blank(2)).append("<object>").append(enter);
        builder.append(blank(4)).append("<name>cell</name>").append(enter);
        builder.append(blank(4)).append("<difficult>0</difficult>").append(enter);
        builder.append(blank(4)).append("<bndbox>").append(enter);
        builder.append(blank(6)).append("<xmin>" + cell.getTextP1().getX() + "</xmin>").append(enter);
        builder.append(blank(6)).append("<ymin>" + cell.getTextP1().getY() + "</ymin>").append(enter);
        builder.append(blank(6)).append("<xmax>" + cell.getTextP2().getX() + "</xmax>").append(enter);
        builder.append(blank(6)).append("<ymax>" + cell.getTextP2().getY() + "</ymax>").append(enter);
        builder.append(blank(4)).append("</bndbox>").append(enter);
        builder.append(blank(2)).append("</object>").append(enter);
    }

    public static void getCellText(TableCell cell, TableStructureText tableStructureText) {
        String enter = Const.enter;
        String begin = "-------------start " + cell.getTextP1() + "  end " + cell.getTextP2() + " ---------------";


        tableStructureText.append(begin + enter);
        for (String tm : cell.getTextList()) {
            tableStructureText.append(tm + enter);
        }
        tableStructureText.append(enter);

    }

    public static String blank(int size) {
        return StringUtils.leftPad("", size, " ");
    }

    public static void main(String[] args) {
        TableLayoutConfig config = new TableLayoutConfig();
        for (int i = 0; i < 100; i++) {
            TableStructure structure = getFulfillStructure(config);
            structure.printLayout();
//            System.out.println(structure);
        }
    }

}
