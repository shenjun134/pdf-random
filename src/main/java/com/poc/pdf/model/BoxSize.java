package com.poc.pdf.model;


import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("size")
public class BoxSize extends ToString {
    private static final long serialVersionUID = 348369956801601682L;

    private int width;

    private int height;

    private int depth = 3;

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
