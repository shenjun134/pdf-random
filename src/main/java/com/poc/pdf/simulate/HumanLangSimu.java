package com.poc.pdf.simulate;

import com.poc.pdf.util.FileUtil;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class HumanLangSimu extends SimulatorBase {

    private static final Logger logger = LoggerFactory.getLogger(HumanLangSimu.class);

    interface Constant {
        String netAmount = "netAmount";
        String units = "units";
        String fund = "fund";
        String price = "price";
        String payDate = "payDate";
        String templatePath = "humanlang-template.txt";
        String configPath = "humanlang-config.properties";
        String output = "simulate/human-lang/";
    }

    public static void main(String[] args) throws Exception {
        process();
    }

    public static void process() {
        String userDir = System.getProperty("user.dir");
        List<String> templateList = null;
        try {
            templateList = FileUtils.readLines(new File(userDir + "/" + Constant.templatePath));
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (CollectionUtils.isEmpty(templateList)) {
            throw new RuntimeException("No template found...");
        }
        Properties properties = load(Constant.configPath);
        int total = NumberUtils.toInt(properties.getProperty("total.count", "0"), 0);
        if (total == 0) {
            throw new RuntimeException("No total found...");
        }
        for(int i =0 ;i < total; i++){
            try {
                generate(templateList, properties, i);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void generate(List<String> templateList, Properties properties, int index) throws IOException {
        Map<String, String> map = randomMap();
        String template = randomTemplate(templateList);
        String result = template;
        for(Map.Entry<String, String> entry : map.entrySet()){
            String key = "#" + entry.getKey() + "#";
            result = result.replace(key, entry.getValue());
        }
        List<String> list = JanusHendersonSimu.splitLine(result);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("###################Template is:").append("\n");
        stringBuilder.append(template).append("\n");
        stringBuilder.append("###################Result is:").append("\n");
        stringBuilder.append(StringUtils.join(list, "\n")).append("\n");
        stringBuilder.append("###################Key-Value is:").append("\n");
        stringBuilder.append(map).append("\n");
        String output = Constant.output + StringUtils.leftPad("" + index, 8, "0") + ".txt";
        FileUtil.write(stringBuilder.toString(), output);
    }

    public static String randomTemplate(List<String> tempalteList) {
        Collections.shuffle(tempalteList);
        return tempalteList.get(0);
    }


    public static Map<String, String> randomMap() {
        Map<String, String> map = new HashMap<>();
        map.put(Constant.fund, JanusHendersonSimu.randomFundNumber());
        map.put(Constant.payDate, JanusHendersonSimu.randomDateStr());
        map.put(Constant.units, randomNumber(5, 4, false));
        map.put(Constant.netAmount, randomNumber(5, 2, true));
        map.put(Constant.price, randomNumber(1, 4, true));
        return map;
    }


}
