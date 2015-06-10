package org.motechproject.ebodac.service;

import org.joda.time.DateTime;
import org.motechproject.ebodac.domain.Subject;

import java.util.List;

public interface ReportService {

    void generateBoosterVaccinationReport(List<Subject> subjects, DateTime date);

    void generatePrimerVaccinationReport(List<Subject> subjects, DateTime date);
}
