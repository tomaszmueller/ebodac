package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.motechproject.ebodac.constants.EbodacConstants;
import org.motechproject.ebodac.domain.Config;
import org.motechproject.ebodac.domain.Subject;
import org.motechproject.ebodac.service.ConfigService;
import org.motechproject.ebodac.service.ReportUpdateService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service("reportUpdateService")
public class ReportUpdateServiceImpl implements ReportUpdateService {

    private ConfigService configService;

    @Override
    public Set<String> getPrimerVaccinationReportsToUpdate() {
        Config config = configService.getConfig();

        Set<String> reportsToUpdate = config.getPrimerVaccinationReportsToUpdate();
        config.setPrimerVaccinationReportsToUpdate(null);

        configService.updateConfig(config);

        return reportsToUpdate;
    }

    @Override
    public Set<String> getBoosterVaccinationReportsToUpdate() {
        Config config = configService.getConfig();

        Set<String> reportsToUpdate = config.getBoosterVaccinationReportsToUpdate();
        config.setBoosterVaccinationReportsToUpdate(null);

        configService.updateConfig(config);

        return reportsToUpdate;
    }

    @Override
    public void addReportsToUpdateIfNeeded(Subject oldSubject, Subject newSubject) {
        DateTimeFormatter formatter = DateTimeFormat.forPattern(EbodacConstants.REPORT_DATE_FORMAT);

        Config config = configService.getConfig();
        String lastReportDateString = config.getLastReportDate();

        if (StringUtils.isNotBlank(lastReportDateString)) {
            DateTime lastReportDate = formatter.parseDateTime(config.getLastReportDate());

            DateTime newPrimerVaccinationDate = newSubject.getPrimerVaccinationDate();
            DateTime oldPrimerVaccinationDate = null;
            if (oldSubject != null) {
                oldPrimerVaccinationDate = oldSubject.getPrimerVaccinationDate();
            }

            if (oldPrimerVaccinationDate != null && !oldPrimerVaccinationDate.isEqual(newPrimerVaccinationDate)
                    && oldPrimerVaccinationDate.isBefore(lastReportDate.plusDays(1))) {

                config.getPrimerVaccinationReportsToUpdate().add(oldPrimerVaccinationDate.toString(formatter));
            }
            if (newPrimerVaccinationDate != null && newPrimerVaccinationDate.isBefore(lastReportDate.plusDays(1))
                    && (!newPrimerVaccinationDate.isEqual(oldPrimerVaccinationDate) || reportRelevantDataChanged(oldSubject, newSubject))) {

                config.getPrimerVaccinationReportsToUpdate().add(newPrimerVaccinationDate.toString(formatter));
            }

            DateTime newBoosterVaccinationDate = newSubject.getBoosterVaccinationDate();
            DateTime oldBoosterVaccinationDate = null;
            if (oldSubject != null) {
                oldBoosterVaccinationDate = oldSubject.getBoosterVaccinationDate();
            }

            if (oldBoosterVaccinationDate != null && !oldBoosterVaccinationDate.isEqual(newBoosterVaccinationDate)
                    && oldBoosterVaccinationDate.isBefore(lastReportDate.plusDays(1))) {

                config.getBoosterVaccinationReportsToUpdate().add(oldBoosterVaccinationDate.toString(formatter));
            }
            if (newBoosterVaccinationDate != null && newBoosterVaccinationDate.isBefore(lastReportDate.plusDays(1))
                    && (!newBoosterVaccinationDate.isEqual(oldBoosterVaccinationDate) || reportRelevantDataChanged(oldSubject, newSubject))) {

                config.getBoosterVaccinationReportsToUpdate().add(newBoosterVaccinationDate.toString(formatter));
            }

            configService.updateConfig(config);
        }
    }

    private boolean reportRelevantDataChanged(Subject oldSubject, Subject newSubject) {
        if (oldSubject == null) {
            return true;
        }
        if (oldSubject.getGender() != null && !oldSubject.getGender().equals(newSubject.getGender())) {
            return true;
        }
        if (newSubject.getGender() != null && !newSubject.getGender().equals(oldSubject.getGender())) {
            return true;
        }
        if (oldSubject.getDateOfBirth() != null && !oldSubject.getDateOfBirth().isEqual(newSubject.getDateOfBirth())) {
            return true;
        }
        if (newSubject.getDateOfBirth() != null && !newSubject.getDateOfBirth().isEqual(oldSubject.getDateOfBirth())) {
            return true;
        }
        return false;
    }

    @Autowired
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
