package com.poc.pdf.model;

import com.itextpdf.kernel.color.DeviceRgb;
import org.apache.commons.lang.math.NumberUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

public class GridLayoutConfig extends SignatureConfig {

    public interface Const {
        int blankProbability = 80;

        int splitProbability = 70;

        int blankRowHeight = 50;

        int rectMinSize = 200;

        int defPadding = 50;

        int shock = 0;

        int signatureMax = 5;
    }


    private int blankRowHeight;

    private int rectMinSize;

    /**
     * total 100
     */
    private int blankProbability;

    private int splitProbability;

    private int shockSize;

    private int numberOfCategory;

    private int eachCategoryTotal;

    /**
     * random offsize
     */
    private int parentOffsize;


    private List<SplitConfig> splitConfigList = new ArrayList<>();

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
        this.noiseTopProbability = NumberUtils.toInt(properties.get("probability.noise.top").toString(), 0);
        this.noiseBottomProbability = NumberUtils.toInt(properties.get("probability.noise.bottom").toString(), 0);
        this.noiseLeftProbability = NumberUtils.toInt(properties.get("probability.noise.left").toString(), 0);
        this.noiseRightProbability = NumberUtils.toInt(properties.get("probability.noise.right").toString(), 0);

        this.paddingTop = NumberUtils.toInt(properties.get("padding.top").toString(), Const.defPadding);
        this.paddingRight = NumberUtils.toInt(properties.get("padding.right").toString(), Const.defPadding);
        this.paddingBottom = NumberUtils.toInt(properties.get("padding.bottom").toString(), Const.defPadding);
        this.paddingLeft = NumberUtils.toInt(properties.get("padding.left").toString(), Const.defPadding);
        this.shockSize = NumberUtils.toInt(properties.get("shock.size").toString(), Const.shock);

        this.parentOffsize = NumberUtils.toInt(properties.get("rectangle.parent.max.offsize").toString(), 0);

        this.numberOfCategory = NumberUtils.toInt(properties.get("category.number").toString(), 1);
        this.eachCategoryTotal = NumberUtils.toInt(properties.get("each.category.total").toString(), 1);

        this.borderColor = new DeviceRgb(this.borderColorR, this.borderColorG, this.borderColorB);

        this.signatureWidth = NumberUtils.toInt(properties.get("signature.react.max.width").toString(), TableLayoutConfig.Const.signatureWidth);
        this.signatureHeight = NumberUtils.toInt(properties.get("signature.react.max.height").toString(), TableLayoutConfig.Const.signatureHeight);

        this.signatureMax = NumberUtils.toInt(properties.get("signature.max").toString(), 0);
        this.signatureMin = NumberUtils.toInt(properties.get("signature.min").toString(), 0);


        this.markRectPaddingTop = NumberUtils.toInt(properties.get("mark.rectangle.padding.top").toString(), 0);
        this.markRectPaddingBottom = NumberUtils.toInt(properties.get("mark.rectangle.padding.bottom").toString(), 0);
        this.markRectPaddingLeft = NumberUtils.toInt(properties.get("mark.rectangle.padding.left").toString(), 0);
        this.markRectPaddingRight = NumberUtils.toInt(properties.get("mark.rectangle.padding.right").toString(), 0);

//        if (this.markRectPaddingTop > this.cellPaddingTop) {
//            this.markRectPaddingTop = this.cellPaddingTop;
//        }
//        if (this.markRectPaddingBottom > this.cellPaddingBottom) {
//            this.markRectPaddingBottom = this.cellPaddingBottom;
//        }
//        if (this.markRectPaddingLeft > this.cellPaddingLeft) {
//            this.markRectPaddingLeft = this.cellPaddingLeft;
//        }
//        if (this.markRectPaddingRight > this.cellPaddingRight) {
//            this.markRectPaddingRight = this.cellPaddingRight;
//        }

        Object fixedStructureEnable = properties.getProperty("fixed.structure.enable");
        if (fixedStructureEnable != null) {
            this.fixedStructureEnable = Boolean.valueOf(fixedStructureEnable.toString());
        }
        Object fixedStructureJsonList = properties.getProperty("fixed.structure.json.list");
        if (fixedStructureJsonList != null) {
            this.fixedStructureJsonList = new ArrayList<>(Arrays.asList(fixedStructureJsonList.toString().split(",")));
            if (this.fixedStructureEnable && this.fixedStructureJsonList.size() > 0) {
                this.numberOfCategory = this.fixedStructureJsonList.size();
            }
        }


        this.markBorderColorR = NumberUtils.toInt(properties.get("mark.border.color.red").toString(), 0);
        this.markBorderColorG = NumberUtils.toInt(properties.get("mark.border.color.green").toString(), 0);
        this.markBorderColorB = NumberUtils.toInt(properties.get("mark.border.color.blue").toString(), 0);

        this.markBorderColor = new DeviceRgb(this.markBorderColorR, this.markBorderColorG, this.markBorderColorB);

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

        System.out.println(this);
    }

    public static void main(String[] args) {
        System.out.println(new GridLayoutConfig());
    }


    public int getLayoutW() {
        return this.totalWidth - this.paddingLeft - this.paddingRight;
    }

    public int getLayoutH() {
        return this.totalHeight - this.paddingTop - this.paddingBottom;
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

    public int getShockSize() {
        return shockSize;
    }

    public void setShockSize(int shockSize) {
        this.shockSize = shockSize;
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

    public List<SplitConfig> getSplitConfigList() {
        return splitConfigList;
    }

    public void setSplitConfigList(List<SplitConfig> splitConfigList) {
        this.splitConfigList = splitConfigList;
    }

    public int getParentOffsize() {
        return parentOffsize;
    }

    public void setParentOffsize(int parentOffsize) {
        this.parentOffsize = parentOffsize;
    }
}
