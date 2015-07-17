package org.motechproject.ebodac.domain;

import org.apache.commons.lang.StringUtils;
import org.motechproject.ebodac.constants.EbodacConstants;

import java.util.HashSet;
import java.util.Set;

public class Config {

    public static final String TIME_PICKER_FORMAT = "HH:mm";

    private String zetesUrl;

    private String zetesUsername;

    private String zetesPassword;

    private String startTime;

    private String ftpsHost;

    private Integer ftpsPort;

    private String ftpsDirectory;

    private String ftpsUsername;

    private String ftpsPassword;

    private String lastCsvUpdate;

    private String emailHost;

    private String email;

    private String emailPassword;

    private Integer emailCheckInterval;

    private String reportCalculationStartTime;

    private String firstCalculationStartDate;

    private String lastCalculationDate;

    private Boolean generateReports;

    private Boolean showWarnings = true;

    public Boolean getShowWarnings() {
        return showWarnings;
    }

    public void setShowWarnings(Boolean showWarnings) {
        this.showWarnings = showWarnings;
    }

    private Set<String> primerVaccinationReportsToUpdate;

    private Set<String> boosterVaccinationReportsToUpdate;

    public Config() {
    }

    public String getZetesUrl() {
        return zetesUrl;
    }

    public void setZetesUrl(String zetesUrl) {
        this.zetesUrl = zetesUrl;
    }

    public String getZetesPassword() {
        return zetesPassword;
    }

    public void setZetesPassword(String zetesPassword) {
        this.zetesPassword = zetesPassword;
    }

    public String getZetesUsername() {
        return zetesUsername;
    }

    public void setZetesUsername(String zetesUsername) {
        this.zetesUsername = zetesUsername;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getFtpsHost() {
        return ftpsHost;
    }

    public void setFtpsHost(String ftpsHost) {
        this.ftpsHost = ftpsHost;
    }

    public Integer getFtpsPort() {
        return ftpsPort;
    }

    public void setFtpsPort(Integer ftpsPort) {
        this.ftpsPort = ftpsPort;
    }

    public String getFtpsDirectory() {
        return ftpsDirectory;
    }

    public void setFtpsDirectory(String ftpsDirectory) {
        this.ftpsDirectory = ftpsDirectory;
    }

    public String getFtpsUsername() {
        return ftpsUsername;
    }

    public void setFtpsUsername(String ftpsUsername) {
        this.ftpsUsername = ftpsUsername;
    }

    public String getFtpsPassword() {
        return ftpsPassword;
    }

    public void setFtpsPassword(String ftpsPassword) {
        this.ftpsPassword = ftpsPassword;
    }

    public String getLastCsvUpdate() {
        return lastCsvUpdate;
    }

    public void setLastCsvUpdate(String lastCsvUpdate) {
        this.lastCsvUpdate = lastCsvUpdate;
    }

    public String getEmailHost() {
        return emailHost;
    }

    public void setEmailHost(String emailHost) {
        this.emailHost = emailHost;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getEmailPassword() {
        return emailPassword;
    }

    public void setEmailPassword(String emailPassword) {
        this.emailPassword = emailPassword;
    }

    public Integer getEmailCheckInterval() {
        return emailCheckInterval;
    }

    public void setEmailCheckInterval(Integer emailCheckInterval) {
        this.emailCheckInterval = emailCheckInterval;
    }

    public String getReportCalculationStartTime() {
        if (StringUtils.isBlank(reportCalculationStartTime)) {
            reportCalculationStartTime = EbodacConstants.DAILY_REPORT_EVENT_START_HOUR;
        }
        return reportCalculationStartTime;
    }

    public void setReportCalculationStartTime(String reportCalculationStartTime) {
        this.reportCalculationStartTime = reportCalculationStartTime;
    }

    public String getFirstCalculationStartDate() {
        return firstCalculationStartDate;
    }

    public void setFirstCalculationStartDate(String firstCalculationStartDate) {
        this.firstCalculationStartDate = firstCalculationStartDate;
    }

    public String getLastCalculationDate() {
        return lastCalculationDate;
    }

    public void setLastCalculationDate(String lastCalculationDate) {
        this.lastCalculationDate = lastCalculationDate;
    }

    public Boolean getGenerateReports() {
        return generateReports;
    }

    public void setGenerateReports(Boolean generateReports) {
        this.generateReports = generateReports;
    }

    public Set<String> getPrimerVaccinationReportsToUpdate() {
        if (primerVaccinationReportsToUpdate == null) {
            primerVaccinationReportsToUpdate = new HashSet<>();
        }
        return primerVaccinationReportsToUpdate;
    }

    public void setPrimerVaccinationReportsToUpdate(Set<String> primerVaccinationReportsToUpdate) {
        this.primerVaccinationReportsToUpdate = primerVaccinationReportsToUpdate;
    }

    public Set<String> getBoosterVaccinationReportsToUpdate() {
        if (boosterVaccinationReportsToUpdate == null) {
            boosterVaccinationReportsToUpdate = new HashSet<>();
        }
        return boosterVaccinationReportsToUpdate;
    }

    public void setBoosterVaccinationReportsToUpdate(Set<String> boosterVaccinationReportsToUpdate) {
        this.boosterVaccinationReportsToUpdate = boosterVaccinationReportsToUpdate;
    }
}
