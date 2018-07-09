package com.poc.pdf.util;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

public class FileUtil {

    private static final Logger logger = Logger.getLogger(FileUtil.class);

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
}
