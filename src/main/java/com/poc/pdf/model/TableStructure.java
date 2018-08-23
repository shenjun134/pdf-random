package com.poc.pdf.model;

import java.util.ArrayList;
import java.util.List;

public class TableStructure extends ToString {

    private Point startPoint;

    private int cellMinWidth;

    private int cellMinHeight;

    private int cellLeftPadding;

    private int cellRightPadding;

    private int cellTopPadding;

    private int cellBottomPadding;

    private int tableWidth;

    private int tableHeight;

    private List<Integer> cellWidthList = new ArrayList<>();

    private List<Integer> cellHeightList = new ArrayList<>();

    private int fontSize;

    private String fontFamily;

    private float lineHeight;

    private int borderWidth;

    private int innerBorderWidth;

    private boolean noiseTop;

    private boolean noiseBottom;

    private boolean noiseLeft;

    private boolean noiseRight;

    private Rectangle topTextRect;

    private Rectangle bottomTextRect;

    private Rectangle leftTextRect;

    private Rectangle rightTextRect;

    /**
     * merged position
     * like: [0,0] [0,1]
     * <p>
     * like: [4,4] [5,4]
     * <p>
     * like: [3,3] [3,4] [4,3] [4,4]
     */
    private List<Position> mergePosition = new ArrayList<>();

    private boolean mergeCell;

    private List<TableCell> mergedCell = new ArrayList<>();

    private List<TableCell> cellList = new ArrayList<>();

    private List<Line> lineList = new ArrayList<>();

    public Point getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(Point startPoint) {
        this.startPoint = startPoint;
    }

    public int getCellMinWidth() {
        return cellMinWidth;
    }

    public void setCellMinWidth(int cellMinWidth) {
        this.cellMinWidth = cellMinWidth;
    }

    public int getCellMinHeight() {
        return cellMinHeight;
    }

    public void setCellMinHeight(int cellMinHeight) {
        this.cellMinHeight = cellMinHeight;
    }

    public int getCellLeftPadding() {
        return cellLeftPadding;
    }

    public void setCellLeftPadding(int cellLeftPadding) {
        this.cellLeftPadding = cellLeftPadding;
    }

    public int getCellRightPadding() {
        return cellRightPadding;
    }

    public void setCellRightPadding(int cellRightPadding) {
        this.cellRightPadding = cellRightPadding;
    }

    public int getCellTopPadding() {
        return cellTopPadding;
    }

    public void setCellTopPadding(int cellTopPadding) {
        this.cellTopPadding = cellTopPadding;
    }

    public int getCellBottomPadding() {
        return cellBottomPadding;
    }

    public void setCellBottomPadding(int cellBottomPadding) {
        this.cellBottomPadding = cellBottomPadding;
    }

    public int getTableWidth() {
        return tableWidth;
    }

    public void setTableWidth(int tableWidth) {
        this.tableWidth = tableWidth;
    }

    public int getTableHeight() {
        return tableHeight;
    }

    public void setTableHeight(int tableHeight) {
        this.tableHeight = tableHeight;
    }

    public List<Integer> getCellWidthList() {
        return cellWidthList;
    }

    public void setCellWidthList(List<Integer> cellWidthList) {
        this.cellWidthList = cellWidthList;
    }

    public List<Integer> getCellHeightList() {
        return cellHeightList;
    }

    public void setCellHeightList(List<Integer> cellHeightList) {
        this.cellHeightList = cellHeightList;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }

    public String getFontFamily() {
        return fontFamily;
    }

    public void setFontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
    }

    public float getLineHeight() {
        return lineHeight;
    }

    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public List<Position> getMergePosition() {
        return mergePosition;
    }

    public void setMergePosition(List<Position> mergePosition) {
        this.mergePosition = mergePosition;
    }

    public boolean isMergeCell() {
        return mergeCell;
    }

    public void setMergeCell(boolean mergeCell) {
        this.mergeCell = mergeCell;
    }

    public List<TableCell> getCellList() {
        return cellList;
    }

    public void setCellList(List<TableCell> cellList) {
        this.cellList = cellList;
    }

    public List<Line> getLineList() {
        return lineList;
    }

    public void setLineList(List<Line> lineList) {
        this.lineList = lineList;
    }

    public int getInnerBorderWidth() {
        return innerBorderWidth;
    }

    public void setInnerBorderWidth(int innerBorderWidth) {
        this.innerBorderWidth = innerBorderWidth;
    }

    public void printLayout() {
        System.out.println(cellWidthList.size() + " X " + cellHeightList.size() + ", mergePosition" + mergePosition.size() + " width:" + tableWidth + " height:" + tableHeight);
    }

    public boolean isMergeOnBorder() {
        if (!isMergeCell()) {
            return false;
        }
        for (TableCell tableCell : mergedCell) {
            if (tableCell.isTop()) {
                return true;
            }
            if (tableCell.isBottom()) {
                return true;
            }
            if (tableCell.isLeft()) {
                return true;
            }
            if (tableCell.isRight()) {
                return true;
            }
        }
        return false;
    }

    public List<TableCell> getMergedCell() {
        return mergedCell;
    }

    public void setMergedCell(List<TableCell> mergedCell) {
        this.mergedCell = mergedCell;
    }

    public boolean isNoiseTop() {
        return noiseTop;
    }

    public void setNoiseTop(boolean noiseTop) {
        this.noiseTop = noiseTop;
    }

    public boolean isNoiseBottom() {
        return noiseBottom;
    }

    public void setNoiseBottom(boolean noiseBottom) {
        this.noiseBottom = noiseBottom;
    }

    public boolean isNoiseLeft() {
        return noiseLeft;
    }

    public void setNoiseLeft(boolean noiseLeft) {
        this.noiseLeft = noiseLeft;
    }

    public boolean isNoiseRight() {
        return noiseRight;
    }

    public void setNoiseRight(boolean noiseRight) {
        this.noiseRight = noiseRight;
    }

    public Rectangle getTopTextRect() {
        return topTextRect;
    }

    public void setTopTextRect(Rectangle topTextRect) {
        this.topTextRect = topTextRect;
    }

    public Rectangle getBottomTextRect() {
        return bottomTextRect;
    }

    public void setBottomTextRect(Rectangle bottomTextRect) {
        this.bottomTextRect = bottomTextRect;
    }

    public Rectangle getLeftTextRect() {
        return leftTextRect;
    }

    public void setLeftTextRect(Rectangle leftTextRect) {
        this.leftTextRect = leftTextRect;
    }

    public Rectangle getRightTextRect() {
        return rightTextRect;
    }

    public void setRightTextRect(Rectangle rightTextRect) {
        this.rightTextRect = rightTextRect;
    }
}
