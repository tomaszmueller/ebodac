package org.motechproject.ebodac.service.impl.csv;

public enum RaveVisitField {
    Visit("type"),
    VISITDT("date"),
    VISITDTPRJ("dateProjected");

    private String value;

    private RaveVisitField(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
