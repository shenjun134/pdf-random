package com.poc.pdf.util;

import com.poc.pdf.model.FileInfo;
import com.poc.pdf.model.MockData;
import com.poc.pdf.model.MockHead;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.lang.StringUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MockDataUtil {

    public static MockData parseMockData(String path) {
        String userDir = System.getProperty("user.dir");
        String propName = path;
        File file = new File(userDir + "/" + propName);
        Reader reader = null;
        MockData mockData = new MockData();
        try {
            reader = new BufferedReader(new FileReader(file));
            CSVParser csvParser = new CSVParser(reader, CSVFormat.DEFAULT);
            int index = 0;
            for (CSVRecord csvRecord : csvParser) {
                int size = csvRecord.size();
                index++;
                if (index == 1) {
                    List<MockHead> list = new ArrayList<>(size);
                    for (int i = 0; i < size; i++) {
                        String value = csvRecord.get(i);
                        MockHead mockHead = new MockHead();
                        mockHead.setName(value);
                        mockHead.setIndex(i);
                        list.add(mockHead);
                    }
                    mockData.setHeadList(list);
                    continue;
                }
                List<String> list = new ArrayList<>(size);
                for (int i = 0; i < size; i++) {
                    String value = csvRecord.get(i);
                    list.add(value);
                }
                mockData.getValueList().add(list);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("No " + propName + " found", e);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("No " + propName + " found", e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if (CollectionUtils.isEmpty(mockData.getHeadList())) {
            return mockData;
        }
        for (int i = 0, len = mockData.getHeadList().size(); i < len; i++) {
            MockHead mockHead = mockData.getHeadList().get(i);
            int max = getMaxSize(mockData, i);
            int headLength = StringUtils.length(mockHead.getName());
            if (max < headLength) {
                max = headLength;
            }
            mockData.getHeadList().get(i).setMaxLenght(max);
        }
        return mockData;
    }

    private static int getMaxSize(MockData mockData, int colIndex) {
        int max = 0;
        for (List<String> row : mockData.getValueList()) {
            String value = row.get(colIndex);
            int length = StringUtils.length(value);
            if (length > max) {
                max = length;
            }
        }
        return max;
    }


    public static void main(String[] args) {

        String path = "mock-data.txt";
        MockData mockData = parseMockData(path);


        System.out.println(mockData);

    }

    public static List<MockHead> copyShuffle(List<MockHead> headList) {
        List<MockHead> temp = new ArrayList<>(headList);
        Collections.shuffle(temp);
        return temp;
    }
}
