package org.motechproject.ebodac.service.impl;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.ReportBoosterVaccination;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.ReportBoosterVaccinationDataService;
import org.motechproject.ebodac.repository.ReportPrimerVaccinationDataService;
import org.motechproject.ebodac.service.ReportService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

    private static final Logger LOGGER = LoggerFactory.getLogger(ReportServiceImpl.class);

    ReportPrimerVaccinationDataService primerVaccinationDataService;

    ReportBoosterVaccinationDataService boosterVaccinationDataService;

    @Override
    public void generateBoosterVaccinationReport(List<Subject> subjects, DateTime date) {
        DateTime age_6 = date.minusYears(6);
        DateTime age_12 = date.minusYears(12);
        DateTime age_18 = date.minusYears(18);

        int children_0_5 = 0;
        int children_6_11 = 0;
        int children_12_17 = 0;
        int adultMales = 0;
        int adultFemales = 0;

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
            } else {
                LOGGER.warn("Subject with id: {} has no gender", subject.getSubjectId());
            }
        }

        int peopleBoostered = children_0_5 + children_6_11 + children_12_17 + adultMales + adultFemales;

        ReportBoosterVaccination existingReport = boosterVaccinationDataService.findReportByDate(date);

        if (existingReport != null) {
            existingReport.updateReportData(adultMales, adultFemales, children_0_5, children_6_11, children_12_17, peopleBoostered);
            boosterVaccinationDataService.update(existingReport);

            LOGGER.debug("Booster Vaccination Daily Report for date: {} updated", date.toString(DateTimeFormat.mediumDate()));
        } else {
            ReportBoosterVaccination reportBoosterVaccination = new ReportBoosterVaccination(date, adultMales, adultFemales,
                    children_0_5, children_6_11, children_12_17, peopleBoostered);

            boosterVaccinationDataService.create(reportBoosterVaccination);

            LOGGER.debug("Booster Vaccination Daily Report for date: {} created", date.toString(DateTimeFormat.mediumDate()));
        }
    }

    @Override
    public void generatePrimerVaccinationReport(List<Subject> subjects, DateTime date) {
        DateTime age_6 = date.minusYears(6);
        DateTime age_12 = date.minusYears(12);
        DateTime age_18 = date.minusYears(18);

        int children_0_5 = 0;
        int children_6_11 = 0;
        int children_12_17 = 0;
        int adultMales = 0;
        int adultFemales = 0;

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
            } else {
                LOGGER.warn("Subject with id: {} has no gender", subject.getSubjectId());
            }
        }

        int peopleVaccinated = children_0_5 + children_6_11 + children_12_17 + adultMales + adultFemales;

        ReportPrimerVaccination existingReport = primerVaccinationDataService.findReportByDate(date);

        if (existingReport != null) {
            existingReport.updateReportData(adultMales, adultFemales, children_0_5, children_6_11, children_12_17, peopleVaccinated);
            primerVaccinationDataService.update(existingReport);

            LOGGER.debug("Primer Vaccination Daily Report for date: {} updated", date.toString(DateTimeFormat.mediumDate()));
        } else {
            ReportPrimerVaccination reportPrimerVaccination = new ReportPrimerVaccination(date, adultMales, adultFemales,
                    children_0_5, children_6_11, children_12_17, peopleVaccinated);

            primerVaccinationDataService.create(reportPrimerVaccination);

            LOGGER.debug("Primer Vaccination Daily Report for date: {} created", date.toString(DateTimeFormat.mediumDate()));
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
}
