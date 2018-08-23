package com.poc.pdf.model;

public class Line {

    private int width = 2;

    private Point point1;

    private Point point2;

    public Line(Point point1, Point point2) {
        this.point1 = point1;
        this.point2 = point2;
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


    public Point getReal1(GridLayoutConfig config) {
        return getReal(config, point1);
    }

    public Point getReal1(int totalHeight) {
        return getReal(totalHeight, point1);
    }

    public Point getReal2(GridLayoutConfig config) {
        return getReal(config, point2);
    }

    public Point getReal2(int totalHeight) {
        return getReal(totalHeight, point2);
    }

    public Point getReal(GridLayoutConfig config, Point point) {
        int x = point.getX();
        int y = config.getTotalHeight() - point.getY();
//        int y = point.getY();
        return new Point(x, y);
    }

    public Point getReal(int totalHeight, Point point) {
        int x = point.getX();
        int y = totalHeight - point.getY();
        return new Point(x, y);
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Line line = (Line) o;

        if (width != line.width) return false;
        if (point1 != null ? !point1.equals(line.point1) : line.point1 != null) return false;
        return point2 != null ? point2.equals(line.point2) : line.point2 == null;
    }

    @Override
    public int hashCode() {
        int result = width;
        result = 31 * result + (point1 != null ? point1.hashCode() : 0);
        result = 31 * result + (point2 != null ? point2.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Line{" +
                "width=" + width +
                ", point1=" + point1 +
                ", point2=" + point2 +
                '}';
    }
}
