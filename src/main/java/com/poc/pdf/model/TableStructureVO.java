package com.poc.pdf.model;

public class TableStructureVO extends ToString {
    private static final long serialVersionUID = -8596281427800136695L;

    private float totalHeight;

    private float totalWidth;

    private float colWidth[];

    private float rowheight[];

    public TableStructureVO(float totalHeight, float totalWidth) {
        this.totalHeight = totalHeight;
        this.totalWidth = totalWidth;
    }

    public float getTotalHeight() {
        return totalHeight;
    }

    public void setTotalHeight(float totalHeight) {
        this.totalHeight = totalHeight;
    }

    public float getTotalWidth() {
        return totalWidth;
    }

    public void setTotalWidth(float totalWidth) {
        this.totalWidth = totalWidth;
    }

    public float[] getColWidth() {
        return colWidth;
    }

    public void setColWidth(float[] colWidth) {
        this.colWidth = colWidth;
    }

    public float[] getRowheight() {
        return rowheight;
    }

    public void setRowheight(float[] rowheight) {
        this.rowheight = rowheight;
    }
}
