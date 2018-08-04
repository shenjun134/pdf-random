package com.poc.pdf.model;

public class SplitConfig {

    private boolean isHorizontal;

    private int offSite;

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

    @Override
    public String toString() {
        return "SplitConfig{" +
                "isHorizontal=" + isHorizontal +
                ", offSite=" + offSite +
                ", blanWidth=" + blanWidth +
                ", isExisted=" + isExisted +
                '}';
    }
}
