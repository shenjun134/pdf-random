//package com.poc.pdf.base;
//
//import net.sourceforge.tess4j.ITesseract;
//import net.sourceforge.tess4j.Tesseract;
//import net.sourceforge.tess4j.TesseractException;
//
//import java.io.File;
//
//public class App {
//
//    public String getImgText(String imageLocation) {
//        ITesseract instance = new Tesseract();
//        try
//        {
//            String imgText = instance.doOCR(new File(imageLocation));
//            return imgText;
//        }
//        catch (TesseractException e)
//        {
//            e.getMessage();
//            return "Error while reading image";
//        }
//    }
//    public static void main ( String[] args)
//    {
//        App app = new App();
//        String userDir = System.getProperty("user.dir");
//        System.out.println(app.getImgText(userDir + "/000000.jpg"));
//    }
//}
