package com.poc.pdf.model;

public class LotteryRange {
    private Long begin;

    private Long end;

    private int realNum;

    public LotteryRange(Long begin, Long end) {
        this.begin = begin;
        this.end = end;
    }

    public Long getBegin() {
        return begin;
    }

    public void setBegin(Long begin) {
        this.begin = begin;
    }

    public Long getEnd() {
        return end;
    }

    public void setEnd(Long end) {
        this.end = end;
    }

    public int getRealNum() {
        return realNum;
    }

    public void setRealNum(int realNum) {
        this.realNum = realNum;
    }

    @Override
    public String toString() {
        return "LotteryRange{" +
                "begin=" + begin +
                ", end=" + end +
                ", realNum=" + realNum +
                '}';
    }
}
