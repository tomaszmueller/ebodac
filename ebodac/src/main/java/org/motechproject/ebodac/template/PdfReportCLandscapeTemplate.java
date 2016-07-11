package org.motechproject.ebodac.template;

import com.itextpdf.text.Rectangle;

import java.io.OutputStream;

import static com.itextpdf.text.PageSize.A4;

public class PdfReportCLandscapeTemplate extends PdfBasicTemplate {

    private static final String TEMPLATE_PATH = "/templates/report_templateC_landscape.pdf";
    private static final float MARGIN = 5;
    private static final Rectangle PAGE_RECTANGLE = new Rectangle(MARGIN, MARGIN, A4.getHeight() - MARGIN, A4.getWidth() - MARGIN);

    public PdfReportCLandscapeTemplate(OutputStream outputStream) {
        super(TEMPLATE_PATH, PAGE_RECTANGLE, outputStream);
    }

    @Override
    public Rectangle getNextPageRectangle() {
        return PAGE_RECTANGLE;
    }
}
