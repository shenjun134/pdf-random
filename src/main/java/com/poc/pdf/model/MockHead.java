package com.poc.pdf.model;

public class MockHead {
    private String name;
    private int maxLenght;
    private int index;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getMaxLenght() {
        return maxLenght;
    }

    public void setMaxLenght(int maxLenght) {
        this.maxLenght = maxLenght;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    @Override
    public String toString() {
        return "MockHead{" +
                "name='" + name + '\'' +
                ", maxLenght=" + maxLenght +
                ", index=" + index +
                '}';
    }
}
