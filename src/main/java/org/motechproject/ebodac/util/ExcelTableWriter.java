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
        Row headerRow = sheet.createRow(XlsTemplate.INDEX_OF_HEADER_ROW);
        headerRow.setHeightInPoints(40);
        Cell headerCell;
        for (int i = 0; i < titles.length; i++) {
            headerCell = headerRow.createCell(i);
            headerCell.setCellValue(titles[i]);
            headerCell.setCellStyle(xlsTemplate.getCellStyleForName("header"));
            columnIndexMap.put(titles[i], i);
        }
    }

    @Override
    public void writeRow(Map<String, String> map, String[] strings) throws IOException {
        Sheet sheet = xlsTemplate.getSheet();
        Row row = sheet.createRow(XlsTemplate.INDEX_OF_FIRST_DATA_ROW + currentRowIndex);
        Cell dataCell;
        for(Map.Entry<String, String> entry : map.entrySet()) {
            Integer columnIndex = columnIndexMap.get(entry.getKey());
            if(columnIndex != null) {
                dataCell = row.createCell(columnIndex);
                dataCell.setCellValue(entry.getValue());
                dataCell.setCellStyle(xlsTemplate.getCellStyleForName("cell"));
            } else {
                throw new EbodacExportException("No such column: " + entry.getKey());
            }
        }
        currentRowIndex++;
    }

    @Override
    public void close() {
        Sheet sheet = xlsTemplate.getSheet();
        for (int i = 0; i < columnIndexMap.size(); i++) {
            sheet.autoSizeColumn(i);
        }
        try {
            xlsTemplate.getTemplateWorkbook().write(outputStream);
        } catch (IOException e) {
            throw new EbodacExportException(e.getMessage(), e);
        }
    }

}
