package org.motechproject.ebodac.domain;

public class Config {

    public static final String TIME_PICKER_FORMAT = "HH:mm";

    private String zetesUrl;

    private String zetesUsername;

    private String zetesPassword;

    private String startTime;

    private String ftpsHost;

    private String ftpsPort;

    private String ftpsDirectory;

    private String ftpsUsername;

    private String ftpsPassword;

    private String lastCsvUpdate;

    private String emailHost;

    private String email;

    private String emailPassword;

    private Integer emailCheckInterval;

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

    public String getFtpsPort() {
        return ftpsPort;
    }

    public void setFtpsPort(String ftpsPort) {
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
}
