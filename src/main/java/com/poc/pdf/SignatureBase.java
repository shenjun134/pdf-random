package com.poc.pdf;

import com.poc.pdf.base.FontTest;
import com.poc.pdf.base.GridLines;
import com.poc.pdf.base.NormalTableLines;
import com.poc.pdf.base.TableLines;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class SignatureBase {

    public static void main(String[] args) throws IOException {
//        args = new String[]{"table"};
//        args = new String[]{"grid"};
//        args = new String[]{"font-test"};
        process(args);
    }

    private static void process(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            throw new RuntimeException("Unknown operation, please enter table, grid, font-test");
        }
        String type = args[0];
        if (StringUtils.equalsIgnoreCase("table", type.trim())) {
            System.out.println("begin to random table layout");
            TableLines.process();
            return;
        }
        if (StringUtils.equalsIgnoreCase("normal-table", type.trim())) {
            System.out.println("begin to random normal-table layout");
            NormalTableLines.process();
            return;
        }
        if (StringUtils.equalsIgnoreCase("grid", type.trim())) {
            System.out.println("begin to random grid layout");
            GridLines.process();
            return;
        }
        if (StringUtils.equalsIgnoreCase("font-test", type.trim())) {
            System.out.println("begin to print font-test.pdf");
            new FontTest().process();
            return;
        }
    }
}
