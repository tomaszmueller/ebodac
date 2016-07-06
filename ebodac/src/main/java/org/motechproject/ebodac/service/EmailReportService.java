package org.motechproject.ebodac.service;

import org.motechproject.ebodac.domain.EmailReport;
import org.motechproject.ebodac.domain.enums.EmailReportStatus;
import org.motechproject.ebodac.dto.EmailReportDto;

import java.util.List;

public interface EmailReportService {

    List<EmailReport> getEmailReports();

    EmailReport saveReport(EmailReportDto reportDto);

    void deleteReport(Long reportId);

    void sendEmailReport(Long reportId);

    EmailReportStatus enableReport(Long reportId);

    EmailReportStatus disableReport(Long reportId);
}
