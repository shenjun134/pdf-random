package com.poc.pdf.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("bndbox")
public class AreaVO extends ToString {

    private static final long serialVersionUID = 5674683597153092355L;

    private float xmin;
    private float ymin;

    private float xmax;
    private float ymax;

    public float getXmin() {
        return xmin;
    }

    public AreaVO setXmin(float xmin) {
        this.xmin = xmin;
        return this;
    }

    public float getYmin() {
        return ymin;
    }

    public AreaVO setYmin(float ymin) {
        this.ymin = ymin;
        return this;
    }

    public float getXmax() {
        return xmax;
    }

    public AreaVO setXmax(float xmax) {
        this.xmax = xmax;
        return this;
    }

    public float getYmax() {
        return ymax;
    }

    public AreaVO setYmax(float ymax) {
        this.ymax = ymax;
        return this;
    }
}
