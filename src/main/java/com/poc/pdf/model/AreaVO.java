package com.poc.pdf.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("bndbox")
public class AreaVO extends ToString {

    private static final long serialVersionUID = 5674683597153092355L;

    private int xmin;
    private int ymin;

    private int xmax;
    private int ymax;

    public int getXmin() {
        return xmin;
    }

    public AreaVO setXmin(float xmin) {
        this.xmin = (int) xmin;
        return this;
    }

    public int getYmin() {
        return ymin;
    }

    public AreaVO setYmin(float ymin) {
        this.ymin = (int) ymin;
        return this;
    }

    public int getXmax() {
        return xmax;
    }

    public AreaVO setXmax(float xmax) {
        this.xmax = (int) xmax;
        return this;
    }

    public int getYmax() {
        return ymax;
    }

    public AreaVO setYmax(float ymax) {
        this.ymax = (int) ymax;
        return this;
    }
}
