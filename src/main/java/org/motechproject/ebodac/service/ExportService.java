package org.motechproject.ebodac.service;


import org.motechproject.mds.query.QueryParams;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.util.Map;

public interface ExportService {

    void  exportEntityToPDF(OutputStream outputStream, Class<?> entityType, Map<String, String> headerMap,
                                                   String lookup, String lookupFields, QueryParams queryParams) throws IOException;

    void  exportEntityToCSV(Writer writer, Class<?> entityType, Map<String, String> headerMap,
                                                   String lookup, String lookupFields, QueryParams queryParams) throws IOException;

    void  exportEntityToPDF(OutputStream outputStream, Class<?> entityDtoType, Class<?> entityType, Map<String, String> headerMap,
                            String lookup, String lookupFields, QueryParams queryParams) throws IOException;

    void  exportEntityToCSV(Writer writer, Class<?> entityDtoType, Class<?> entityType, Map<String, String> headerMap,
                            String lookup, String lookupFields, QueryParams queryParams) throws IOException;
}
