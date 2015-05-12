package org.motechproject.ebodac.domain;

public class Config {

    public static final String TIME_PICKER_FORMAT = "HH:mm";

    private String zetesUrl;

    private String zetesUsername;

    private String zetesPassword;

    private String startTime;

    public Config() {
    }

    public Config(String zetesUrl, String zetesUsername, String zetesPassword, String startTime) {
        this.zetesUrl = zetesUrl;
        this.zetesUsername = zetesUsername;
        this.zetesPassword = zetesPassword;
        this.startTime = startTime;
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
}
