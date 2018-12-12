package com.poc.pdf.temp;

class Size {
    private float width;
    private float height;
    private int depth;

    public Size(float width, float height) {
        this.width = width;
        this.height = height;
        depth = 0;
    }

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

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }
}
