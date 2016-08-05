package org.motechproject.ebodac.uitest.helper;

public enum BookingAppFilters {

    DATE_RANGE("Date range"), 
    TODAY("Today"), 
    TOMORROW("Tomorrow"), 
    DAY_AFTER_TOMORROW("Day after tomorrow"), 
    NEXT_3_DAYS("Next 3 days"), 
    NEXT_7_DAYS("Next 7 days");

    private String value;

    BookingAppFilters(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
