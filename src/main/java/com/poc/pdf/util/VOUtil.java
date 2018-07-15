package com.poc.pdf.util;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.poc.pdf.model.*;
import org.apache.commons.lang.StringUtils;

public class VOUtil {

    private static Gson gson = new GsonBuilder().create();

    public static String tableVO2Str(TableVO tableVO) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(rowVO2Str(tableVO.getHeader()));
        for (RowVO rowVO : tableVO.getBodyList()) {
            stringBuilder.append(rowVO2Str(rowVO));
        }
        return stringBuilder.toString();
    }

    public static String cellVO2Str(CellVO cellVO) {
        return cellVO.getValue() == null ? "" : cellVO.getValue().toString();
    }

    public static String rowVO2Str(RowVO rowVO) {
        StringBuilder stringBuilder = new StringBuilder();
        if (rowVO != null && rowVO.getList().size() > 0) {
            for (CellVO cellVO : rowVO.getList()) {
                String value = cellVO2Str(cellVO);
                stringBuilder.append("\"").append(value).append("\"").append(",");
            }
        } else {
            stringBuilder.append("NA");
        }
        stringBuilder.append("\n");
        return stringBuilder.toString();
    }

    public static String tableStructionVO2Str(TableStructureVO tableStructureVO) {
        return gson.toJson(tableStructureVO);
    }

    public static TableStructureVO json2TableStruction(String json) {
        return gson.fromJson(json, TableStructureVO.class);
    }


    public static CellPosition extractCellPosition(float xmin, float ymin, TableStructureVO tableStructureVO) {
        CellPosition cellPosition = new CellPosition();
        for (int i = 0; i < tableStructureVO.getColWidth().length; i++) {
            BoxVO boxVO = thBoxVO(i, xmin, ymin, tableStructureVO);
            cellPosition.add(boxVO);
        }

        for (int row = 0; row < tableStructureVO.getRowheight().length - 1; row++) {
            for (int col = 0; col < tableStructureVO.getColWidth().length; col++) {
                BoxVO boxVO = tdBoxVO(row, col, xmin, ymin, tableStructureVO);
                cellPosition.add(boxVO);
            }
        }

        BoxVO tableBox = BoxVO.newTABLE();
        AreaVO areaVO = new AreaVO();
        areaVO.setXmax(tableStructureVO.getTotalWidth() + xmin);
        areaVO.setYmax(tableStructureVO.getTotalHeight() + ymin);
        areaVO.setXmin(0 + xmin);
        areaVO.setYmin(0 + ymin);
        tableBox.setArea(areaVO);
        cellPosition.add(tableBox);

        return cellPosition;
    }


    public static BoxVO thBoxVO(int col, float xBegin, float yBegin, TableStructureVO tableStructureVO) {
        BoxVO boxVO = BoxVO.newTH();
        int row = 0;
        float xmax = getWidth(col + 1, tableStructureVO);
        float ymax = getHeight(row + 1, tableStructureVO);
        float xmin = getWidth(col, tableStructureVO);
        float ymin = getHeight(row, tableStructureVO);

        AreaVO areaVO = new AreaVO();
        areaVO.setXmax(xmax + xBegin);
        areaVO.setYmax(ymax + yBegin);
        areaVO.setXmin(xmin + xBegin);
        areaVO.setYmin(ymin + yBegin);
        boxVO.setArea(areaVO);

        return boxVO;
    }

    public static BoxVO tdBoxVO(int row, int col, float xBegin, float yBegin, TableStructureVO tableStructureVO) {
        BoxVO boxVO = BoxVO.newTD();
        row = row + 1;
        float xmax = getWidth(col + 1, tableStructureVO);
        float ymax = getHeight(row + 1, tableStructureVO);
        float xmin = getWidth(col, tableStructureVO);
        float ymin = getHeight(row, tableStructureVO);

        AreaVO areaVO = new AreaVO();
        areaVO.setXmax(xmax + xBegin);
        areaVO.setYmax(ymax + yBegin);
        areaVO.setXmin(xmin + xBegin);
        areaVO.setYmin(ymin + yBegin);
        boxVO.setArea(areaVO);

        return boxVO;
    }


    public static float getHeight(int row, TableStructureVO tableStructureVO) {
        float sum = 0;
        for (int i = 0; i < row; i++) {
            sum += tableStructureVO.getRowheight()[i];
        }
        return sum;
    }

    public static float getWidth(int col, TableStructureVO tableStructureVO) {
        float sum = 0;
        for (int i = 0; i < col; i++) {
            sum += tableStructureVO.getColWidth()[i];
        }
        return sum;
    }

    public static String cellPosition2XML(CellPosition cellPosition) {
        String xml = RandomUtil.toXml(cellPosition);
        xml = StringUtils.replace(xml, "<Position>", "");
        xml = StringUtils.replace(xml, "<list>", "");
        xml = StringUtils.replace(xml, "</Position>", "");
        xml = StringUtils.replace(xml, "</list>", "");

        return xml;
    }

    /**
     * @param json
     * @param xmin
     * @param ymin
     * @return
     */
    public static String json2Xml(String json, float xmin, float ymin) {
        TableStructureVO tableStructureVO = json2TableStruction(json);

        CellPosition cellPosition = extractCellPosition(xmin, ymin, tableStructureVO);

        String xml = cellPosition2XML(cellPosition);

        return xml;
    }

    public static String mergeResult(String json, float xmin, float ymin, ScanResult scanResult) {
        String objectXml = json2Xml(json, xmin, ymin);
        String endFix = "</annotation>";
        String resultXml = RandomUtil.toXml(scanResult);
        resultXml = StringUtils.replace(resultXml, endFix, "");
        resultXml = resultXml + objectXml + "" + endFix;

        return resultXml;
    }

    //

    public static void main(String[] args) {
        String json = "{\"totalHeight\":584.0,\"totalWidth\":1382.4,\"colWidth\":[73.0,73.0,73.0,73.0,73.0,73.0,73.0,73.0],\"rowheight\":[71.81299,35.906494,53.85974,179.53247,107.71948,107.71948,89.766235,89.766235,125.67272,143.62598,71.81299,71.81299,71.81299,107.71948,53.85974]}";


        float xmin = 100;
        float ymin = 100;

        String objectXml = json2Xml(json, xmin, ymin);

//        System.out.println(objectXml);


        ScanResult scanResult = new ScanResult(200f, 300f);
        scanResult.setFolder("VOC2007");
        scanResult.setFilename("000000.jpg");
        scanResult.setOwner("HengTian");

        String endFix = "</annotation>";
        String resultXml = RandomUtil.toXml(scanResult);
        resultXml = StringUtils.replace(resultXml, endFix, "");

        resultXml = resultXml + objectXml + "" + endFix;

        System.out.println(resultXml);

    }

}
