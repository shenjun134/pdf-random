package com.poc.pdf.model;

public class StructureText extends ToString {
    private String structureXml;

    private StringBuilder text = new StringBuilder();

    private StringBuilder json = new StringBuilder();

    public String getStructureXml() {
        return structureXml;
    }

    public void setStructureXml(String structureXml) {
        this.structureXml = structureXml;
    }

    public StringBuilder getText() {
        return text;
    }

    public void append(Object obj) {
        text.append(obj);
    }


    public StringBuilder getJson() {
        return json;
    }
}
