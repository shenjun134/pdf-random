package com.poc.pdf.model;

public class SplitIntConfig {

    private int totalInt;

    private int paddingBefore;

    private int paddingAfter;

    private int base;

    private int minInt;

    private int srcTotal;

    private int initCount;

    private int retry;

    private int baseCount;

    public int getTotalInt() {
        return totalInt;
    }

    public void setTotalInt(int totalInt) {
        this.totalInt = totalInt;
    }

    public int getPaddingBefore() {
        return paddingBefore;
    }

    public void setPaddingBefore(int paddingBefore) {
        this.paddingBefore = paddingBefore;
    }

    public int getPaddingAfter() {
        return paddingAfter;
    }

    public void setPaddingAfter(int paddingAfter) {
        this.paddingAfter = paddingAfter;
    }

    public int getBase() {
        return base;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public int getMinInt() {
        return minInt;
    }

    public void setMinInt(int minInt) {
        this.minInt = minInt;
    }

    public int getSrcTotal() {
        return srcTotal;
    }

    public void setSrcTotal(int srcTotal) {
        this.srcTotal = srcTotal;
    }

    public int getRetry() {
        return retry;
    }

    public void setRetry(int retry) {
        this.retry = retry;
    }

    public int getBaseCount() {
        return baseCount;
    }

    public void setBaseCount(int baseCount) {
        this.baseCount = baseCount;
    }

    public int getInitCount() {
        return initCount;
    }

    public void setInitCount(int initCount) {
        this.initCount = initCount;
    }

    @Override
    public String toString() {
        return "SplitIntConfig{" +
                "totalInt=" + totalInt +
                ", paddingBefore=" + paddingBefore +
                ", paddingAfter=" + paddingAfter +
                ", base=" + base +
                ", minInt=" + minInt +
                ", srcTotal=" + srcTotal +
                ", initCount=" + initCount +
                ", defRetry=" + retry +
                ", baseCount=" + baseCount +
                '}';
    }
}
