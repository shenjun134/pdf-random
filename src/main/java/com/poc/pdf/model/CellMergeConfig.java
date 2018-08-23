package com.poc.pdf.model;

public class CellMergeConfig {

    private int startRow;

    private int endRow;

    private int startCol;

    private int endCol;

    public int getStartRow() {
        return startRow;
    }

    public void setStartRow(int startRow) {
        this.startRow = startRow;
    }

    public int getEndRow() {
        return endRow;
    }

    public void setEndRow(int endRow) {
        this.endRow = endRow;
    }

    public int getStartCol() {
        return startCol;
    }

    public void setStartCol(int startCol) {
        this.startCol = startCol;
    }

    public int getEndCol() {
        return endCol;
    }

    public void setEndCol(int endCol) {
        this.endCol = endCol;
    }

    @Override
    public String toString() {
        return "CellMergeConfig{" +
                "startRow=" + startRow +
                ", endRow=" + endRow +
                ", startCol=" + startCol +
                ", endCol=" + endCol +
                '}';
    }
}
