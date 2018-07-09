package com.poc.pdf.model;

import java.util.ArrayList;
import java.util.List;

public class RowVO extends ToString {
    private static final long serialVersionUID = -1896625160283007411L;

    private List<CellVO> list = new ArrayList<>();

    public void add(CellVO cell){
        this.list.add(cell);
    }

    public List<CellVO> getList() {
        return list;
    }

    public void setList(List<CellVO> list) {
        this.list = list;
    }
}
