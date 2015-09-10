package org.motechproject.ebodac.util;


import org.apache.commons.lang.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.PrintSetup;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.CellReference;
import org.motechproject.ebodac.exception.EbodacExportException;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class XlsTemplate {

    private Map<String, CellStyle> styles;
    
    private Workbook workbook;

    private Sheet sheet;

    public static final int INDEX_OF_HEADER_ROW = 15;

    public static final int INDEX_OF_FIRST_DATA_ROW = 16;

    private static final Map<String, String> CELL_ADDRESS_OF_ADDITIONAL_INFO =
            Collections.unmodifiableMap(new HashMap<String, String>() {
                {
                    put("title",    "B10");
                    put("district", "A14");
                    put("chiefdom", "C14");
                }
            });

    public XlsTemplate(String templatePath) {
        try {
           workbook = new HSSFWorkbook(getClass().getResourceAsStream(templatePath));
        } catch (IOException e) {
            throw new EbodacExportException(e.getMessage(), e);
        }
        setStyleMap();
        sheet = workbook.getSheetAt(0);
        PrintSetup printSetup = sheet.getPrintSetup();
        printSetup.setLandscape(true);
        sheet.setFitToPage(true);
        sheet.setHorizontallyCenter(true);
    }
    
    public String getCellAddressForAdditionalInfo(String additionalInfoName) {
        return CELL_ADDRESS_OF_ADDITIONAL_INFO.get(additionalInfoName);
    }
    
    public CellStyle getCellStyleForName(String styleName) {
        return styles.get(styleName);
    }
    
    public Workbook getTemplateWorkbook() {
        return workbook;
    }

    public Sheet getSheet() {
        return sheet;
    }
    
    public void setAdditionalCellValue(String cellName, String cellValue) {
        String cellAddress = CELL_ADDRESS_OF_ADDITIONAL_INFO.get(cellName);
        if(StringUtils.isNotBlank(cellAddress)) {
            CellReference cr = new CellReference(cellAddress);
            Row row = sheet.getRow(cr.getRow());
            Cell cell = row.getCell(cr.getCol());
            cell.setCellValue(cellValue);
            sheet.autoSizeColumn(cr.getCol());
        } else {
            throw new EbodacExportException("No such additional cell: " + cellName);
        }
    }

    private void setStyleMap() {
        styles = new HashMap<>();
        CellStyle style;
        Font monthFont = workbook.createFont();
        monthFont.setFontHeightInPoints((short) 11);
        monthFont.setBoldweight(Font.BOLDWEIGHT_BOLD);
        style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setVerticalAlignment(CellStyle.VERTICAL_CENTER);
        style.setFillForegroundColor(IndexedColors.GREY_50_PERCENT.getIndex());
        style.setFillPattern(CellStyle.SOLID_FOREGROUND);
        style.setFont(monthFont);
        style.setWrapText(true);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styles.put("header", style);

        style = workbook.createCellStyle();
        style.setAlignment(CellStyle.ALIGN_CENTER);
        style.setWrapText(true);
        style.setBorderRight(CellStyle.BORDER_THIN);
        style.setRightBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderLeft(CellStyle.BORDER_THIN);
        style.setLeftBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderTop(CellStyle.BORDER_THIN);
        style.setTopBorderColor(IndexedColors.BLACK.getIndex());
        style.setBorderBottom(CellStyle.BORDER_THIN);
        style.setBottomBorderColor(IndexedColors.BLACK.getIndex());
        styles.put("cell", style);
    }
}
