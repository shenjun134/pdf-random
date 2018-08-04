package com.poc.pdf.model;

public class SplitRect {
    private Rectangle a;

    private Rectangle b;

    private Line splitLine;

    private SplitConfig config;

    public Rectangle getA() {
        return a;
    }

    public void setA(Rectangle a) {
        this.a = a;
    }

    public Rectangle getB() {
        return b;
    }

    public void setB(Rectangle b) {
        this.b = b;
    }

    public Line getSplitLine() {
        return splitLine;
    }

    public void setSplitLine(Line splitLine) {
        this.splitLine = splitLine;
    }

    public SplitConfig getConfig() {
        return config;
    }

    public void setConfig(SplitConfig config) {
        this.config = config;
    }

    @Override
    public String toString() {
        return "SplitRect{" +
                "a=" + a +
                ", b=" + b +
                ", splitLine=" + splitLine +
                ", config=" + config +
                '}';
    }
}
