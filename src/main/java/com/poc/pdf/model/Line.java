package com.poc.pdf.model;

public class Line {

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

    public Point getReal2(GridLayoutConfig config) {
        return getReal(config, point2);
    }

    public Point getReal(GridLayoutConfig config, Point point) {
        int x = point.getX();
        int y = config.getTotalHeight() - point.getY();
//        int y = point.getY();
        return new Point(x, y);
    }

    @Override
    public String toString() {
        return "Line{" +
                "point1=" + point1 +
                ", point2=" + point2 +
                '}';
    }
}
