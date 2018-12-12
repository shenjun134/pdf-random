package com.poc.pdf.simulate;

import com.poc.pdf.temp.PIMCOSimu;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;

import java.util.Properties;

public class PimcoSimu extends SimulatorBase {

    private static final Logger logger = Logger.getLogger(PimcoSimu.class);

    public static void process() {
        Long startAt = System.currentTimeMillis();
        try {
            Properties properties = load();
            int total = NumberUtils.toInt(properties.getProperty("pimco.total"), 0);
            String prefix = properties.getProperty("pimco.prefix");
            for (int i = 0; i < total; i++) {
                PIMCOSimu.process(prefix + "-" + StringUtils.leftPad("" + i, 5, "0"), properties);
            }
        } catch (Exception e) {
            logger.error("PimcoSimu process error", e);
        } finally {
            System.out.println("Total Used:" + (System.currentTimeMillis() - startAt));
        }
    }

}
