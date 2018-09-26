package com.poc.pdf.model;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import org.apache.commons.lang.math.NumberUtils;

import java.io.*;
import java.util.Properties;

public class TableLayoutConfig extends SignatureConfig {

    public interface Const {
        int mergeProbability = 50;

        int defPadding = 50;

        int defCellPadding = 5;

        int cellWidth = 20;

        int cellHeight = 20;

        int cellRowLimit = 3;
        int cellColumnLimit = 3;

        int maxCategory = 1000;
        int maxTotal = 1000;
        int distance = 1000;

        int signatureWidth = 200;
        int signatureHeight = 80;

        int signatureMax = 10;

        String blankStr = "NA";
    }


    private int borderMinWidth;

    private int borderMaxWidth;

//    private int markBorderColorR = 0;
//
//    private int markBorderColorG = 0;
//
//    private int markBorderColorB = 0;
//
//    private int markRectPaddingTop = 0;
//    private int markRectPaddingBottom = 0;
//    private int markRectPaddingLeft = 0;
//    private int markRectPaddingRight = 0;
//
//    private Color markBorderColor;

    private int cellPaddingTop;

    private int cellPaddingRight;

    private int cellPaddingBottom;

    private int cellPaddingLeft;

    private int cellMinWidth;

    private int cellMinHeight;

    private int cellRealMinWdith;

    private int cellRealMinHeight;

    /**
     * total 100
     */
    private int mergeProbability;

    /**
     * total 100
     */
    private int noiseTableProbability;

    private int noiseTableMaxCount;

    private int noiseBorderColorR = 0;

    private int noiseBorderColorG = 0;

    private int noiseBorderColorB = 0;

    private Color noiseBorderColor;

    private int numberOfCategory;

    private int eachCategoryTotal;

    private int beginAt;

    private int distance;

    private int cellRowLimit;
    private int cellColumnLimit;

    private int tableStartXMaxOff;

    private int tableStartYMaxOff;

    private boolean showRectangleInfo;

    private boolean blankFillDisable;

    private String blankFillString;




    public TableLayoutConfig(String propName) {
//        String propName = "table-layout.properties";
        String userDir = System.getProperty("user.dir");
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
        this.borderMinWidth = NumberUtils.toInt(properties.get("border.min.width").toString(), 1);
        this.borderMaxWidth = NumberUtils.toInt(properties.get("border.max.width").toString(), 1);
        this.borderColorR = NumberUtils.toInt(properties.get("border.color.red").toString(), 0);
        this.borderColorG = NumberUtils.toInt(properties.get("border.color.green").toString(), 0);
        this.borderColorB = NumberUtils.toInt(properties.get("border.color.blue").toString(), 0);
        this.mergeProbability = NumberUtils.toInt(properties.get("probability.merge").toString(), Const.mergeProbability);

        this.noiseTopProbability = NumberUtils.toInt(properties.get("probability.noise.top").toString(), 0);
        this.noiseBottomProbability = NumberUtils.toInt(properties.get("probability.noise.bottom").toString(), 0);
        this.noiseLeftProbability = NumberUtils.toInt(properties.get("probability.noise.left").toString(), 0);
        this.noiseRightProbability = NumberUtils.toInt(properties.get("probability.noise.right").toString(), 0);
        this.noiseTableProbability = NumberUtils.toInt(properties.get("probability.noise.table").toString(), 0);

        this.noiseTableMaxCount = NumberUtils.toInt(properties.get("noise.table.count.max").toString(), 0);
        this.noiseBorderColorR = NumberUtils.toInt(properties.get("noise.border.color.red").toString(), 0);
        this.noiseBorderColorG = NumberUtils.toInt(properties.get("noise.border.color.green").toString(), 0);
        this.noiseBorderColorB = NumberUtils.toInt(properties.get("noise.border.color.blue").toString(), 0);

        this.paddingTop = NumberUtils.toInt(properties.get("padding.top").toString(), Const.defPadding);
        this.paddingRight = NumberUtils.toInt(properties.get("padding.right").toString(), Const.defPadding);
        this.paddingBottom = NumberUtils.toInt(properties.get("padding.bottom").toString(), Const.defPadding);
        this.paddingLeft = NumberUtils.toInt(properties.get("padding.left").toString(), Const.defPadding);


        this.cellPaddingTop = NumberUtils.toInt(properties.get("cell.padding.top").toString(), Const.defCellPadding);
        this.cellPaddingBottom = NumberUtils.toInt(properties.get("cell.padding.bottom").toString(), Const.defCellPadding);
        this.cellPaddingRight = NumberUtils.toInt(properties.get("cell.padding.right").toString(), Const.defCellPadding);
        this.cellPaddingLeft = NumberUtils.toInt(properties.get("cell.padding.left").toString(), Const.defCellPadding);

        this.markRectPaddingTop = NumberUtils.toInt(properties.get("mark.rectangle.padding.top").toString(), 0);
        this.markRectPaddingBottom = NumberUtils.toInt(properties.get("mark.rectangle.padding.bottom").toString(), 0);
        this.markRectPaddingLeft = NumberUtils.toInt(properties.get("mark.rectangle.padding.left").toString(), 0);
        this.markRectPaddingRight = NumberUtils.toInt(properties.get("mark.rectangle.padding.right").toString(), 0);

        if (this.markRectPaddingTop > this.cellPaddingTop) {
            this.markRectPaddingTop = this.cellPaddingTop;
        }
        if (this.markRectPaddingBottom > this.cellPaddingBottom) {
            this.markRectPaddingBottom = this.cellPaddingBottom;
        }
        if (this.markRectPaddingLeft > this.cellPaddingLeft) {
            this.markRectPaddingLeft = this.cellPaddingLeft;
        }
        if (this.markRectPaddingRight > this.cellPaddingRight) {
            this.markRectPaddingRight = this.cellPaddingRight;
        }


        this.cellMinWidth = NumberUtils.toInt(properties.get("cell.min.width").toString(), Const.cellWidth);
        this.cellMinHeight = NumberUtils.toInt(properties.get("cell.min.height").toString(), Const.cellHeight);


        this.numberOfCategory = NumberUtils.toInt(properties.get("category.number").toString(), 1);
        this.numberOfCategory = this.numberOfCategory > Const.maxCategory ? Const.maxCategory : this.numberOfCategory;
        this.eachCategoryTotal = NumberUtils.toInt(properties.get("each.category.total").toString(), 1);
        this.eachCategoryTotal = this.eachCategoryTotal > Const.maxTotal ? Const.maxTotal : this.eachCategoryTotal;
        this.beginAt = NumberUtils.toInt(properties.get("begin.at").toString(), 0);
        this.distance = NumberUtils.toInt(properties.get("distance").toString(), Const.distance);

        this.cellRowLimit = NumberUtils.toInt(properties.get("cell.row.limit").toString(), Const.cellRowLimit);
        this.cellColumnLimit = NumberUtils.toInt(properties.get("cell.column.limit").toString(), Const.cellColumnLimit);

        this.tableStartXMaxOff = NumberUtils.toInt(properties.get("table.start.x.max.offset").toString(), 0);
        this.tableStartYMaxOff = NumberUtils.toInt(properties.get("table.start.y.max.offset").toString(), 0);

        this.markBorderColorR = NumberUtils.toInt(properties.get("mark.border.color.red").toString(), 0);
        this.markBorderColorG = NumberUtils.toInt(properties.get("mark.border.color.green").toString(), 0);
        this.markBorderColorB = NumberUtils.toInt(properties.get("mark.border.color.blue").toString(), 0);


        this.signatureWidth = NumberUtils.toInt(properties.get("signature.react.max.width").toString(), Const.signatureWidth);
        this.signatureHeight = NumberUtils.toInt(properties.get("signature.react.max.height").toString(), Const.signatureHeight);

        this.signatureMax = NumberUtils.toInt(properties.get("signature.max").toString(), 0);
        this.signatureMin = NumberUtils.toInt(properties.get("signature.min").toString(), 0);

        Object signatureDir = properties.get("signature.dir");
        if (signatureDir == null) {
            throw new RuntimeException("signature.dir no set");
        }
        this.signatureDir = signatureDir.toString();

        if (this.signatureMax > Const.signatureMax) {
            this.signatureMax = Const.signatureMax;
        } else if (this.signatureMax < 0) {
            this.signatureMax = 0;
        }
        if (this.signatureMin > this.signatureMax) {
            this.signatureMin = this.signatureMax;
        } else if (this.signatureMin < 0) {
            this.signatureMin = 0;
        }

        this.showRectangleInfo = Boolean.valueOf(properties.get("show.rectangle.info").toString());

        Object blankFillDisable = properties.get("blank.fill.disable");
        Object blankFillString = properties.get("blank.fill.string");
        if (blankFillDisable != null) {
            this.blankFillDisable = Boolean.valueOf(blankFillDisable.toString());
        }
        this.blankFillString = blankFillString != null ? blankFillString.toString() : Const.blankStr;

        this.borderColor = new DeviceRgb(this.borderColorR, this.borderColorG, this.borderColorB);
        this.markBorderColor = new DeviceRgb(this.markBorderColorR, this.markBorderColorG, this.markBorderColorB);
        this.noiseBorderColor = new DeviceRgb(this.noiseBorderColorR, this.noiseBorderColorG, this.noiseBorderColorB);
    }

    public int getBorderMinWidth() {
        return borderMinWidth;
    }

    public void setBorderMinWidth(int borderMinWidth) {
        this.borderMinWidth = borderMinWidth;
    }

    public int getBorderMaxWidth() {
        return borderMaxWidth;
    }

    public void setBorderMaxWidth(int borderMaxWidth) {
        this.borderMaxWidth = borderMaxWidth;
    }


    public int getCellPaddingTop() {
        return cellPaddingTop;
    }

    public void setCellPaddingTop(int cellPaddingTop) {
        this.cellPaddingTop = cellPaddingTop;
    }

    public int getCellPaddingRight() {
        return cellPaddingRight;
    }

    public void setCellPaddingRight(int cellPaddingRight) {
        this.cellPaddingRight = cellPaddingRight;
    }

    public int getCellPaddingBottom() {
        return cellPaddingBottom;
    }

    public void setCellPaddingBottom(int cellPaddingBottom) {
        this.cellPaddingBottom = cellPaddingBottom;
    }

    public int getCellPaddingLeft() {
        return cellPaddingLeft;
    }

    public void setCellPaddingLeft(int cellPaddingLeft) {
        this.cellPaddingLeft = cellPaddingLeft;
    }

    public int getCellMinWidth() {
        return cellMinWidth;
    }

    public void setCellMinWidth(int cellMinWidth) {
        this.cellMinWidth = cellMinWidth;
    }

    public int getCellMinHeight() {
        return cellMinHeight;
    }

    public void setCellMinHeight(int cellMinHeight) {
        this.cellMinHeight = cellMinHeight;
    }

    public int getCellRealMinWdith() {
        return cellRealMinWdith;
    }

    public void setCellRealMinWdith(int cellRealMinWdith) {
        this.cellRealMinWdith = cellRealMinWdith;
    }

    public int getCellRealMinHeight() {
        return cellRealMinHeight;
    }

    public void setCellRealMinHeight(int cellRealMinHeight) {
        this.cellRealMinHeight = cellRealMinHeight;
    }

    public int getMergeProbability() {
        return mergeProbability;
    }

    public void setMergeProbability(int mergeProbability) {
        this.mergeProbability = mergeProbability;
    }

    public int getNoiseTopProbability() {
        return noiseTopProbability;
    }

    public void setNoiseTopProbability(int noiseTopProbability) {
        this.noiseTopProbability = noiseTopProbability;
    }

    public int getNoiseTableMaxCount() {
        return noiseTableMaxCount;
    }

    public void setNoiseTableMaxCount(int noiseTableMaxCount) {
        this.noiseTableMaxCount = noiseTableMaxCount;
    }

    public int getNoiseBorderColorR() {
        return noiseBorderColorR;
    }

    public void setNoiseBorderColorR(int noiseBorderColorR) {
        this.noiseBorderColorR = noiseBorderColorR;
    }

    public int getNoiseBorderColorG() {
        return noiseBorderColorG;
    }

    public void setNoiseBorderColorG(int noiseBorderColorG) {
        this.noiseBorderColorG = noiseBorderColorG;
    }

    public int getNoiseBorderColorB() {
        return noiseBorderColorB;
    }

    public void setNoiseBorderColorB(int noiseBorderColorB) {
        this.noiseBorderColorB = noiseBorderColorB;
    }

    public Color getNoiseBorderColor() {
        return noiseBorderColor;
    }

    public void setNoiseBorderColor(Color noiseBorderColor) {
        this.noiseBorderColor = noiseBorderColor;
    }

    public int getNumberOfCategory() {
        return numberOfCategory;
    }

    public void setNumberOfCategory(int numberOfCategory) {
        this.numberOfCategory = numberOfCategory;
    }

    public int getEachCategoryTotal() {
        return eachCategoryTotal;
    }

    public void setEachCategoryTotal(int eachCategoryTotal) {
        this.eachCategoryTotal = eachCategoryTotal;
    }

    public int getBeginAt() {
        return beginAt;
    }

    public void setBeginAt(int beginAt) {
        this.beginAt = beginAt;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    public int getCellRowLimit() {
        return cellRowLimit;
    }

    public void setCellRowLimit(int cellRowLimit) {
        this.cellRowLimit = cellRowLimit;
    }

    public int getCellColumnLimit() {
        return cellColumnLimit;
    }

    public void setCellColumnLimit(int cellColumnLimit) {
        this.cellColumnLimit = cellColumnLimit;
    }

    public int getTableStartXMaxOff() {
        return tableStartXMaxOff;
    }

    public void setTableStartXMaxOff(int tableStartXMaxOff) {
        this.tableStartXMaxOff = tableStartXMaxOff;
    }

    public int getTableStartYMaxOff() {
        return tableStartYMaxOff;
    }

    public void setTableStartYMaxOff(int tableStartYMaxOff) {
        this.tableStartYMaxOff = tableStartYMaxOff;
    }

    public boolean isShowRectangleInfo() {
        return showRectangleInfo;
    }

    public void setShowRectangleInfo(boolean showRectangleInfo) {
        this.showRectangleInfo = showRectangleInfo;
    }

    public int getNoiseTableProbability() {
        return noiseTableProbability;
    }

    public void setNoiseTableProbability(int noiseTableProbability) {
        this.noiseTableProbability = noiseTableProbability;
    }

    public boolean isBlankFillDisable() {
        return blankFillDisable;
    }

    public void setBlankFillDisable(boolean blankFillDisable) {
        this.blankFillDisable = blankFillDisable;
    }

    public String getBlankFillString() {
        return blankFillString;
    }

    public void setBlankFillString(String blankFillString) {
        this.blankFillString = blankFillString;
    }
}
