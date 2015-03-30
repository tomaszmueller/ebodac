package org.motechproject.ebodac.domain;

/**
 * Represents language of Subject
 */
public enum Language {
    ENGLISH("English"),
    KRIO("Krio"),
    LIMBA("Limba"),
    SUSU("Susu"),
    TEMNE("Temne");

    private String value;

    private Language(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
