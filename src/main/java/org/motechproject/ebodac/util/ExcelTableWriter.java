package org.motechproject.ebodac.util;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.motechproject.ebodac.exception.EbodacExportException;
import org.motechproject.mds.service.impl.csv.writer.TableWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class ExcelTableWriter implements TableWriter {

    private XlsTemplate xlsTemplate;

    private OutputStream outputStream;

    private Map<String, Integer> columnIndexMap;

    private int currentRowIndex;

    public ExcelTableWriter(OutputStream outputStream, XlsTemplate template) {
        xlsTemplate = template;
        this.outputStream = outputStream;
        currentRowIndex = 0;
    }

    @Override
    public void writeHeader(String[] titles) throws IOException {
        columnIndexMap = new HashMap<>();
        Sheet sheet = xlsTemplate.getSheet();
        Row headerRow = sheet.createRow(xlsTemplate.getIndexOfHeaderRow());
        headerRow.setHeightInPoints(40);
        Cell headerCell;
        for (int i = 0; i < titles.length; i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(titles[i]);
            headerCell.setCellStyle(xlsTemplate.getCellStyleForName("header"));
            columnIndexMap.put(titles[i], i);
            sheet.autoSizeColumn(i);
        }
    }

    @Override
    public void writeRow(Map<String, String> map, String[] strings) throws IOException {
        Sheet sheet = xlsTemplate.getSheet();
        Row row = sheet.createRow(xlsTemplate.getIndexOfFirstDataRow() + currentRowIndex);
        Cell dataCell;
        for(Map.Entry<String, String> entry : map.entrySet()) {
            Integer columnIndex = columnIndexMap.get(entry.getKey());
            if(columnIndex != null) {
                dataCell = row.createCell(columnIndex);
                dataCell.setCellValue(entry.getValue());
                dataCell.setCellStyle(xlsTemplate.getCellStyleForName("cell"));
                sheet.autoSizeColumn(columnIndex);
            } else {
                throw new EbodacExportException("No such column: " + entry.getKey());
            }
        }
        currentRowIndex++;
    }

    @Override
    public void close() {
        try {
            xlsTemplate.getTemplateWorkbook().write(outputStream);
        } catch (IOException e) {
            throw new EbodacExportException(e.getMessage(), e);
        }
    }

}
