package com.poc.pdf.model;

public class Rectangle {

    private Point point1;

    private Point point2;

    private boolean isSplit;

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

    @Override
    public String toString() {
        return "Rectangle{" +
                "point1=" + point1 +
                ", point2=" + point2 +
                ", isSplit=" + isSplit +
                '}';
    }
}
