package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ebodac.client.EbodacEmailClient;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.EbodacEntity;
import org.motechproject.ebodac.domain.EbodacEntityField;
import org.motechproject.ebodac.domain.EmailRecipient;
import org.motechproject.ebodac.domain.EmailReport;
import org.motechproject.ebodac.dto.EmailReportDto;
import org.motechproject.ebodac.domain.enums.EmailSchedulePeriod;
import org.motechproject.ebodac.repository.EbodacEntityDataService;
import org.motechproject.ebodac.repository.EbodacEntityFieldDataService;
import org.motechproject.ebodac.repository.EmailRecipientDataService;
import org.motechproject.ebodac.repository.EmailReportDataService;
import org.motechproject.ebodac.scheduler.EbodacScheduler;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.EmailReportService;
import org.motechproject.ebodac.service.ExportService;
import org.motechproject.scheduler.exception.MotechSchedulerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.activation.DataSource;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service("emailReportService")
public class EmailReportServiceImpl implements EmailReportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(EmailReportServiceImpl.class);

    @Autowired
    private EmailReportDataService emailReportDataService;

    @Autowired
    private EmailRecipientDataService emailRecipientDataService;

    @Autowired
    private EbodacEntityDataService ebodacEntityDataService;

    @Autowired
    private EbodacEntityFieldDataService ebodacEntityFieldDataService;

    @Autowired
    private EbodacEmailClient ebodacEmailClient;

    @Autowired
    private ConfigService configService;

    @Autowired
    private ExportService exportService;

    @Autowired
    private EbodacScheduler ebodacScheduler;

    @Override
    public List<EmailReport> getEmailReports() {
        return emailReportDataService.retrieveAll();
    }

    @Override
    public EmailReport saveReport(EmailReportDto reportDto) {
        DateTime startDate = null;
        EmailReport report = reportDto.getReport();
        EmailSchedulePeriod period = report.getSchedulePeriod();

        if (EmailSchedulePeriod.DAILY.equals(period)) {
            startDate = report.getScheduleTime().toDateTime(LocalDate.now());
        } else if (EmailSchedulePeriod.WEEKLY.equals(period)) {
            LocalDate date = LocalDate.now().withDayOfWeek(report.getDayOfWeek().getValue());
            startDate = report.getScheduleTime().toDateTime(date);
        } else {
            startDate = report.getScheduleTime().toDateTime(calculateDate(report.getDayOfWeek().getValue()));
        }

        if (startDate.isBeforeNow()) {
            startDate = startDate.plus(period.getPeriod());
        }

        List<EmailRecipient> recipients = new ArrayList<>();

        for (EmailRecipient r : report.getRecipients()) {
            EmailRecipient recipient = emailRecipientDataService.findById(r.getId());
            if (recipient != null) {
                recipients.add(recipient);
            }
        }

        report.setRecipients(recipients);
        report.getEntity().setId(null);

        for (EbodacEntityField field : report.getEntity().getFields()) {
            field.setId(null);
        }

        if (report.getId() == null) {
            emailReportDataService.create(report);

            try {
                ebodacScheduler.scheduleEmailReportJob(startDate, period.getPeriod(), report.getId());
            } catch (RuntimeException e) {
                emailReportDataService.delete(report);
                throw new MotechSchedulerException(e);
            }
        } else {
            ebodacScheduler.rescheduleEmailReportJob(startDate, period.getPeriod(), report.getId());
            emailReportDataService.update(report);
        }

        deleteEntity(reportDto.getOldEntityId());

        return report;
    }

    @Override
    public void deleteReport(Long reportId) {
        EmailReport report = emailReportDataService.findById(reportId);

        if (report == null) {
            throw new IllegalArgumentException("Cannot find report with id: " + reportId);
        } else {
            Long entityId = report.getEntity().getId();
            List<EbodacEntityField> fields = report.getEntity().getFields();

            ebodacScheduler.unscheduleEmailReportJob(reportId);
            emailReportDataService.delete(report);

            deleteEntityAndEntityFields(entityId, fields);
        }
    }

    @Override
    public void sendEmailReport(Long reportId) {
        Config config = configService.getConfig();
        EmailReport emailReport = emailReportDataService.findById(reportId);

        if (emailReport == null) {
            LOGGER.error("Could not send email for report with id: {}, because no report with this id found", reportId);
        } else {
            try {
                List<String> recipients = new ArrayList<>();
                DataSource source = null;

                if (!emailReport.getEntity().getFields().isEmpty()) {
                    Map<String, String> headerMap = new HashMap<>();

                    for (EbodacEntityField field : emailReport.getEntity().getFields()) {
                        headerMap.put(field.isRelationField() ? field.getRelatedFieldDisplayName() : field.getDisplayName(), field.getFieldPath());
                    }

                    ByteArrayOutputStream out = new ByteArrayOutputStream();
                    Writer writer = new OutputStreamWriter(out);

                    exportService.exportEntityToCSV(writer, emailReport.getEntity().getClassName(), headerMap, null, null, null);
                    source = new ByteArrayDataSource(out.toByteArray(), "text/csv");
                }

                for (EmailRecipient recipient : emailReport.getRecipients()) {
                    recipients.add(recipient.getEmailAddress());
                }

                DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("yyyyMMddHHmmss");
                String fileName = (StringUtils.isNotBlank(emailReport.getName()) ? emailReport.getName() : emailReport.getEntity().getName())
                        + "_" + DateTime.now().toString(dateTimeFormatter) + ".csv";

                ebodacEmailClient.sendNewMessage(config.getEmailReportHost(), config.getEmailReportAddress(), config.getEmailReportPassword(),
                        config.getEmailReportPort(), emailReport.getSubject(), recipients, emailReport.getMessageContent(), source, fileName);
            } catch (IllegalArgumentException e) {
                LOGGER.error("Could not send email for report with id: {}, because of wrong report data: {}", reportId, e.getMessage(), e);
            } catch (Exception e) {
                LOGGER.error("Could not send email for report with id: {}, because exception occurred when generating report: {}", reportId, e.getMessage(), e);
            }
        }
    }

    private void deleteEntity(Long id) {
        if (id != null) {
            EbodacEntity entity = ebodacEntityDataService.findById(id);

            if (entity != null) {
                List<EbodacEntityField> fields = entity.getFields();

                ebodacEntityDataService.delete(entity);

                if (fields != null) {
                    for (EbodacEntityField field : fields) {
                        EbodacEntityField entityField = ebodacEntityFieldDataService.findById(field.getId());
                        if (entityField != null) {
                            ebodacEntityFieldDataService.delete(entityField);
                        }
                    }
                }
            }
        }
    }

    private void deleteEntityAndEntityFields(Long entityId, List<EbodacEntityField> fields) {
        if (entityId != null) {
            EbodacEntity entity = ebodacEntityDataService.findById(entityId);

            if (entity != null) {
                ebodacEntityDataService.delete(entity);
            }
        }

        if (fields != null) {
            for (EbodacEntityField field : fields) {
                EbodacEntityField entityField = ebodacEntityFieldDataService.findById(field.getId());

                if (entityField != null) {
                    ebodacEntityFieldDataService.delete(entityField);
                }
            }
        }
    }

    private LocalDate calculateDate(int dayOfWeek) {
        LocalDate date = LocalDate.now();
        int dayOffset = dayOfWeek - date.getDayOfWeek();

        if (date.getDayOfMonth() + dayOffset > 7) {
            date = date.withDayOfMonth(1).plusMonths(1);
            dayOffset = dayOfWeek - date.getDayOfWeek();
        }

        if (date.getDayOfMonth() + dayOffset <= 0) {
            dayOffset += 7;
        }

        return date.plusDays(dayOffset);
    }
}
