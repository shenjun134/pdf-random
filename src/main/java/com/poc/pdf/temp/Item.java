package com.poc.pdf.temp;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("object")
class Item {
    private String name;
    private int difficult;
    private BndBox bndBox;

    public Item(String name, int xmin, int ymin, int xmax, int ymax) {
        this.name = name;
        this.difficult = 0;
        this.bndBox = new BndBox(xmin, ymin, xmax, ymax);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getDifficult() {
        return difficult;
    }

    public void setDifficult(int difficult) {
        this.difficult = difficult;
    }

    public BndBox getBndBox() {
        return bndBox;
    }

    public void setBndBox(BndBox bndBox) {
        this.bndBox = bndBox;
    }
}
