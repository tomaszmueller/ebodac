package org.motechproject.ebodac.domain;

public enum Gender {
    Male("M"),
    Female("F"),
    Unknown("U"),
    Undifferentiated("UN");

    private String value;

    private Gender(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}