package com.poc.pdf.model;

import java.util.ArrayList;
import java.util.List;

public class TableCell {

    private int row;

    private int col;

    /**
     * row-col
     */
    private String id;

    private String name;

    private Point point1;

    private Point point2;

    private Point textP1;

    private Point textP2;

    private boolean mergeBegin;

    private boolean isMerge;

    private List<String> textList = new ArrayList<>();

    private List<TableCell> cellList = new ArrayList<>();

    private Point mergePoint2;

    private int width;

    private int height;

    private int contentWidth;

    private int contentHeight;

    private boolean isTop;

    private boolean isLeft;

    private boolean isRight;

    private boolean isBottom;

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Point getPoint1() {
        return point1;
    }

    public void setPoint1(Point point1) {
        this.point1 = point1;
    }

    public Point getPoint2() {
        return point2;
    }

    public void setPoint2(Point point2) {
        this.point2 = point2;
    }

    public boolean isMergeBegin() {
        return mergeBegin;
    }

    public void setMergeBegin(boolean mergeBegin) {
        this.mergeBegin = mergeBegin;
    }

    public boolean isMerge() {
        return isMerge;
    }

    public void setMerge(boolean merge) {
        isMerge = merge;
    }

    public List<String> getTextList() {
        return textList;
    }

    public void setTextList(List<String> textList) {
        this.textList = textList;
    }

    public List<TableCell> getCellList() {
        return cellList;
    }

    public void setCellList(List<TableCell> cellList) {
        this.cellList = cellList;
    }

    public Point getMergePoint2() {
        return mergePoint2;
    }

    public void setMergePoint2(Point mergePoint2) {
        this.mergePoint2 = mergePoint2;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public int getContentWidth() {
        return contentWidth;
    }

    public void setContentWidth(int contentWidth) {
        this.contentWidth = contentWidth;
    }

    public int getContentHeight() {
        return contentHeight;
    }

    public void setContentHeight(int contentHeight) {
        this.contentHeight = contentHeight;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public boolean isTop() {
        return isTop;
    }

    public void setTop(boolean top) {
        isTop = top;
    }

    public boolean isLeft() {
        return isLeft;
    }

    public void setLeft(boolean left) {
        isLeft = left;
    }

    public boolean isRight() {
        return isRight;
    }

    public void setRight(boolean right) {
        isRight = right;
    }

    public boolean isBottom() {
        return isBottom;
    }

    public void setBottom(boolean bottom) {
        isBottom = bottom;
    }

    public Point getReal(int totalHeight, Point point) {
        int x = point.getX();
        int y = totalHeight - point.getY();
        return new Point(x, y);
    }

    public Point getReal1(int totalHeight) {
        return getReal(totalHeight, point1);
    }

    public Point getReal2(int totalHeight) {
        return getReal(totalHeight, point2);
    }

    public Point getTextP1() {
        return textP1;
    }

    public void setTextP1(Point textP1) {
        this.textP1 = textP1;
    }

    public Point getTextP2() {
        return textP2;
    }

    public void setTextP2(Point textP2) {
        this.textP2 = textP2;
    }

    @Override
    public String toString() {
        return "TableCell{" +
                "row=" + row +
                ", col=" + col +
                ", id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", point1=" + point1 +
                ", point2=" + point2 +
                ", textP1=" + textP1 +
                ", textP2=" + textP2 +
                ", mergeBegin=" + mergeBegin +
                ", isMerge=" + isMerge +
                ", textList=" + textList +
                ", cellList=" + cellList +
                ", mergePoint2=" + mergePoint2 +
                ", width=" + width +
                ", height=" + height +
                ", contentWidth=" + contentWidth +
                ", contentHeight=" + contentHeight +
                ", isTop=" + isTop +
                ", isLeft=" + isLeft +
                ", isRight=" + isRight +
                ", isBottom=" + isBottom +
                '}';
    }
}