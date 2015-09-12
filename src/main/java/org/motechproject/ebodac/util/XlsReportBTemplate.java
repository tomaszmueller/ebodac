package org.motechproject.ebodac.util;

import java.io.OutputStream;

public class XlsReportBTemplate extends XlsBasicTemplate {

    private static final String XLS_TEMPLATE_PATH = "/report_templateB.xls";
    private static final int INDEX_OF_HEADER_ROW = 15;

    private static final String XLS_TEMPLATE_TITLE = "B10";
    private static final String XLS_TEMPLATE_DISTRICT = "A14";
    private static final String XLS_TEMPLATE_CHIEFDOM = "C14";

    public XlsReportBTemplate(OutputStream outputStream) {
        super(XLS_TEMPLATE_PATH, INDEX_OF_HEADER_ROW, outputStream);
    }

    public void setAdditionalCellValues(String title, String  distinct, String chiefdom) {
        setAdditionalCellValue(XLS_TEMPLATE_TITLE, title);
        setAdditionalCellValue(XLS_TEMPLATE_DISTRICT, distinct);
        setAdditionalCellValue(XLS_TEMPLATE_CHIEFDOM, chiefdom);
    }
}
