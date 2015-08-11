package org.motechproject.ebodac.service.impl;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.motechproject.ebodac.service.ExportService;
import org.motechproject.ebodac.service.LookupService;
import org.motechproject.ebodac.web.domain.Records;
import org.motechproject.mds.query.QueryParams;
import org.motechproject.mds.service.impl.csv.writer.CsvTableWriter;
import org.motechproject.mds.service.impl.csv.writer.PdfTableWriter;
import org.motechproject.mds.service.impl.csv.writer.TableWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service("exportService")
public class ExportServiceImpl implements ExportService {

    @Autowired
    private LookupService lookupService;

    private ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public void exportEntityToPDF(OutputStream outputStream, Class<?> entityType, Map<String, String> headerMap,
                                                          String lookup, String lookupFields, QueryParams queryParams) throws IOException {
        PdfTableWriter tableWriter = new PdfTableWriter(outputStream);
        exportReport(entityType, headerMap, tableWriter, lookup, lookupFields, queryParams);
    }

    @Override
    public void  exportEntityToCSV(Writer writer, Class<?> entityType, Map<String, String> headerMap,
                                                          String lookup, String lookupFields, QueryParams queryParams) throws IOException {
        CsvTableWriter tableWriter = new CsvTableWriter(writer);
        exportReport(entityType, headerMap, tableWriter, lookup, lookupFields, queryParams);
    }

    private <T> void exportReport(Class<T> entityType, Map<String, String> headerMap, TableWriter tableWriter,String lookup,
                                                      String lookupFields, QueryParams queryParams) throws IOException {
        Records<T> records = lookupService.getEntities(entityType, lookup, lookupFields, queryParams);
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
        Map<String, Object> entityMap = objectMapper.readValue(json, new TypeReference<HashMap>() {});
        List<Map<String, Object>> entityMapList = getEntityMapList(entityMap);
        Map<String, String> row = new LinkedHashMap<>();

        for(Map.Entry<String, String> entry : headerMap.entrySet()) {
            String value = (String)entityMap.get(entry.getValue());
            if (value == null) {
                for(Map<String, Object> map : entityMapList) {
                    value = (String) map.get(entry.getValue());
                    if(value != null) {
                        break;
                    }
                }
            }
            row.put(entry.getKey(), value);
        }
        return  row;
    }

    private List<Map<String, Object>> getEntityMapList(Map<String, Object> entityMap) {
        List<Map<String, Object>> entityMapList = new ArrayList<>();
        for(Map.Entry<String, Object> entry : entityMap.entrySet()) {
            if(entry.getValue() instanceof Map) {
                Map<String, Object> object = null;
                try {
                    object = (Map<String, Object>) entry.getValue();
                } catch (ClassCastException e) {
                    continue;
                }
                entityMapList.add(object);
            }
        }
        return  entityMapList;
    }
}
