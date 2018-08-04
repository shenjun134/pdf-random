package com.poc.pdf.model;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import org.apache.commons.lang.math.NumberUtils;

import java.io.*;
import java.util.Properties;

public class GridLayoutConfig {

    public interface Const {
        int blankProbability = 80;

        int splitProbability = 70;

        int blankRowHeight = 50;

        int rectMinSize = 200;

        int defPadding = 50;
    }

    private int totalWidth;

    private int totalHeight;

    private int paddingTop;

    private int paddingRight;

    private int paddingBottom;

    private int paddingLeft;

    private int borderWidth;

    private int borderColorR = 0;

    private int borderColorG = 0;

    private int borderColorB = 0;

    private Color borderColor;

    private int blankRowHeight;

    private int rectMinSize;

    /**
     * total 100
     */
    private int blankProbability;

    private int splitProbability;

    public GridLayoutConfig() {
        String userDir = System.getProperty("user.dir");
        String propName = "grid-layout.properties";
        Properties properties = new Properties();
        File file = new File(userDir + "/" + propName);
        InputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(file);
            properties.load(fileInputStream);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("No " + propName + " found", e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("No " + propName + " found", e);
        } finally {
            if (fileInputStream != null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        this.totalWidth = NumberUtils.toInt(properties.get("total.width").toString(), 0);
        this.totalHeight = NumberUtils.toInt(properties.get("total.height").toString(), 0);
        this.borderWidth = NumberUtils.toInt(properties.get("border.width").toString(), 0);
        this.borderColorR = NumberUtils.toInt(properties.get("border.color.red").toString(), 0);
        this.borderColorG = NumberUtils.toInt(properties.get("border.color.green").toString(), 0);
        this.borderColorB = NumberUtils.toInt(properties.get("border.color.blue").toString(), 0);
        this.blankRowHeight = NumberUtils.toInt(properties.get("blank.row.height").toString(), Const.blankRowHeight);
        this.rectMinSize = NumberUtils.toInt(properties.get("rectangle.min.size").toString(), Const.rectMinSize);
        this.blankProbability = NumberUtils.toInt(properties.get("probability.blank").toString(), Const.blankProbability);
        this.splitProbability = NumberUtils.toInt(properties.get("probability.split").toString(), Const.splitProbability);

        this.paddingTop = NumberUtils.toInt(properties.get("padding.top").toString(), Const.defPadding);
        this.paddingRight = NumberUtils.toInt(properties.get("padding.right").toString(), Const.defPadding);
        this.paddingBottom = NumberUtils.toInt(properties.get("padding.bottom").toString(), Const.defPadding);
        this.paddingLeft = NumberUtils.toInt(properties.get("padding.left").toString(), Const.defPadding);


        this.borderColor = new DeviceRgb(this.borderColorR, this.borderColorG, this.borderColorB);
    }

    public static void main(String[] args) {
        System.out.println(new GridLayoutConfig());
    }

    public int getTotalWidth() {
        return totalWidth;
    }

    public void setTotalWidth(int totalWidth) {
        this.totalWidth = totalWidth;
    }

    public int getTotalHeight() {
        return totalHeight;
    }

    public void setTotalHeight(int totalHeight) {
        this.totalHeight = totalHeight;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    public int getBorderColorR() {
        return borderColorR;
    }

    public void setBorderColorR(int borderColorR) {
        this.borderColorR = borderColorR;
    }

    public int getBorderColorG() {
        return borderColorG;
    }

    public void setBorderColorG(int borderColorG) {
        this.borderColorG = borderColorG;
    }

    public int getBorderColorB() {
        return borderColorB;
    }

    public void setBorderColorB(int borderColorB) {
        this.borderColorB = borderColorB;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public int getBlankRowHeight() {
        return blankRowHeight;
    }

    public void setBlankRowHeight(int blankRowHeight) {
        this.blankRowHeight = blankRowHeight;
    }

    public int getRectMinSize() {
        return rectMinSize;
    }

    public void setRectMinSize(int rectMinSize) {
        this.rectMinSize = rectMinSize;
    }

    public int getBlankProbability() {
        return blankProbability;
    }

    public void setBlankProbability(int blankProbability) {
        this.blankProbability = blankProbability;
    }

    public int getSplitProbability() {
        return splitProbability;
    }

    public void setSplitProbability(int splitProbability) {
        this.splitProbability = splitProbability;
    }

    public int getPaddingTop() {
        return paddingTop;
    }

    public void setPaddingTop(int paddingTop) {
        this.paddingTop = paddingTop;
    }

    public int getPaddingRight() {
        return paddingRight;
    }

    public void setPaddingRight(int paddingRight) {
        this.paddingRight = paddingRight;
    }

    public int getPaddingBottom() {
        return paddingBottom;
    }

    public void setPaddingBottom(int paddingBottom) {
        this.paddingBottom = paddingBottom;
    }

    public int getPaddingLeft() {
        return paddingLeft;
    }

    public void setPaddingLeft(int paddingLeft) {
        this.paddingLeft = paddingLeft;
    }

    public int getLayoutW(){
        return this.totalWidth - this.paddingLeft - this.paddingRight;
    }

    public int getLayoutH(){
        return this.totalHeight - this.paddingTop - this.paddingBottom;
    }

    @Override
    public String toString() {
        return "GridLayoutConfig{" +
                "totalWidth=" + totalWidth +
                ", totalHeight=" + totalHeight +
                ", paddingTop=" + paddingTop +
                ", paddingRight=" + paddingRight +
                ", paddingBottom=" + paddingBottom +
                ", paddingLeft=" + paddingLeft +
                ", borderWidth=" + borderWidth +
                ", borderColorR=" + borderColorR +
                ", borderColorG=" + borderColorG +
                ", borderColorB=" + borderColorB +
                ", borderColor=" + borderColor +
                ", blankRowHeight=" + blankRowHeight +
                ", rectMinSize=" + rectMinSize +
                ", blankProbability=" + blankProbability +
                ", splitProbability=" + splitProbability +
                '}';
    }
}
