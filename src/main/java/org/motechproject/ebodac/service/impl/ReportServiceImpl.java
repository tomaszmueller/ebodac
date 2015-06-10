package org.motechproject.ebodac.service.impl;

import org.joda.time.DateTime;
import org.motechproject.ebodac.domain.Gender;
import org.motechproject.ebodac.domain.ReportBoosterVaccination;
import org.motechproject.ebodac.domain.ReportPrimerVaccination;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.repository.ReportBoosterVaccinationDataService;
import org.motechproject.ebodac.repository.ReportPrimerVaccinationDataService;
import org.motechproject.ebodac.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("reportService")
public class ReportServiceImpl implements ReportService {

    ReportPrimerVaccinationDataService primerVaccinationDataService;

    ReportBoosterVaccinationDataService boosterVaccinationDataService;

    @Override
    public void generateBoosterVaccinationReport(List<Subject> subjects, DateTime date) {
        DateTime now = DateTime.now();
        DateTime age_6 = now.minusYears(6);
        DateTime age_12 = now.minusYears(12);
        DateTime age_18 = now.minusYears(18);

        int children_0_5 = 0;
        int children_6_11 = 0;
        int children_12_17 = 0;
        int adultMales = 0;
        int adultFemales = 0;

        for (Subject subject : subjects) {
            if (subject.getDateOfBirth().isAfter(age_6)) {
                children_0_5++;
            } else if (subject.getDateOfBirth().isAfter(age_12)) {
                children_6_11++;
            } else if (subject.getDateOfBirth().isAfter(age_18)) {
                children_12_17++;
            } else {
                if (subject.getGender().equals(Gender.Male)) {
                    adultMales++;
                } else {
                    adultFemales++;
                }
            }
        }

        ReportBoosterVaccination reportBoosterVaccination = new ReportBoosterVaccination(date, adultMales, adultFemales,
                children_0_5, children_6_11, children_12_17, subjects.size());

        boosterVaccinationDataService.create(reportBoosterVaccination);
    }

    @Override
    public void generatePrimerVaccinationReport(List<Subject> subjects, DateTime date) {
        DateTime now = DateTime.now();
        DateTime age_6 = now.minusYears(6);
        DateTime age_12 = now.minusYears(12);
        DateTime age_18 = now.minusYears(18);

        int children_0_5 = 0;
        int children_6_11 = 0;
        int children_12_17 = 0;
        int adultMales = 0;
        int adultFemales = 0;

        for (Subject subject : subjects) {
            if (subject.getDateOfBirth().isAfter(age_6)) {
                children_0_5++;
            } else if (subject.getDateOfBirth().isAfter(age_12)) {
                children_6_11++;
            } else if (subject.getDateOfBirth().isAfter(age_18)) {
                children_12_17++;
            } else {
                if (subject.getGender().equals(Gender.Male)) {
                    adultMales++;
                } else {
                    adultFemales++;
                }
            }
        }

        ReportPrimerVaccination reportPrimerVaccination = new ReportPrimerVaccination(date, adultMales, adultFemales,
                children_0_5, children_6_11, children_12_17, subjects.size());

        primerVaccinationDataService.create(reportPrimerVaccination);
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
