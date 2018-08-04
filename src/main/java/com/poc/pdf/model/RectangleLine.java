package com.poc.pdf.model;

import java.util.ArrayList;
import java.util.List;

public class RectangleLine {

    private List<Line> lineList = new ArrayList<>();

    private Rectangle rectangle;

    private Line top;
    private Line right;
    private Line bottom;
    private Line left;

    public RectangleLine(Rectangle rectangle) {
        this.rectangle = rectangle;
        Point a = rectangle.getPoint1();
        Point c = rectangle.getPoint2();
        Point b = new Point(c.getX(), a.getY());
        Point d = new Point(a.getX(), c.getY());

        top = new Line(a, b);
        right = new Line(b, c);
        bottom = new Line(c, d);
        left = new Line(d, a);

        lineList.add(top);
        lineList.add(right);
        lineList.add(bottom);
        lineList.add(left);

    }

    public List<Line> getLineList() {
        return lineList;
    }

    public void setLineList(List<Line> lineList) {
        this.lineList = lineList;
    }


    public Rectangle getRectangle() {
        return rectangle;
    }

    public void setRectangle(Rectangle rectangle) {
        this.rectangle = rectangle;
    }

    public Line getTop() {
        return top;
    }

    public void setTop(Line top) {
        this.top = top;
    }

    public Line getRight() {
        return right;
    }

    public void setRight(Line right) {
        this.right = right;
    }

    public Line getBottom() {
        return bottom;
    }

    public void setBottom(Line bottom) {
        this.bottom = bottom;
    }

    public Line getLeft() {
        return left;
    }

    public void setLeft(Line left) {
        this.left = left;
    }

    @Override
    public String toString() {
        return "RectangleLine{" +
                "lineList=" + lineList +
                ", rectangle=" + rectangle +
                ", top=" + top +
                ", right=" + right +
                ", bottom=" + bottom +
                ", left=" + left +
                '}';
    }
}
