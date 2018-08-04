package com.poc.pdf.model;

import java.util.List;

public class Rectangle {

    private Point point1;

    private Point point2;

    private boolean isSplit;

    private List<String> text;

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

    public int width() {
        return point2.getX() - point1.getX();
    }

    public int height() {
        return point2.getY() - point1.getY();
    }

    public boolean isSplit() {
        return isSplit;
    }

    public void setSplit(boolean split) {
        isSplit = split;
    }

    public Point getReal1(GridLayoutConfig config) {
        return getReal(config, point1);
    }

    public Point getReal2(GridLayoutConfig config) {
        return getReal(config, point2);
    }

    public Point getReal(GridLayoutConfig config, Point point) {
        int x = point.getX();
        int y = config.getTotalHeight() - point.getY();
//        int y = point.getY();
        return new Point(x, y);
    }

    public List<String> getText() {
        return text;
    }

    public void setText(List<String> text) {
        this.text = text;
    }

    @Override
    public String toString() {
        return "Rectangle{" +
                "point1=" + point1 +
                ", point2=" + point2 +
                ", isSplit=" + isSplit +
                ", text=" + text +
                '}';
    }
}
