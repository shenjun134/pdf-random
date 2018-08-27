package com.poc.pdf.util;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.poc.pdf.model.FontConfig;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FontUtil {

    private static final Logger logger = Logger.getLogger(FontUtil.class);
    public static final FontConfig config = new FontConfig();
    public static List<String> fontStrList;
    public static List<String> defFontStrList;
    public static Map<String, PdfFont> fontMap = new HashMap<>();

    public static final String defFont = FontConstants.COURIER;

    static {
        String dir = config.getDir();
        File fontDir = new File(dir);
        File[] fontFiles = fontDir.listFiles();
//        FontProgramFactory.registerFontDirectory(fontDir.getAbsolutePath());
        String encoding = "utf-8";
        for (File file : fontFiles) {
            if (file.isDirectory()) {
                continue;
            }
            byte[] bytes = FileUtil.readBytes(file.getAbsolutePath());
            String fileName = file.getName();
            String fontName = StringUtils.substring(fileName, 0, fileName.indexOf("."));
            fontName = StringUtils.replace(fontName, " ", "");
            try {
                PdfFont pdfFont = PdfFontFactory.createFont(bytes, encoding);
                fontMap.put(fontName, pdfFont);
            } catch (Exception e) {
                throw new RuntimeException("load font file error " + fileName, e);
            }
        }
        fontStrList = new ArrayList<>(fontMap.keySet());
        String[] fontArr = new String[]{
                FontConstants.COURIER,
                FontConstants.COURIER_BOLD,
                FontConstants.COURIER_OBLIQUE,
                FontConstants.COURIER_BOLDOBLIQUE,
                FontConstants.HELVETICA,
                FontConstants.HELVETICA_BOLD,
                FontConstants.HELVETICA_OBLIQUE,
                FontConstants.HELVETICA_BOLDOBLIQUE,
                FontConstants.SYMBOL,
                FontConstants.TIMES_ROMAN,
                FontConstants.TIMES_BOLD,
                FontConstants.TIMES_ITALIC,
                FontConstants.TIMES_BOLDITALIC,
                FontConstants.ZAPFDINGBATS,
        };
        defFontStrList = Arrays.asList(fontArr);
    }

    public static String fontProgram() {
        String font;
        if (TableUtil.randomTF(50)) {
            font = defFontProgram();
            logger.debug("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^" + font + "   " + config);
            if (StringUtils.contains(config.getFilter().toLowerCase(), font.toLowerCase())) {
                return additionalFontF();
            }
            return font;
        }
        return additionalFontF();
    }

    public static String additionalFontF() {
        List<String> tempList = new ArrayList<>(fontStrList);
        if (tempList.size() == 0) {
            return defFont;
        }
        Collections.shuffle(tempList);
        String fontStr = tempList.get(0);
        float width = 0;

        try {
            width = createFont(fontStr).getWidth(config.getFontTest(), config.getFontSize());
            if (config.getFontMaxWidth() < width || StringUtils.contains(config.getAdditionalFilter().toLowerCase(), fontStr.toLowerCase())) {
                fontStr = defFont;
            }
            return fontStr;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            logger.debug("additionalFontF ---" + width + " fontStr:" + fontStr);
        }
//        return defFont;
    }

    public static List<String> allFont() {
        List<String> tempList = new ArrayList<>(fontStrList);
        tempList.addAll(defFontStrList);
        Collections.sort(tempList);
        return tempList;
    }

    public static String defFontProgram() {
        List<String> tempList = new ArrayList<>(defFontStrList);
        Collections.shuffle(tempList);
        return tempList.get(0);
    }

    public static String fontProgramNoneBold() {
        double rd = Math.random() * 1000;
        String[] fontArr = new String[]{
                FontConstants.COURIER,
                FontConstants.COURIER_OBLIQUE,
                FontConstants.HELVETICA,
                FontConstants.HELVETICA_OBLIQUE,
                FontConstants.TIMES_ROMAN,
                FontConstants.TIMES_ITALIC,

        };
        int index = (int) (rd % fontArr.length - 1);
        return fontArr[index];
    }

    public static String fontProgramSoft() {
        double rd = Math.random() * 1000;
        String[] fontArr = new String[]{
                FontConstants.COURIER,
                FontConstants.TIMES_ROMAN,
                FontConstants.TIMES_ITALIC,
        };
        int index = (int) (rd % fontArr.length - 1);
        return fontArr[index];
    }

    /**
     * @param fontStr
     * @return
     * @throws IOException
     */
    public static PdfFont createFont(String fontStr) throws IOException {
        if (defFontStrList.contains(fontStr)) {
            return PdfFontFactory.createFont(fontStr);
        }
        PdfFont pdfFont = fontMap.get(fontStr);
        if (pdfFont != null) {
            FontProgram fontProgram = pdfFont.getFontProgram();
            return PdfFontFactory.createFont(fontProgram);
        }
        return PdfFontFactory.createFont(defFont);
    }

    public static void main(String[] args) throws IOException {
        System.out.println(fontMap);
        System.out.println(fontStrList);
        for (int i = 0; i < 100; i++) {
            String fontStr = fontProgram();
            PdfFont font = createFont(fontStr);
            System.out.println(font);
        }
    }
}
