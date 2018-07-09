package com.poc.pdf.layout;

//import com.itextpdf.text.*;
//import com.itextpdf.text.pdf.BaseFont;
//import com.itextpdf.text.pdf.PdfPCell;
//import com.itextpdf.text.pdf.PdfPTable;
//import com.itextpdf.text.pdf.PdfWriter;

import com.lowagie.text.*;
import com.lowagie.text.pdf.BaseFont;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;
import org.apache.commons.lang.StringUtils;

import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class PdfTestPage {

    public static final String FONT_FAMILY = "Courier";


    public static void main(String[] args) throws Exception {
        withPageSize();
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
                "years of hard work were wasted on this project.\n" +
                firstSpace + "NOW for the climactic analysis of the second half of our experiments. Operator\n" +
                "error alone cannot account for these results. Though it at first glance seems\n" +
                "unexpected, it is supported by previous work in the field. Second, operator error\n" +
                "alone cannot account for these results. It is usually a technical mission but fell\n" +
                "in line with our expectations. Operator error alone cannot account for these\n" +
                "results.\n" +
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


        float left = document.left();
        float right = document.right();
        float top = document.top();
        float bottom = document.bottom();
        System.out.println("left:" + left);
        System.out.println("right:" + right);
        System.out.println("top:" + top);
        System.out.println("bottom:" + bottom);

        float currentTop = document.top(10f);

        System.out.println("currentTop:" + currentTop);
        String userDir = System.getProperty("user.dir");
        Path tablePath = Paths.get(userDir + "/img-table.png");
        Image img3 = null;
        img3 = Image.getInstance(tablePath.toAbsolutePath().toString());
        img3.scalePercent(100);
        img3.setRotationDegrees(alpha);
        img3.setAbsolutePosition(300f, 1700f);
        document.add(img3);

//        PdfPTable tempTable = new PdfPTable(1);
//        tempTable.setHorizontalAlignment(1);
//        PdfPCell tableCell = new PdfPCell(img3);
//        tableCell.setPadding(0);
//        tableCell.setBorderWidth(0);
//        tempTable.addCell(tableCell);
//        document.add(tempTable);


//        document.add(table);


        left = document.left();
        right = document.right();
        top = document.top();
        bottom = document.bottom();
        System.out.println("---------------------------");
        System.out.println("left:" + left);
        System.out.println("right:" + right);
        System.out.println("top:" + top);

        int rowSize = 14;
        String blankRow = StringUtils.leftPad("", rowSize, "\n");

        Chunk chunk2 = new Chunk(blankRow + paragraphText2, font);
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




}
