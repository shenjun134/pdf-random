package com.poc.pdf;

import com.poc.pdf.layout.PdfTestPage_1;
import com.poc.pdf.layout.PdfTestPage_2;
import com.poc.pdf.table.Table2Image_1;
import com.poc.pdf.table.Table2Image_2;
import org.apache.commons.lang.math.NumberUtils;

public class Base {

    public static void main(String[] args) throws Exception {
        int count = Constant.defCount;
        int beginAt = Constant.defBeginAt;
        if (args != null && args.length > 0) {
            count = NumberUtils.toInt(args[0], Constant.defCount);
        }
        if (args != null && args.length > 1) {
            beginAt = NumberUtils.toInt(args[1], Constant.defBeginAt);
        }
        if (beginAt > Constant.defMaxBegin) {
            beginAt = Constant.defMaxBegin;
        } else if (beginAt < 0) {
            beginAt = 0;
        }

        process(count, beginAt);
    }

    public static void process(int count, int beginAt) {
        Long startAt = System.currentTimeMillis();
        try {
            Table2Image_1.process2(beginAt, count + beginAt);
            Table2Image_2.process2(count + beginAt, 2 * count + beginAt);

            System.out.println("################# random table completed!!!");

            PdfTestPage_1.process2(beginAt, count + beginAt);
            PdfTestPage_2.process2(count + beginAt, 2 * count + beginAt);
        } catch (Exception e) {
            System.out.println("process error ...");
            e.printStackTrace();
        } finally {
            System.out.println("**************** Layout-1 begin:" + beginAt + " end:" + (count - 1 + beginAt));
            System.out.println("**************** Layout-2 begin:" + (count + beginAt) + " end:" + (2 * count - 1 + beginAt));
            System.out.println("**************** Total used:" + (System.currentTimeMillis() - startAt));
        }
    }

    interface Constant {
        int defCount = 200;

        int defBeginAt = 100000;

        int defMaxBegin = 990000;
    }
}
