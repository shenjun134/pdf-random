package com.poc.pdf.model;


import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("size")
public class BoxSize extends ToString {
    private static final long serialVersionUID = 348369956801601682L;

    private float width;

    private float height;

    private float depth;

    public float getWidth() {
        return width;
    }

    public void setWidth(float width) {
        this.width = width;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getDepth() {
        return depth;
    }

    public void setDepth(float depth) {
        this.depth = depth;
    }
}
