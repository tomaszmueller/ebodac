package org.motechproject.ebodac.dto;

import org.motechproject.ebodac.domain.EmailReport;

public class EmailReportDto {

    private EmailReport report;

    private Long oldEntityId;

    public EmailReportDto() {
    }

    public EmailReport getReport() {
        return report;
    }

    public void setReport(EmailReport report) {
        this.report = report;
    }

    public Long getOldEntityId() {
        return oldEntityId;
    }

    public void setOldEntityId(Long oldEntityId) {
        this.oldEntityId = oldEntityId;
    }
}
