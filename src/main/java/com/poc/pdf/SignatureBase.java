package com.poc.pdf;

import com.poc.pdf.base.GridLines;
import com.poc.pdf.base.TableLines;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;

public class SignatureBase {

    public static void main(String[] args) throws IOException {
//        args = new String[]{"table"};
//        args = new String[]{"grid"};
        process(args);
    }

    private static void process(String[] args) throws IOException {
        if (args == null || args.length == 0) {
            throw new RuntimeException("Unknown operation, please enter table or grid");
        }
        String type = args[0];
        if (StringUtils.equalsIgnoreCase("table", type.trim())) {
            System.out.println("begin to random table layout");
            TableLines.process();
            return;
        }
        if (StringUtils.equalsIgnoreCase("grid", type.trim())) {
            System.out.println("begin to random grid layout");
            GridLines.process();

        }
    }
}
