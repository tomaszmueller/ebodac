package org.motechproject.ebodac.util;


import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Font;
import com.itextpdf.text.Rectangle;
import com.itextpdf.text.pdf.AcroFields;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;
import org.motechproject.ebodac.exception.EbodacExportException;

import java.io.IOException;
import java.io.OutputStream;

public class PdfTemplate {

    private PdfReader pdfReader;

    private PdfStamper pdfStamper;

    public static final Font TABLE_FONT = new Font(Font.FontFamily.HELVETICA, 8);

    public static final Font HEADER_FONT = new Font(Font.FontFamily.HELVETICA, 9, Font.BOLD);

    public static final Rectangle FIRST_PAGE_RECTANGLE = new Rectangle(20, 36, 580, 475);

    public static final Rectangle NEXT_PAGE_RECTANGLE = new Rectangle(20, 36, 580, 762);

    public PdfTemplate(String templatePath, OutputStream outputStream) {
        try {
            pdfReader = new PdfReader(getClass().getResourceAsStream(templatePath));
            pdfStamper = new PdfStamper(pdfReader, outputStream);
        } catch (DocumentException | IOException e) {
            throw new EbodacExportException(e.getMessage(), e);
        }
    }

    public void setAdditionalCellValue(String cellName, String cellValue) {
        AcroFields form = pdfStamper.getAcroFields();
        try {
            form.setField(cellName, cellValue);
        } catch (IOException | DocumentException e) {
            throw new EbodacExportException("No such additional cell: " + cellName, e);
        }
    }

    public PdfStamper getPdfStamper() {
        return this.pdfStamper;
    }

    public Rectangle getTemplatePageSize() {
        return pdfReader.getPageSize(1);
    }

    public void close() {
        try {
            pdfStamper.close();
            pdfReader.close();
        } catch (DocumentException | IOException e) {
            throw new EbodacExportException(e.getMessage(), e);
        }
    }
}
