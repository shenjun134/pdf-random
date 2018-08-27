package com.poc.pdf.model;

import org.apache.commons.lang.math.NumberUtils;

import java.io.*;
import java.util.Properties;

public class FontConfig extends ToString {

    private String dir;

    private String filter;

    private String additionalFilter;

    private String fontTest = "ABCDEFGHIJKLMNOPQRSTUVWXYZ abcdefghijklmnopqrstuvwxyz 0123456789 . ~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/? ";

    private int fontMaxWidth;

    private int fontSize = 14;

    public FontConfig() {
        String userDir = System.getProperty("user.dir");
        String propName = "font.properties";
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
        Object dir = properties.get("font.dir");
        if (dir == null) {
            throw new RuntimeException("font.dir no set");
        }
        this.dir = dir.toString();
        Object filter = properties.get("default.filter");
        if (filter == null) {
            this.filter = "";
        } else {
            this.filter = filter.toString();
        }

        this.dir = dir.toString();
        Object additionalFilter = properties.get("additional.filter");
        if (additionalFilter == null) {
            this.additionalFilter = "";
        } else {
            this.additionalFilter = additionalFilter.toString();
        }
        this.fontMaxWidth = NumberUtils.toInt(properties.get("font.max.width").toString(), 500);

    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }

    public String getAdditionalFilter() {
        return additionalFilter;
    }

    public void setAdditionalFilter(String additionalFilter) {
        this.additionalFilter = additionalFilter;
    }

    public String getFontTest() {
        return fontTest;
    }

    public void setFontTest(String fontTest) {
        this.fontTest = fontTest;
    }

    public int getFontMaxWidth() {
        return fontMaxWidth;
    }

    public void setFontMaxWidth(int fontMaxWidth) {
        this.fontMaxWidth = fontMaxWidth;
    }

    public int getFontSize() {
        return fontSize;
    }

    public void setFontSize(int fontSize) {
        this.fontSize = fontSize;
    }
}
