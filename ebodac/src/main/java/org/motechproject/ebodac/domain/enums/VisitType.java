package org.motechproject.ebodac.domain.enums;

public enum VisitType {
    SCREENING("Screening"),
    PRIME_VACCINATION_DAY("Prime Vaccination Day"),
    PRIME_VACCINATION_FIRST_FOLLOW_UP_VISIT("Prime Vaccination First Follow-up visit"),
    PRIME_VACCINATION_SECOND_FOLLOW_UP_VISIT("Prime Vaccination Second Follow-up visit"),
    BOOST_VACCINATION_DAY("Boost Vaccination Day"),
    BOOST_VACCINATION_FIRST_FOLLOW_UP_VISIT("Boost Vaccination First Follow-up visit"),
    BOOST_VACCINATION_SECOND_FOLLOW_UP_VISIT("Boost Vaccination Second Follow-up visit"),
    BOOST_VACCINATION_THIRD_FOLLOW_UP_VISIT("Boost Vaccination Third Follow-up visit"),
    FIRST_LONG_TERM_FOLLOW_UP_VISIT("First Long-term Follow-up visit"),
    SECOND_LONG_TERM_FOLLOW_UP_VISIT("Second Long-term Follow-up visit"),
    THIRD_LONG_TERM_FOLLOW_UP_VISIT("Third Long-term Follow-up visit"),
    FOURTH_LONG_TERM_FOLLOW_UP_VISIT("Fourth Long-term Follow-up visit", "LFU4 (18MPP)"),
    FIFTH_LONG_TERM_FOLLOW_UP_VISIT("Fifth Long-term Follow-up visit", "LFU5 (24MPP)"),
    SIXTH_LONG_TERM_FOLLOW_UP_VISIT("Sixth Long-term Follow-up visit", "LFU6 (30MPP)"),
    SEVENTH_LONG_TERM_FOLLOW_UP_VISIT("Seventh Long-term Follow-up visit", "LFU7 (36MPP)"),
    UNSCHEDULED_VISIT("Unscheduled Visit");

    private String motechValue;
    private String[] typeValues;

    VisitType(String... values) {
        this.typeValues = values;
        this.motechValue = values[0];
    }

    public static VisitType getByValue(String value) {
        if (value != null && value.startsWith(UNSCHEDULED_VISIT.getMotechValue())) {
            return UNSCHEDULED_VISIT;
        }
        for (VisitType visitType : VisitType.values()) {
            for (String typeValue : visitType.typeValues) {
                if (typeValue.equalsIgnoreCase(value)) {
                    return visitType;
                }
            }
        }
        return null;
    }

    public String getMotechValue() {
        return motechValue;
    }
}
