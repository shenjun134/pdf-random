package com.poc.pdf.temp;

import com.thoughtworks.xstream.annotations.XStreamImplicit;
import java.util.ArrayList;
import java.util.List;

class XmlResult implements XmlModel {
    private String folder;
    private String filename;
    private Owner owner;
    private Size size;
    private int segmented;

    @XStreamImplicit
    List<Item> objects;

    public XmlResult(String folder, String filename, float width, float height) {
        this.folder = folder;
        this.filename = filename;
        this.size = new Size(width, height);
        this.owner = new Owner("HengTian");
        this.segmented = 0;
        this.objects = new ArrayList<>();
    }

    public void addItem(String name, double xmin, double ymin, double xmax, double ymax) {
        objects.add(new Item(name, (int)xmin, (int)ymin, (int)xmax, (int)ymax));
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

    public Size getSize() {
        return size;
    }

    public void setSize(Size size) {
        this.size = size;
    }

    public int getSegmented() {
        return segmented;
    }

    public void setSegmented(int segmented) {
        this.segmented = segmented;
    }

    public List<Item> getObjects() {
        return objects;
    }

    public void setObjects(List<Item> objects) {
        this.objects = objects;
    }

    public String toXML(int indent) {
        StringBuilder builder = new StringBuilder();
        builder.append("<annotation>\r\n");
        builder.append("  <folder>").append(folder).append("</folder>\r\n");
        builder.append("  <filename>").append(folder).append("</filename>\r\n");
        builder.append("  <owner>\r\n");
        builder.append("    <name>").append(owner.getName()).append("</name>\r\n");
        builder.append("  </owner>\r\n");
        builder.append("  <size>\r\n");
        builder.append("    <width>").append(size.getWidth()).append("</width>\r\n");
        builder.append("    <height>").append(size.getHeight()).append("</height>\r\n");
        builder.append("    <depth>").append(size.getDepth()).append("</depth>\r\n");
        builder.append("  </size>\r\n");
        builder.append("  <segmented>").append(segmented).append("</segmented>\r\n");
        for(Item item : objects) {
            builder.append("  <object>\r\n");
            builder.append("    <name>").append(item.getName()).append("</name>\r\n");
            builder.append("    <difficult>").append(item.getDifficult()).append("</difficult>\r\n");
            builder.append("    <bndbox>\r\n");
            BndBox box = item.getBndBox();
            builder.append("    <xmin>").append(box.getXmin()).append("</xmin>\r\n");
            builder.append("    <ymin>").append(box.getYmin()).append("</ymin>\r\n");
            builder.append("    <xmax>").append(box.getXmax()).append("</xmax>\r\n");
            builder.append("    <ymax>").append(box.getYmax()).append("</ymax>\r\n");
            builder.append("    </bndbox>\r\n");
            builder.append("  </object>\r\n");
        }
        builder.append("</annotation>");
        return builder.toString();
    }
}


