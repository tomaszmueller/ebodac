package org.motechproject.ebodac.service.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.repository.VisitDataService;
import org.motechproject.ebodac.service.ExportService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.MotechDataService;
import org.motechproject.mds.service.impl.csv.writer.CsvTableWriter;
import org.motechproject.mds.service.impl.csv.writer.PdfTableWriter;
import org.motechproject.mds.service.impl.csv.writer.TableWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("exportService")
public class ExportServiceImpl implements ExportService {

    @Autowired
    private LookupService lookupService;

    @Autowired
    private VisitDataService visitDataService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void exportDailyClinicVisitScheduleReportToPDF(OutputStream outputStream, String lookup,
                                                          String lookupFields, QueryParams queryParams)
            throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        PdfTableWriter tableWriter = new PdfTableWriter(outputStream);
        exportReport(visitDataService, EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_MAP,
                tableWriter, lookup, lookupFields, queryParams);
    }

    @Override
    public void exportDailyClinicVisitScheduleReportToPDF(OutputStream outputStream)
            throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.exportDailyClinicVisitScheduleReportToPDF(outputStream, null, null, null);
    }

    @Override
    public void exportDailyClinicVisitScheduleReportToCSV(Writer writer, String lookup,
                                                          String lookupFields, QueryParams queryParams)
            throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        CsvTableWriter tableWriter = new CsvTableWriter(writer);
        exportReport(visitDataService, EbodacConstants.DAILY_CLINIC_VISIT_SCHEDULE_REPORT_MAP,
                tableWriter, lookup, lookupFields, queryParams);
    }

    @Override
    public void exportDailyClinicVisitScheduleReportToCSV(Writer writer)
            throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        this.exportDailyClinicVisitScheduleReportToCSV(writer, null, null, null);
    }

    private <T> void exportReport(MotechDataService<T> dataService, Map<String, String> headerMap, TableWriter tableWriter,String lookup,
                                                      String lookupFields, QueryParams queryParams)
            throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        Records<T> records = lookupService.getEntities(dataService, lookup, lookupFields, queryParams);
        List<T> entities = records.getRows();
        Set<String> keys = headerMap.keySet();
        String[] fields = keys.toArray(new String[keys.size()]);
        try {
            tableWriter.writeHeader(fields);
            for (T entity : entities) {
                Map<String, String> row = buildRow(entity, headerMap);
                tableWriter.writeRow(row,fields);
            }
        } catch (IOException e) {
            throw new IOException("IO Error when writing data", e);
        } finally {
            tableWriter.close();
        }
    }

    private <T> Map<String, String> buildRow(T entity, Map<String, String> headerMap) throws IOException {
        String json = objectMapper.writeValueAsString(entity);
        Map<String, Object> visitMap = objectMapper.readValue(json, new TypeReference<HashMap>() {});
        Map<String, Object> subjectMap = (Map<String, Object>)visitMap.get("subject");
        Map<String, String> row = new LinkedHashMap<>();

        for(Map.Entry<String, String> entry : headerMap.entrySet()) {
            String value = (String)visitMap.get(entry.getValue());
            if (value == null) {
                value = (String)subjectMap.get(entry.getValue());
            }
            row.put(entry.getKey(), value);
        }
        return  row;
    }
}
