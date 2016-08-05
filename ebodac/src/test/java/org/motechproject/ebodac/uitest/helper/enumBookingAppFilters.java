package org.motechproject.ebodac.uitest.helper;

public enum enumBookingAppFilters {

    DATE_RANGE("Date range"), 
    TODAY("Today"), 
    TOMORROW("Tomorrow"), 
    DAY_AFTER_TOMORROW("Day after tomorrow"), 
    NEXT_3_DAYS("Next 3 days"), 
    NEXT_7_DAYS("Next 7 days");

    private String value;

    enumBookingAppFilters(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
