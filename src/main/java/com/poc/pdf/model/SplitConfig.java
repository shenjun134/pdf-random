package com.poc.pdf.model;

public class SplitConfig {

    private Long id;

    private String name;

    private boolean isHorizontal;

    private int offSite;

    private int shock;

    private int blanWidth;

    private boolean isExisted;

    public boolean isHorizontal() {
        return isHorizontal;
    }

    public void setHorizontal(boolean horizontal) {
        isHorizontal = horizontal;
    }

    public int getOffSite() {
        return offSite;
    }

    public int getMergeOffSite() {
//        return offSite + shock;
        return offSite;
    }

    public void setOffSite(int offSite) {
        this.offSite = offSite;
    }

    public int getBlanWidth() {
        return blanWidth;
    }

    public void setBlanWidth(int blanWidth) {
        this.blanWidth = blanWidth;
    }

    public boolean isExisted() {
        return isExisted;
    }

    public void setExisted(boolean existed) {
        isExisted = existed;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShock() {
        return shock;
    }

    public void setShock(int shock) {
        this.shock = shock;
    }

    @Override
    public String toString() {
        return "SplitConfig{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", isHorizontal=" + isHorizontal +
                ", offSite=" + offSite +
                ", shock=" + shock +
                ", blanWidth=" + blanWidth +
                ", isExisted=" + isExisted +
                '}';
    }
}
