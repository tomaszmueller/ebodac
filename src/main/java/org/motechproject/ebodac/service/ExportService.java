package org.motechproject.ebodac.service;


import org.motechproject.mds.query.QueryParams;

import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;

public interface ExportService {

    void exportDailyClinicVisitScheduleReportToPDF(OutputStream outputStream, String lookup,
                                                   String lookupFields, QueryParams queryParams)
            throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    void exportDailyClinicVisitScheduleReportToPDF(OutputStream outputStream)
            throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    void exportDailyClinicVisitScheduleReportToCSV(Writer writer, String lookup,
                                                   String lookupFields, QueryParams queryParams)
            throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;

    void exportDailyClinicVisitScheduleReportToCSV(Writer writer)
            throws IOException, NoSuchMethodException, IllegalAccessException, InvocationTargetException;
}
