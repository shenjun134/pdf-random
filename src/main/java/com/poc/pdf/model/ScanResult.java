package com.poc.pdf.model;

import com.thoughtworks.xstream.annotations.XStreamAlias;


@XStreamAlias("annotation")
public class ScanResult extends ToString {
    private static final long serialVersionUID = 2278021270352338645L;


    private String folder;

    private String filename;

    private Owner owner = new Owner("");

    @XStreamAlias("size")
    private BoxSize boxSize = new BoxSize();

    private int segmented;


    public ScanResult(float width, float height) {
        this.boxSize.setWidth(width);
        this.boxSize.setHeight(height);
    }


    public int getSegmented() {
        return segmented;
    }

    public void setSegmented(int segmented) {
        this.segmented = segmented;
    }


    public String getFolder() {
        return folder;
    }

    public void setFolder(String folder) {
        this.folder = folder;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public void setOwner(String name) {
        this.owner.setName(name);
    }

    public BoxSize getBoxSize() {
        return boxSize;
    }

    public void setBoxSize(BoxSize boxSize) {
        this.boxSize = boxSize;
    }
}

