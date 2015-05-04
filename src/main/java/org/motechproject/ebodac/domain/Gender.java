package org.motechproject.ebodac.domain;

public enum Gender {
    MALE("M"),
    FEMALE("F"),
    UNKNOWN("U"),
    UNDIFFERENTIATED("UN");

    private String value;

    private Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}