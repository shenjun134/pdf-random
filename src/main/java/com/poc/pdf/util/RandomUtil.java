package com.poc.pdf.util;

import com.poc.pdf.enums.FieldType;
import com.poc.pdf.model.AreaVO;
import com.poc.pdf.model.BoxVO;
import com.poc.pdf.model.CellVO;
import com.poc.pdf.model.ScanResult;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;

import java.text.DecimalFormat;

public class RandomUtil {


    public static String defRandom() {
        return randomStr(8, 4);
    }

    public static String randomStr(int max, int min) {
        double random = Math.random() * 100d;
        int length = (int) (Math.random() * 100d % max);
        if (length < min && length > 0) {
            length = min;
        } else if (length == 0) {
            length = max;
        }

        if (random <= Constant.numberOnly) {
            return RandomStringUtils.random(length, false, true);
        } else if (random <= Constant.stringOnly) {
            return RandomStringUtils.random(length, true, false);
        } else if (random <= Constant.mix) {
            return RandomStringUtils.random(length, true, true);
        }
        return RandomStringUtils.random(length, true, true);
    }


    public static String randomStr(FieldType fieldType, Object... params) {
        switch (fieldType) {
            case DATE:
                return randomDate();
            case FUND:
                return RandomStringUtils.random(4, true, true).toUpperCase();
            case NUMBER: {
                if (params == null || params.length == 0) {

                    return randomNum();
                }
                if (params[0] instanceof CellVO) {
                    CellVO cellVO = (CellVO) params[0];
                    String intPart = "";
                    String decimalPart = "";

                    if (cellVO.isIntEnable() && cellVO.getIntMinCount() >= 0) {
                        int rd = randomInt(cellVO.getIntMaxCount(), cellVO.getIntMinCount());
                        intPart = RandomStringUtils.random(rd, false, true);
                    }
                    if (cellVO.isDecimalEnable() && cellVO.getDecimalMinCount() > 0) {
                        int rd = randomInt(cellVO.getDecimalMaxCount(), cellVO.getDecimalMinCount());
                        decimalPart = "." + RandomStringUtils.random(rd, false, true);
                    }
                    if (intPart.length() != 0 || decimalPart.length() != 0) {
                        intPart = intPart.length() == 0 ? "0" : intPart;
                        return intPart + decimalPart;
                    }
                }
                return randomNum();
            }
            case STRING:
                return defRandom();
            /**
             * 6 digital
             */
            case AGREEMENT:
                return RandomStringUtils.random(6, false, true);

            /**
             * CP3A-OW02
             */
            case W_FUND:
                return RandomStringUtils.random(4, true, true).toUpperCase() + "-" + RandomStringUtils.random(4, true, true).toUpperCase();
            /**
             * Sell-Payment, Securities Internal Transfer,
             */
            case TXN_TYPE:
                return randomBool() ? "Sell-Payment" : "Securities Internal Transfer";

            /**
             * 12 string begin with XS0 or GB0
             */
            case ISIN: {
                String begin = randomBool() ? "XS0" : "GB0";
                return begin + RandomStringUtils.random(9, true, true).toUpperCase();
            }

            /**
             * Euroclear, CREST,
             */
            case LOCATION:
                return randomBool() ? "Euroclear" : "CREST";

            /**
             * SW-SEC-1
             */
            case REFERENCE:
                return "SW-SEC-" + RandomStringUtils.random(2, false, true);

            /**
             * ACCOUNTING ONLY/CUSTODY ONLY
             */
            case TYPE:
                return randomBool() ? "ACCOUNTING ONLY" : "CUSTODY ONLY";

            /**
             * 5 digital
             */
            case BROKER:
                return RandomStringUtils.random(5, false, true);

            case BLOCK_ID:
                return RandomStringUtils.random(9, false, true) + "_F1";
            case ASSET_ID:
                return RandomStringUtils.random(9, true, true).toUpperCase();
            case SECURITY_DESC: {
                int rd = randomInt(6, 1);
                if (rd == 1) {
                    return "VANGUARD-JAPAN STK IND-GBP ACC";
                }
                if (rd == 2) {
                    return "VANGUARD DEV WORLD XUK EI-A";
                }
                if (rd == 3) {
                    return "VANGUARD-JAPAN STK INDX-GBPA";
                }
                if (rd == 4) {
                    return "VANGUARD-US EQUITY INDEX-A";
                }
                if (rd == 5) {
                    return " BLOCKROCK CONF EUR INC-A ACC";
                }
                if (rd == 6) {
                    return "BLOCKROCK CIF-EM NKT EQ-D";
                }
                return "VANGUARD-JAPAN STK IND-GBP ACC";
            }

            case CURRENCY: {
//                GBP, USD, RMB, EUR
                int rd = randomInt(4, 1);
                if (rd == 1) {
                    return "GBP";
                }
                if (rd == 2) {
                    return "USD";
                }
                if (rd == 3) {
                    return "RMB";
                }
                if (rd == 4) {
                    return "EUR";
                }
                return "GBP";
            }

        }
        return defRandom();
    }


    /**
     * length = 10
     *
     *
     * @return 23/02/2018
     */
    public static String randomDate() {
        String day = randomDay();
        String month = randomMonth();
        String year = randomYear();
        return new StringBuilder().append(day).append("/").append(month).append("/").append(year).toString();
    }


    /**
     * 01 ~ 28
     *
     * @return
     */
    public static String randomDay() {
        return StringUtils.leftPad("" + randomInt(28, 1), 2, "0");
    }

    /**
     * 01 ~ 12
     *
     * @return
     */
    public static String randomMonth() {
        return StringUtils.leftPad("" + randomInt(12, 1), 2, "0");
    }

    /**
     * 2017 ~ 2018
     *
     * @return
     */
    public static String randomYear() {
        return StringUtils.leftPad("20" + randomInt(18, 17), 2, "0");
    }


    public static boolean randomBool() {
        return Math.random() > 0.5;
    }

    /**
     * 100,000.00 ~ 9,999,999,999.00
     *
     * @return
     */
    public static String randomNum() {
        DecimalFormat decimalFormat = new DecimalFormat("##,###.00");
        int count = randomInt(10, 6);
        String strNum = RandomStringUtils.random(count, false, true);
        try {
            int num = Integer.valueOf(strNum);
            return decimalFormat.format(num);
        } catch (NumberFormatException e) {
            return "100,000.00";
        }
    }


    public static int randomInt(int maxRow, int minRow) {
        int length = (int) (Math.random() * 1000000d % maxRow);
        if (length < minRow && length > 0) {
            length = minRow;
        } else if (length == 0) {
            length = maxRow;
        }
        return length;
    }

    interface Constant {

        int numberOnly = 50;
        int stringOnly = 80;
        int mix = 100;
    }


    /**
     * @param instance
     * @return
     */
    public static String toXml(Object instance) {
        Class<?> clazz = instance.getClass();
        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(clazz);
        return xstream.toXML(instance);
    }

    /**
     * @param instance
     * @return
     */
    public static String toXmlWithHead(Object instance) {
        Class<?> clazz = instance.getClass();
        XStream xstream = new XStream(new DomDriver());
        xstream.processAnnotations(clazz);
//        return new StringBuilder().append("<?xml version=\"1.0\"?>\n").append(xstream.toXML(instance)).toString();
        return xstream.toXML(instance);
    }

    public static void main(String[] args) {
//        for (int i = 0; i < 100; i++) {
//            System.out.println(randomDate());
//            System.out.println(randomBool());
//        }


        ScanResult scanResult = new ScanResult(200f, 300f);
        scanResult.setFolder("VOC2007");
        scanResult.setFilename("000000.jpg");
        scanResult.setOwner("HengTian");

        System.out.println(toXmlWithHead(scanResult));

    }

}
