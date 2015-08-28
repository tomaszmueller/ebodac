package org.motechproject.ebodac.util;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Element;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.ColumnText;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfStamper;
import org.apache.commons.lang.StringUtils;
import org.motechproject.mds.ex.csv.DataExportException;
import org.motechproject.mds.service.impl.csv.writer.TableWriter;

import java.io.IOException;
import java.util.Map;

public class TemplatedPdfTableWriter implements TableWriter {

    private PdfTemplate template;

    private PdfPTable dataTable;

    public TemplatedPdfTableWriter(PdfTemplate template) {
        this.template = template;
    }

    @Override
    public void writeRow(Map<String, String> row, String[] headers) throws IOException {
        if(this.dataTable == null) {
            this.writeHeader(headers);
        }
        String[] arr = headers;
        int len = headers.length;

        for(int i = 0; i < len; ++i) {
            String header = arr[i];
            String value = row.get(header);
            if(StringUtils.isBlank(value)) {
                value = "\n";
            }
            Paragraph paragraph = new Paragraph(value, PdfTemplate.TABLE_FONT);
            PdfPCell cell = new PdfPCell(paragraph);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            cell.setHorizontalAlignment(Element.ALIGN_CENTER);
            this.dataTable.addCell(cell);
        }
    }

    @Override
    public void writeHeader(String[] headers) throws IOException {
        this.dataTable = new PdfPTable(headers.length);
        this.dataTable.setWidthPercentage(100.0F);
        String[] arr = headers;
        int len = headers.length;

        for(int i = 0; i < len; ++i) {
            String header = arr[i];
            PdfPCell cell = new PdfPCell(new Paragraph(header, PdfTemplate.HEADER_FONT));
            cell.setBackgroundColor(BaseColor.GRAY);
            cell.setVerticalAlignment(Element.ALIGN_MIDDLE);
            this.dataTable.addCell(cell);
        }
    }

    @Override
    public void close() {
        try {
            ColumnText column = new ColumnText(template.getPdfStamper().getOverContent(1));
            column.setSimpleColumn(PdfTemplate.FIRST_PAGE_RECTANGLE);
            column.addElement(dataTable);
            int pageCount = 1;
            int status = column.go();
            while (ColumnText.hasMoreText(status)) {
                status = triggerNewPage(column, ++pageCount);
            }
            this.template.close();
        } catch (DocumentException ex) {
            throw new DataExportException("Unable to add a table to the PDF file", ex);
        }
    }

    private int triggerNewPage(ColumnText column, int pageCount) throws DocumentException {
        PdfStamper stamper = template.getPdfStamper();
        stamper.insertPage(pageCount, template.getTemplatePageSize());
        PdfContentByte canvas = stamper.getOverContent(pageCount);
        column.setCanvas(canvas);
        column.setSimpleColumn(PdfTemplate.NEXT_PAGE_RECTANGLE);
        return column.go();
    }
}
