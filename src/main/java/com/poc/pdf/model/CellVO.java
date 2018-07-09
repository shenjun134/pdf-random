package com.poc.pdf.model;

import com.lowagie.text.Element;
import com.poc.pdf.enums.FieldType;

public class CellVO extends ToString {
    private static final long serialVersionUID = 560535522122734910L;

    private Object value;


    private int xAlign = Element.ALIGN_LEFT;

    private int yAlign = Element.ALIGN_BOTTOM;

    private int valueX = Element.ALIGN_LEFT;

    private int valueY = Element.ALIGN_BOTTOM;

    private FieldType valueType = FieldType.STRING;


    private boolean decimalEnable = false;
    private int decimalMaxCount = 0;
    private int decimalMinCount = 0;

    private boolean intEnable = false;
    private int intMaxCount = 0;

    private int intMinCount = 0;

    private int width = 1;

    private String valueDef;


    public CellVO(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    public CellVO setValue(Object value) {
        this.value = value;
        return this;
    }

    public int getxAlign() {
        return xAlign;
    }

    public CellVO setxAlign(int xAlign) {
        this.xAlign = xAlign;
        return this;
    }

    public int getyAlign() {
        return yAlign;
    }

    public CellVO setyAlign(int yAlign) {
        this.yAlign = yAlign;
        return this;
    }

    public int getValueX() {
        return valueX;
    }

    public CellVO setValueX(int valueX) {
        this.valueX = valueX;
        return this;
    }

    public int getValueY() {
        return valueY;
    }

    public CellVO setValueY(int valueY) {
        this.valueY = valueY;
        return this;
    }

    public FieldType getValueType() {
        return valueType;
    }

    public CellVO setValueType(FieldType valueType) {
        this.valueType = valueType;
        return this;
    }

    public String getValueDef() {
        return valueDef;
    }

    public CellVO setValueDef(String valueDef) {
        this.valueDef = valueDef;
        return this;
    }

    public int getWidth() {
        return width;
    }

    public CellVO setWidth(int width) {
        this.width = width;
        return this;
    }

    public boolean isDecimalEnable() {
        return decimalEnable;
    }


    public int getDecimalMaxCount() {
        return decimalMaxCount;
    }


    public boolean isIntEnable() {
        return intEnable;
    }


    public int getIntMaxCount() {
        return intMaxCount;
    }

    public CellVO setInt(int max, int min) {
        this.intMaxCount = max;
        this.intMinCount = min;
        this.intEnable = max >= min;
        return this;
    }

    public CellVO setDecimal(int max, int min) {
        this.decimalMaxCount = max;
        this.decimalMinCount = min;
        this.decimalEnable = max >= min;
        return this;
    }


    public int getDecimalMinCount() {
        return decimalMinCount;
    }

    public int getIntMinCount() {
        return intMinCount;
    }
}
