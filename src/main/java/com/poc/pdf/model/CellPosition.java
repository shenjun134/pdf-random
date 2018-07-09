package com.poc.pdf.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;

import java.util.ArrayList;
import java.util.List;

@XStreamAlias("Position")
public class CellPosition {
    @XStreamAlias("list")
    private List<BoxVO> boxVOList = new ArrayList<>();

    public List<BoxVO> getBoxVOList() {
        return boxVOList;
    }

    public void setBoxVOList(List<BoxVO> boxVOList) {
        this.boxVOList = boxVOList;
    }

    public void add(BoxVO boxVO) {
        this.boxVOList.add(boxVO);
    }

    @Override
    public String toString() {
        return "CellPosition{" +
                "boxVOList=" + boxVOList +
                '}';
    }
}
