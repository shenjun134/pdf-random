package com.poc.pdf.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

@XStreamAlias("object")
public class BoxVO extends ToString {
    private static final long serialVersionUID = -7399237521587824033L;

    private String name;

    private int difficult;

    @XStreamAlias("bndbox")
    private AreaVO area;


    public static BoxVO newTABLE() {
        return new BoxVO("TABLE");
    }

    public static BoxVO newTH() {
        return new BoxVO("TH");
    }

    public static BoxVO newTD() {
        return new BoxVO("TD");
    }

    public BoxVO() {
    }

    public BoxVO(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public BoxVO setName(String name) {
        this.name = name;
        return this;
    }

    public int getDifficult() {
        return difficult;
    }

    public BoxVO setDifficult(int difficult) {
        this.difficult = difficult;
        return this;
    }

    public AreaVO getArea() {
        return area;
    }

    public BoxVO setArea(AreaVO area) {
        this.area = area;
        return this;
    }
}
