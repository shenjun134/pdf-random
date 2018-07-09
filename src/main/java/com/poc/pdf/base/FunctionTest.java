package com.poc.pdf.base;

//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.lowagie.text.*;
import com.lowagie.text.Font;
import com.lowagie.text.Image;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import com.poc.pdf.model.CellVO;
import com.poc.pdf.model.RowVO;
import com.poc.pdf.model.TableVO;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Stream;

public class FunctionTest {

    public static final String FONT_FAMILY = "Courier";

    interface Constant {
        String paragraph = "Document document = new Document()";


        String paragraph2 = "private void addTableHeader(PdfPTable table) ";
    }


    public static void main(String[] args) throws Exception {

//        generateText("Hello world!");
//        generateImage();
//        generateTable();

//        generateTextImageTable();

        withPageSize();

//        readPDF();
//        System.out.println(createdTableVO());
    }


    public static void readPDF() throws IOException {
        String userDir = System.getProperty("user.dir");
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(userDir + "/with-page.pdf"));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
        System.out.println();
//        PdfArray position = form.getField(name).getWidgets().get(0).getRectangle();
    }

    public static void generateText(String text) {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("with-text.pdf"));
            document.open();
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, Color.BLACK);
            Chunk chunk = new Chunk(text, font);
            document.add(chunk);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }


    public static void generateImage() {
        Path path = null;
        Rectangle pagesize = new Rectangle(2480f, 3508f);
        float marginLeft = 0f;
        float marginRight = 0f;
        float marginTop = 0f;
        float marginBottom = 0f;
        Document document = new Document(pagesize, marginLeft, marginRight, marginTop, marginBottom);
        Image img = null;
        try {
            String userDir = System.getProperty("user.dir");
            path = Paths.get(userDir + "/000000.jpg");
            PdfWriter.getInstance(document, new FileOutputStream("with-image.pdf"));
            document.open();
            img = Image.getInstance(path.toAbsolutePath().toString());
            document.add(img);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    public static void generateTable() {
        Document document = new Document();
        try {
            PdfWriter.getInstance(document, new FileOutputStream("with-table.pdf"));
            document.open();
            PdfPTable table = new PdfPTable(3);
            addTableHeader(table);
            addRows(table);
            addCustomRows(table);
            document.add(table);
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }


    public static void withPageSize() throws IOException, DocumentException {
        float xOffset = 0f;
        float yOffset = 0f;
        Rectangle pagesize = new Rectangle(2480f + xOffset, 3508f + yOffset);
        float marginLeft = 140f;
        float marginRight = 0f;
        float marginTop = 80f;
        float marginBottom = 0f;

        Document document = new Document(pagesize, marginLeft, marginRight, marginTop, marginBottom);
        // step 2
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream("with-page.pdf"));
        // step 3
        document.open();
//        writer.setSpaceCharRatio(PdfWriter.SPACE_CHAR_RATIO_DEFAULT);
        writer.setSpaceCharRatio(23f);

        String firstSpace = StringUtils.leftPad(" ", 4);
        String paragraphText =
                firstSpace + "Many hardware modifications were necessary to measure our heuristic. we\n" +
                        "scripted a simulation on Intel's Internet testbed to disprove pervasive symmetries's\n" +
                        "effect on the mystery of artificial intelligence. This step flies in the face of\n" +
                        "conventional wisdom, but is essential to our results. To begin with, we added some\n" +
                        "optical drive space to Intel's Internet~2 overlay network. The 2kB of NVQRAM\n" +
                        "described here explain our expected results. Second, scholars added 10MB of NVeRAM\n" +
                        "to our human test subjects to prove opportunistically cacheable information's impact\n" +
                        "on the work of American complexity theorist J1 Smith. Further, we added lOGb/s of\n" +
                        "Ethernet access to MIT's mobile telephones to measure collectively concurrent\n" +
                        "information's effect on C. Antony R. Hoare's refinement of multiiprocessors in 1993.\n" +
                        "Finally, we added lOOGB/s of Wi—Fi throughput to the KGB's mobile telephones.\n\n" +
                        firstSpace + "web services must work. A confusing problem in algorithms is the understanding\n" +
                        "of probabilistic configurations. Contrarily, this method is always well—received.\n" +
                        "The structured unification of information retrieval systems and RPCs would\n" +
                        "profOundly improve the exploration of write—ahead logging.\n\n" +
                        firstSpace + "We ran a 4—year—long trace validating that our architecture is solidly grounded\n" +
                        "in reality. This may or may not actually hold in reality. Furthermore, Fovilla does\n" +
                        "not require such a practical refinement to run correctly, but it doesn't hurt.\n" +
                        "Fovilla does not require such a typical refinement to run correctly, but it doesn't\n" +
                        "hurt. This is a theoretical property of our system. See our prior technical report\n" +
                        "[l6] for details [20].";

        String paragraphText2 = "" + firstSpace + "Lastly, we discuss experiments (3) and (4) enumerated above. The many\n" +
                "discontinuities in the graphs point to exaggerated popularity of the transistor\n" +
                "introduced with our hardware upgrades. Note that Figure 3 shows the average and not\n" +
                "expected Bayesian ROM speed. Such a hypothesis is usually a compelling mission but\n" +
                "is derived from known results. The data in Figure 4, in particular, proves that four\n" +
                "years of hard work were wasted on this project.\n\n" +
                firstSpace + "NOW for the climactic analysis of the second half of our experiments. Operator\n" +
                "error alone cannot account for these results. Though it at first glance seems\n" +
                "unexpected, it is supported by previous work in the field. Second, operator error\n" +
                "alone cannot account for these results. It is usually a technical mission but fell\n" +
                "in line with our expectations. Operator error alone cannot account for these\n" +
                "results.\n\n\n" +
                firstSpace + "Our experiences with our approach and multiiprocessors validate that\n" +
                "information retrieval systems and the Turing machine are always incompatible.\n" +
                "Furthermore, we verified that I/O automata and semaphores can collaborate to\n" +
                "surmount this riddle. we plan to explore more issues related to these issues in\n" +
                "future work.";
        // step 4
        BaseFont bf1 = null;
        try {
            bf1 = BaseFont.createFont(
                    FONT_FAMILY, "UTF-8", BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Font font = new Font(bf1);
        int red = 17;
        int green = 17;
        int blue = 17;

        font.setColor(red, green, blue);
//        font.setFamily("Abhaya Libre");
        font.setSize(46f);

        Paragraph paragraph = new Paragraph("", font);
        paragraph.setAlignment(Element.ALIGN_JUSTIFIED);
        paragraph.setLeading(50);

        document.add(paragraph);

        /**
         * skew
         */
        float alpha = -2f;
//        float alpha = 0f;

        float beta = 0f;

        Chunk chunk = new Chunk(paragraphText, font);
        chunk.setSkew(alpha, beta);

        document.add(chunk);
        document.add(Chunk.NEWLINE);

        TableVO tableVO = createdTableVO();
        PdfPTable table = generatePDFTable(tableVO);


        float totalHeight = table.getTotalHeight();
        float totalWidth = table.getTotalWidth();

        float left = document.left();
        float right = document.right();
        float top = document.top();
        float bottom = document.bottom();
        System.out.println("left:" + left);
        System.out.println("right:" + right);
        System.out.println("top:" + top);
        System.out.println("bottom:" + bottom);
        System.out.println("totalHeight:" + totalHeight);
        System.out.println("totalWidth:" + totalWidth);

        float currentTop = document.top(10f);

        System.out.println("currentTop:" + currentTop);
        String userDir = System.getProperty("user.dir");
        Path tablePath = Paths.get(userDir + "/img-table.png");
        Image img3 = null;
        img3 = Image.getInstance(tablePath.toAbsolutePath().toString());
        img3.scalePercent(100);
        img3.setRotationDegrees(alpha);
//        img3.setAbsolutePosition(300f, 1900f);
//        document.add(img3);

        PdfPTable tempTable = new PdfPTable(1);
        tempTable.setHorizontalAlignment(1);
        PdfPCell tableCell = new PdfPCell(img3);
        tableCell.setPadding(0);
        tableCell.setBorderWidth(0);
        tempTable.addCell(tableCell);
        document.add(tempTable);


//        document.add(table);



        left = document.left();
        right = document.right();
        top = document.top();
        bottom = document.bottom();
        totalHeight = table.getTotalHeight();
        totalWidth = table.getTotalWidth();
        float[] w = table.getAbsoluteWidths();
        System.out.println("---------------------------");
        System.out.println("left:" + left);
        System.out.println("right:" + right);
        System.out.println("top:" + top);
        System.out.println("bottom:" + bottom);
        System.out.println("totalHeight:" + totalHeight);
        System.out.println("totalWidth:" + totalWidth);
        System.out.println("w:" + Arrays.toString(w));

        Chunk chunk2 = new Chunk(paragraphText2, font);
        chunk2.setSkew(alpha, beta);

        document.add(chunk2);
        document.add(Chunk.NEWLINE);


        Path chartPath = Paths.get(userDir + "/chart.png");
        Path signaturePath = Paths.get(userDir + "/signature.png");


        PdfPTable footerTable = new PdfPTable(1);
        Image img1 = null;
        Image img2 = null;


        try {
            img1 = Image.getInstance(chartPath.toAbsolutePath().toString());
            img1.scalePercent(100);

            PdfPCell chartCell = new PdfPCell(img1);
            chartCell.setBorderWidth(0);
            chartCell.setPaddingLeft(550);
            footerTable.addCell(chartCell);


            img2 = Image.getInstance(signaturePath.toAbsolutePath().toString());
            img2.scalePercent(100);

            PdfPCell signatureCell = new PdfPCell(img2);
            signatureCell.setBorderWidth(0);
            signatureCell.setPaddingLeft(350);
            footerTable.addCell(signatureCell);

//            img3 = Image.getInstance(tablePath.toAbsolutePath().toString());
//            img3.scalePercent(100);


            document.add(footerTable);

//            document.add(img3);

        } catch (IOException e) {
            e.printStackTrace();
        }
        // step 5
        document.close();
    }

    public static void generateTextImageTable() {
        Document document = new Document();
        float alpha = 0.1f;
        float beta = 0.2f;
        try {
            PdfWriter.getInstance(document, new FileOutputStream("with-table-text-image.pdf"));
            document.open();
            /**
             * add paragraph begin
             */
            Font font = FontFactory.getFont(FontFactory.COURIER, 16, Color.BLACK);
            Chunk chunk = new Chunk(Constant.paragraph, font);
            chunk.setSkew(alpha, beta);
            document.add(chunk);
            /**
             * add paragraph end
             */

            PdfPTable table = new PdfPTable(3);
//            table.setSpacingBefore(500);
            addTableHeader(table);
            addRows(table);
            addCustomRows(table);
            document.add(table);


            /**
             * add paragraph begin
             */
            Chunk chunk2 = new Chunk(Constant.paragraph2, font);

            chunk2.setSkew(alpha, beta);
            document.add(chunk2);
            /**
             * add paragraph end
             */
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (document != null) {
                document.close();
            }
        }
    }

    private static void addTableHeader(PdfPTable table) {
        Stream.of("column header 1", "column header 2", "column header 3")
                .forEach(columnTitle -> {
                    PdfPCell header = new PdfPCell();
//                    header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    header.setBorderWidth(2);
                    header.setPhrase(new Phrase(columnTitle));
                    table.addCell(header);
                });
    }

    private static void addRows(PdfPTable table) {
        table.addCell("row 1, col 1");
        table.addCell("row 1, col 2");
        table.addCell("row 1, col 3");
    }

    private static void addCustomRows(PdfPTable table)
            throws URISyntaxException, BadElementException, IOException {
        String userDir = System.getProperty("user.dir");
        Path path = Paths.get(userDir + "/1.jpg");
        Image img = Image.getInstance(path.toAbsolutePath().toString());
        img.scalePercent(10);

        PdfPCell imageCell = new PdfPCell(img);
        table.addCell(imageCell);

        PdfPCell horizontalAlignCell = new PdfPCell(new Phrase("row 2, col 2"));
        horizontalAlignCell.setHorizontalAlignment(Element.ALIGN_CENTER);
        table.addCell(horizontalAlignCell);

        PdfPCell verticalAlignCell = new PdfPCell(new Phrase("row 2, col 3"));
        verticalAlignCell.setVerticalAlignment(Element.ALIGN_BOTTOM);
        table.addCell(verticalAlignCell);
    }


    private static TableVO createdTableVO() {
        String headerStr = "department PM status manager commission member";
        String bodyStr = "-40791 75931 65046 97433 89780 -37667.06\n" +
                "-72575 -39754 6cHFJd 16722 13613 -28747\n" +
                "\n" +
                "58407.92 32120 25223.4 GySQWOrK -81452 8393\n" +
                "\n" +
                "-93549.53 -69912 20544.94 38824 eBE2Hk3C0 -15053.91";


        String[] hArr = StringUtils.split(headerStr, " ");


        RowVO header = new RowVO();
        java.util.List<RowVO> body = new ArrayList<>();
        for (String h : hArr) {
            CellVO cellVO = new CellVO(h);
            header.add(cellVO);
        }
        String[] rowArr = StringUtils.split(bodyStr, "\n");
        for (String rowStr : rowArr) {
            if (StringUtils.isBlank(rowStr)) {
                continue;
            }
            String[] cellArr = StringUtils.split(rowStr, " ");
            RowVO row = new RowVO();
            for (String c : cellArr) {
                CellVO cellVO = new CellVO(c);
                row.add(cellVO);
            }
            body.add(row);

        }


        TableVO tableVO = new TableVO(header, body);
        return tableVO;
    }

    private static PdfPTable generatePDFTable(TableVO tableVO) {
        String space = StringUtils.leftPad("", 1);
        float paddingBottom = 40f;
        float lastPaddingBottom = 20f;
        float paddingLeft = 30f;
        float paddingTop = 20f;
        BaseFont bf1 = null;
        try {
            bf1 = BaseFont.createFont("Times-Bold"
                    , "UTF-8", BaseFont.EMBEDDED);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (DocumentException e) {
            e.printStackTrace();
        }
        Font font = new Font(bf1);
        int red = 00;
        int green = 00;
        int blue = 00;
        font.setColor(red, green, blue);
//        font.setFamily("Abhaya Libre");
        font.setSize(46f);
        font.setStyle(Font.NORMAL);

        int colSize = tableVO.cloSize();
        PdfPTable table = new PdfPTable(colSize);
        table.setRunDirection(1);
        table.setHorizontalAlignment(1);

        int borderWith = 6;
        /**
         * set header
         */
        for (int i = 0; i < colSize; i++) {
            CellVO cellVO = tableVO.getHeader().getList().get(i);
            PdfPCell header = new PdfPCell();
            header.setBorderWidth(0);
            String value = cellVO.getValue().toString();
            if (i == 0) {
                header.setBorderWidthLeft(borderWith);
                value = cellVO.getValue().toString();
            }

            if (i == colSize - 1) {
                header.setBorderWidthRight(borderWith);
            }
            header.setHorizontalAlignment(1);
            header.setBorderWidthTop(borderWith);
            header.setPaddingLeft(paddingLeft);
            header.setPaddingTop(paddingTop);
            header.setPaddingBottom(paddingBottom);

            Phrase phrase = new Phrase(value, font);
            header.setPhrase(phrase);
            table.addCell(header);
        }


        /**
         * set body
         */
        int rowSize = tableVO.getBodyList().size();
        for (int i = 0; i < rowSize; i++) {
            RowVO rowVO = tableVO.getBodyList().get(i);
            boolean isLast = i == rowSize - 1;

            for (int j = 0; j < colSize; j++) {
                CellVO cellVO = rowVO.getList().get(j);
                PdfPCell header = new PdfPCell();
                header.setBorderWidth(0);
                String value = cellVO.getValue().toString();
                if (j == 0) {
                    header.setBorderWidthLeft(borderWith);
                    value = space + cellVO.getValue().toString();
                }
                if (j == colSize - 1) {
                    header.setBorderWidthRight(borderWith);
                }
                header.setPaddingBottom(paddingBottom);
                if (isLast) {
                    header.setBorderWidthBottom(borderWith);
                    header.setPaddingBottom(lastPaddingBottom);
                }
                header.setHorizontalAlignment(1);
                header.setPaddingLeft(paddingLeft);
                Phrase phrase = new Phrase(value, font);
                header.setPhrase(phrase);
                table.addCell(header);
            }

        }


        return table;
    }
}
