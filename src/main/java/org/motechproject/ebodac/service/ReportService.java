package org.motechproject.ebodac.service;

import org.joda.time.DateTime;

public interface ReportService {

    void generateDailyReports();

    void generateDailyReportsFromDate(DateTime startDate);

    void shouldGenerateReports();
}
