package org.motechproject.ebodac.domain;

public class Config {

    public static final String TIME_PICKER_FORMAT = "HH:mm";

    private String zetesUrl;

    private String zetesUsername;

    private String zetesPassword;

    private String startTime;

    private String sftpHost;

    private String sftpPort;

    private String sftpDirectory;

    private String sftpUsername;

    private String sftpPassword;

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

    public String getSftpHost() {
        return sftpHost;
    }

    public void setSftpHost(String sftpHost) {
        this.sftpHost = sftpHost;
    }

    public String getSftpPort() {
        return sftpPort;
    }

    public void setSftpPort(String sftpPort) {
        this.sftpPort = sftpPort;
    }

    public String getSftpDirectory() {
        return sftpDirectory;
    }

    public void setSftpDirectory(String sftpDirectory) {
        this.sftpDirectory = sftpDirectory;
    }

    public String getSftpUsername() {
        return sftpUsername;
    }

    public void setSftpUsername(String sftpUsername) {
        this.sftpUsername = sftpUsername;
    }

    public String getSftpPassword() {
        return sftpPassword;
    }

    public void setSftpPassword(String sftpPassword) {
        this.sftpPassword = sftpPassword;
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
