package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.commons.date.util.DateUtil;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.ReportBoosterVaccination;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.ReportBoosterVaccinationDataService;
import org.motechproject.ebodac.repository.ReportPrimerVaccinationDataService;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.ReportService;
import org.motechproject.ebodac.service.ReportUpdateService;
import org.motechproject.ebodac.service.SubjectService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

    private ReportPrimerVaccinationDataService primerVaccinationDataService;

    private ReportBoosterVaccinationDataService boosterVaccinationDataService;

    private ConfigService configService;

    private SubjectService subjectService;

    private ReportUpdateService reportUpdateService;

    @Override
    public void generateDailyReports() {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

        Config config = configService.getConfig();

        if (config.getGenerateReports() != null && config.getGenerateReports()) {
            String lastCalculationDate = config.getLastCalculationDate();
            String calculationStartDate = config.getFirstCalculationStartDate();

            LocalDate startDate;

            if (StringUtils.isNotBlank(lastCalculationDate)) {
                startDate = LocalDate.parse(lastCalculationDate, formatter).plusDays(1);
            } else if (StringUtils.isNotBlank(calculationStartDate)) {
                startDate = LocalDate.parse(calculationStartDate, formatter);
            } else {
                startDate = subjectService.findOldestPrimerVaccinationDate();
            }

            updateBoosterVaccinationReportsForDates(reportUpdateService.getBoosterVaccinationReportsToUpdate());
            updatePrimerVaccinationReportsForDates(reportUpdateService.getPrimerVaccinationReportsToUpdate());

            generateDailyReportsFromDate(startDate);

            config = configService.getConfig();
            config.setGenerateReports(false);
            config.setLastCalculationDate(DateUtil.now().minusDays(1).toString(formatter));
            configService.updateConfig(config);
        }
    }

    @Override
    public void generateDailyReportsFromDate(LocalDate startDate) {
        LocalDate now = DateUtil.now().toLocalDate();

        for(LocalDate date = startDate; date.isBefore(now); date = date.plusDays(1)) {
            generateOrUpdatePrimerVaccinationReport(subjectService.findSubjectsPrimerVaccinatedAtDay(date), date);
            generateOrUpdateBoosterVaccinationReport(subjectService.findSubjectsBoosterVaccinatedAtDay(date), date);
        }
    }

    @Override
    public void shouldGenerateReports() {
        Config config = configService.getConfig();

        config.setGenerateReports(true);
        configService.updateConfig(config);
    }

    private void updateBoosterVaccinationReportsForDates(Set<String> dates) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

        for (String dateString : dates) {
            LocalDate date = LocalDate.parse(dateString, formatter);
            generateOrUpdateBoosterVaccinationReport(subjectService.findSubjectsBoosterVaccinatedAtDay(date), date);
        }
    }

    private void updatePrimerVaccinationReportsForDates(Set<String> dates) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

        for (String dateString : dates) {
            LocalDate date = LocalDate.parse(dateString, formatter);
            generateOrUpdatePrimerVaccinationReport(subjectService.findSubjectsPrimerVaccinatedAtDay(date), date);
        }
    }

    private void generateOrUpdateBoosterVaccinationReport(List<Subject> subjects, LocalDate date) {
        LocalDate age_6 = date.minusYears(6);
        LocalDate age_12 = date.minusYears(12);
        LocalDate age_18 = date.minusYears(18);

        int children_0_5 = 0;
        int children_6_11 = 0;
        int children_12_17 = 0;
        int adultMales = 0;
        int adultFemales = 0;
        int adultUndifferentiated = 0;
        int adultUnidentified = 0;

        for (Subject subject : subjects) {
            if (subject.getDateOfBirth() == null) {
                LOGGER.warn("Subject with id: {} has no birth date", subject.getSubjectId());
            } else if (subject.getDateOfBirth().isAfter(age_6)) {
                children_0_5++;
            } else if (subject.getDateOfBirth().isAfter(age_12)) {
                children_6_11++;
            } else if (subject.getDateOfBirth().isAfter(age_18)) {
                children_12_17++;
            } else if (Gender.Male.equals(subject.getGender())) {
                adultMales++;
            } else if (Gender.Female.equals(subject.getGender())) {
                adultFemales++;
            } else if (Gender.Undifferentiated.equals(subject.getGender())) {
                adultUndifferentiated++;
            } else {
                adultUnidentified++;
            }
        }

        int peopleBoostered = children_0_5 + children_6_11 + children_12_17 + adultMales + adultFemales;

        ReportBoosterVaccination existingReport = boosterVaccinationDataService.findReportByDate(date);

        if (existingReport != null) {
            existingReport.updateReportData(adultMales, adultFemales, children_0_5, children_6_11, children_12_17,
                    adultUnidentified, adultUndifferentiated, peopleBoostered);
            boosterVaccinationDataService.update(existingReport);

            LOGGER.debug("Booster Vaccination Daily Report for date: {} updated",
                    date.toString(DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT)));
        } else {
            ReportBoosterVaccination reportBoosterVaccination = new ReportBoosterVaccination(date, adultMales, adultFemales,
                    children_0_5, children_6_11, children_12_17, adultUnidentified, adultUndifferentiated, peopleBoostered);

            boosterVaccinationDataService.create(reportBoosterVaccination);

            LOGGER.debug("Booster Vaccination Daily Report for date: {} created",
                    date.toString(DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT)));
        }
    }

    private void generateOrUpdatePrimerVaccinationReport(List<Subject> subjects, LocalDate date) {
        LocalDate age_6 = date.minusYears(6);
        LocalDate age_12 = date.minusYears(12);
        LocalDate age_18 = date.minusYears(18);

        int children_0_5 = 0;
        int children_6_11 = 0;
        int children_12_17 = 0;
        int adultMales = 0;
        int adultFemales = 0;
        int adultUndifferentiated = 0;
        int adultUnidentified = 0;

        for (Subject subject : subjects) {
            if (subject.getDateOfBirth() == null) {
                LOGGER.warn("Subject with id: {} has no birth date", subject.getSubjectId());
            } else if (subject.getDateOfBirth().isAfter(age_6)) {
                children_0_5++;
            } else if (subject.getDateOfBirth().isAfter(age_12)) {
                children_6_11++;
            } else if (subject.getDateOfBirth().isAfter(age_18)) {
                children_12_17++;
            } else if (Gender.Male.equals(subject.getGender())) {
                adultMales++;
            } else if (Gender.Female.equals(subject.getGender())) {
                adultFemales++;
            } else if (Gender.Undifferentiated.equals(subject.getGender())) {
                adultUndifferentiated++;
            } else {
                adultUnidentified++;
            }
        }

        int peopleVaccinated = children_0_5 + children_6_11 + children_12_17 + adultMales + adultFemales;

        ReportPrimerVaccination existingReport = primerVaccinationDataService.findReportByDate(date);

        if (existingReport != null) {
            existingReport.updateReportData(adultMales, adultFemales, children_0_5, children_6_11, children_12_17,
                    adultUnidentified, adultUndifferentiated, peopleVaccinated);
            primerVaccinationDataService.update(existingReport);

            LOGGER.debug("Primer Vaccination Daily Report for date: {} updated",
                    date.toString(DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT)));
        } else {
            ReportPrimerVaccination reportPrimerVaccination = new ReportPrimerVaccination(date, adultMales, adultFemales,
                    children_0_5, children_6_11, children_12_17, adultUnidentified, adultUndifferentiated, peopleVaccinated);

            primerVaccinationDataService.create(reportPrimerVaccination);

            LOGGER.debug("Primer Vaccination Daily Report for date: {} created",
                    date.toString(DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT)));
        }
    }

    @Autowired
    public void setPrimerVaccinationDataService(ReportPrimerVaccinationDataService primerVaccinationDataService) {
        this.primerVaccinationDataService = primerVaccinationDataService;
    }

    @Autowired
    public void setBoosterVaccinationDataService(ReportBoosterVaccinationDataService boosterVaccinationDataService) {
        this.boosterVaccinationDataService = boosterVaccinationDataService;
    }

    @Autowired
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }

    @Autowired
    public void setSubjectService(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @Autowired
    public void setReportUpdateService(ReportUpdateService reportUpdateService) {
        this.reportUpdateService = reportUpdateService;
    }
}
