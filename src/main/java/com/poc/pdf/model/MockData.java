package com.poc.pdf.model;

import java.util.ArrayList;
import java.util.List;

public class MockData {

    private List<String> srcTxt;

    private List<MockHead> headList = new ArrayList<>();

    private List<List<String>> valueList = new ArrayList<>();

    public List<String> getSrcTxt() {
        return srcTxt;
    }

    public void setSrcTxt(List<String> srcTxt) {
        this.srcTxt = srcTxt;
    }

    public List<MockHead> getHeadList() {
        return headList;
    }

    public void setHeadList(List<MockHead> headList) {
        this.headList = headList;
    }

    public List<List<String>> getValueList() {
        return valueList;
    }

    public void setValueList(List<List<String>> valueList) {
        this.valueList = valueList;
    }
}
