package org.motechproject.ebodac.service.impl;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;
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
        String lastCalculationDate = config.getLastCalculationDate();

        if (StringUtils.isNotBlank(lastCalculationDate)) {
            LocalDate newCalculationDate = LocalDate.parse(lastCalculationDate,formatter).plusDays(1);

            LocalDate newPrimerVaccinationDate = newSubject.getPrimerVaccinationDate();
            LocalDate oldPrimerVaccinationDate = null;
            if (oldSubject != null) {
                oldPrimerVaccinationDate = oldSubject.getPrimerVaccinationDate();
            }

            if (oldPrimerVaccinationDate != null && !oldPrimerVaccinationDate.equals(newPrimerVaccinationDate)
                    && oldPrimerVaccinationDate.isBefore(newCalculationDate)) {

                config.getPrimerVaccinationReportsToUpdate().add(oldPrimerVaccinationDate.toString(formatter));
            }
            if (newPrimerVaccinationDate != null && newPrimerVaccinationDate.isBefore(newCalculationDate)
                    && (!newPrimerVaccinationDate.equals(oldPrimerVaccinationDate) || reportRelevantDataChanged(oldSubject, newSubject))) {

                config.getPrimerVaccinationReportsToUpdate().add(newPrimerVaccinationDate.toString(formatter));
            }

            LocalDate newBoosterVaccinationDate = newSubject.getBoosterVaccinationDate();
            LocalDate oldBoosterVaccinationDate = null;
            if (oldSubject != null) {
                oldBoosterVaccinationDate = oldSubject.getBoosterVaccinationDate();
            }

            if (oldBoosterVaccinationDate != null && !oldBoosterVaccinationDate.equals(newBoosterVaccinationDate)
                    && oldBoosterVaccinationDate.isBefore(newCalculationDate)) {

                config.getBoosterVaccinationReportsToUpdate().add(oldBoosterVaccinationDate.toString(formatter));
            }
            if (newBoosterVaccinationDate != null && newBoosterVaccinationDate.isBefore(newCalculationDate)
                    && (!newBoosterVaccinationDate.equals(oldBoosterVaccinationDate) || reportRelevantDataChanged(oldSubject, newSubject))) {

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
        if (oldSubject.getDateOfBirth() != null && !oldSubject.getDateOfBirth().equals(newSubject.getDateOfBirth())) {
            return true;
        }
        if (newSubject.getDateOfBirth() != null && !newSubject.getDateOfBirth().equals(oldSubject.getDateOfBirth())) {
            return true;
        }
        return false;
    }

    @Autowired
    @Override
    public void setConfigService(ConfigService configService) {
        this.configService = configService;
    }
}
