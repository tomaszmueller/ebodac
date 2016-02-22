package org.motechproject.bookingapp.domain;

public enum ScreeningStatus {
    ACTIVE("Active"),
    CANCELED("Canceled");

    private String value;

    ScreeningStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
