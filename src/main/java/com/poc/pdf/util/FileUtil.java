package com.poc.pdf.util;

import com.poc.pdf.model.FileInfo;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FileUtil {

    private static final Logger logger = Logger.getLogger(FileUtil.class);

    interface Const {
        //miss tif
        String[] imageType = new String[]{
                "jpg", "png", "bmp", "gif", "JPG", "PNG", "BMP", "GIF"
        };
    }


    public static void write(String data, String path) {
        FileOutputStream fileOutputStream = null;
        try {
            fileOutputStream = new FileOutputStream(new File(path));
            IOUtils.write(data, fileOutputStream);
        } catch (FileNotFoundException e) {
            logger.error("file not found", e);
        } catch (IOException e) {
            logger.error("write data error", e);
        } finally {
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    logger.error("file close error", e);
                }
            }
        }
    }

    public static BufferedImage readImageAndDele(String path) {
        File tempImageFile = new File(path);
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(tempImageFile);
            return ImageIO.read(fis);
        } catch (FileNotFoundException e) {
            logger.error("image not found", e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            logger.error("read image error", e);
            throw new RuntimeException(e);
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    logger.error("image close error", e);
                }
            }
            tempImageFile.delete();
        }
    }


    public static List<FileInfo> loadDirFiles(String dir) {
        File folder = new File(dir);
        if (!folder.exists()) {
            throw new RuntimeException("dir no existed - " + dir);
        }
        File[] files = folder.listFiles();
        if (files == null || files.length == 0) {
            return null;
        }
        List<FileInfo> list = new ArrayList<>(files.length);
        for (File file : files) {
            String name = file.getName();
            int dotIndex = name.indexOf(".");
            String nameWithoutType = StringUtils.substring(name, 0, name.indexOf("."));
            String type = StringUtils.substring(name, dotIndex + 1);
            String parentPath = file.getParent();
            String fullPath = file.getAbsolutePath();
            FileInfo fileInfo = new FileInfo(nameWithoutType, parentPath, type);
            fileInfo.setFullpath(fullPath);
            list.add(fileInfo);
        }
        return list;
    }

    public static List<FileInfo> loadDirImages(String dir) {
        List<FileInfo> list = loadDirFiles(dir);
        if (CollectionUtils.isEmpty(list)) {
            throw new RuntimeException("No image found...");
        }
        List<FileInfo> result = new ArrayList<>();
        for (FileInfo fileInfo : list) {
            if (ArrayUtils.contains(Const.imageType, fileInfo.getType())) {
                result.add(fileInfo);
            }
        }
        if (CollectionUtils.isEmpty(result)) {
            throw new RuntimeException("No image found...");
        }
        return result;
    }


    public static void main(String[] args) {
        String dir = "C:/Users/e521907/home/data/signature-temp/val";
        System.out.println(loadDirImages(dir));
    }
}
