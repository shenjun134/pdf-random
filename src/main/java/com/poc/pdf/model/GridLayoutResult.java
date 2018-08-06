package com.poc.pdf.model;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import java.util.TreeSet;

public class GridLayoutResult {
    private List<Rectangle> rectList = new ArrayList<>();

    private List<Line> lineList = new ArrayList<>();

    public List<Rectangle> getRectList() {
        return rectList;
    }

    public void setRectList(List<Rectangle> rectList) {
        this.rectList = rectList;
    }

    public List<Line> getLineList() {
        return lineList;
    }

    public void setLineList(List<Line> lineList) {
        this.lineList = lineList;
    }

    public void addRect(Rectangle rectangle) {
        this.rectList.add(rectangle);
    }

    public void addLine(Line line) {
        this.lineList.add(line);
    }


    public void printRect() {
        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
        TreeSet<String> treeSet = new TreeSet<>();
        for (Rectangle rectangle : rectList) {
            StringBuilder builder = new StringBuilder();
            builder.append(rectangle.getName()).append(":").append(rectangle.isSplit());
            treeSet.add(builder.toString());
        }
        for (String temp : treeSet) {
            System.out.println(temp);
        }

        System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~");
    }

    @Override
    public String toString() {
        return "GridLayoutResult{" +
                "rectList=" + rectList +
                ", lineList=" + lineList +
                '}';
    }
}
