package com.poc.pdf;

import com.poc.pdf.base.FontTest;
import com.poc.pdf.base.GridLines;
import com.poc.pdf.base.NormalTableLines;
import com.poc.pdf.base.TableLines;
import com.poc.pdf.simulate.HumanLangSimu;
import com.poc.pdf.simulate.JanusHendersonSimu;
import com.poc.pdf.simulate.LazardTranSimu;
import com.poc.pdf.simulate.PimcoSimu;
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
        if (StringUtils.equalsIgnoreCase("janus-henderson", type.trim())) {
            System.out.println("begin to print janus-henderson.pdf");
            new JanusHendersonSimu().process();
            return;
        }
        if (StringUtils.equalsIgnoreCase("pimco", type.trim())) {
            System.out.println("begin to print pimco.pdf");
            new PimcoSimu().process();
            return;
        }
        if (StringUtils.equalsIgnoreCase("lazard-tan", type.trim())) {
            System.out.println("begin to print lazard-tan.pdf");
            new LazardTranSimu().process();
            return;
        }
        if (StringUtils.equalsIgnoreCase("human-lang", type.trim())) {
            System.out.println("begin to generate human-lang");
            new HumanLangSimu().process();
            return;
        }
    }
}
