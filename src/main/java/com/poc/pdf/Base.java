package com.poc.pdf;

import com.poc.pdf.layout.PdfTestPage_1;
import com.poc.pdf.layout.PdfTestPage_2;
import com.poc.pdf.table.Table2Image_1;
import com.poc.pdf.table.Table2Image_2;
import org.apache.commons.lang.math.NumberUtils;

public class Base {

    public static void main(String[] args) throws Exception {
        int count = Constant.defCount;
        if (args != null && args.length > 0) {
            count = NumberUtils.toInt(args[0], Constant.defCount);
        }
        process(count);
    }

    public static void process(int count) {
        Long startAt = System.currentTimeMillis();
        try {
            Table2Image_1.process2(0, count);
            Table2Image_2.process2(count, 2 * count);

            System.out.println("################# random table completed!!!");

            PdfTestPage_1.process2(0, count);
            PdfTestPage_2.process2(count, 2 * count);
        } catch (Exception e) {
            System.out.println("process error ...");
            e.printStackTrace();
        } finally {
            System.out.println("**************** Layout-1 begin:" + 0 + " end:" + (count - 1));
            System.out.println("**************** Layout-2 begin:" + count + " end:" + (2 * count - 1));
            System.out.println("**************** Total used:" + (System.currentTimeMillis() - startAt));
        }
    }

    interface Constant {
        int defCount = 5;
    }
}
