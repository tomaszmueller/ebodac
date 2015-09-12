package org.motechproject.ebodac.util;

import com.itextpdf.text.Rectangle;

import java.io.OutputStream;

public class PdfReportBTemplate extends PdfBasicTemplate {

    private static final String TEMPLATE_PATH = "/report_templateB.pdf";
    private static final Rectangle FIRST_PAGE_RECTANGLE = new Rectangle(20, 36, 580, 475);

    private static final String PDF_TEMPLATE_TITLE = "#titleReport";
    private static final String PDF_TEMPLATE_DISTRICT = "#district";
    private static final String PDF_TEMPLATE_CHIEFDOM = "#chiefdom";

    public PdfReportBTemplate(OutputStream outputStream) {
        super(TEMPLATE_PATH, FIRST_PAGE_RECTANGLE, outputStream);
    }

    public void setAdditionalCellValues(String title, String  distinct, String chiefdom) {
        setAdditionalCellValue(PDF_TEMPLATE_TITLE, title);
        setAdditionalCellValue(PDF_TEMPLATE_DISTRICT, distinct);
        setAdditionalCellValue(PDF_TEMPLATE_CHIEFDOM, chiefdom);
    }
}
