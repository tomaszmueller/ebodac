package org.motechproject.ebodac.validation;

import org.motechproject.ebodac.domain.Language;

public class ValidationError {

    public static final String PHONE_NUMBER_NOT_CORRECT = "ERROR: The phone number introduced is not correct";
    public static final String SUBJECT_ID_NULL = "ERROR: subjectId parameter value can't be null";
    public static final String SUBJECT_ID_NOT_NUMERIC = "ERROR: subjectId parameter value has to be numeric";
    public static final String SUBJECT_ID_NOT_VERIFIED = "ERROR: subjectId parameter value's format verification failed";
    public static final String NAME_NULL  = "ERROR: name parameter value can't be null";
    public static final String NAME_HAS_DIGITS  = "ERROR: name parameter value cannot contain a number";
    public static final String HOUSEHOLD_NAME_HAS_DIGITS = "ERROR: householdName parameter value cannot contain a number";
    public static final String LANGUAGE_NULL  = "ERROR: language parameter value can't be null";
    public static final String LANGUAGE_NOT_CORRECT  = "ERROR: language parameter value can only equal to one of: " +
            Language.getListOfCodes();
    public static final String HEAD_OF_HOUSEHOLD_HAS_DIGITS = "ERROR: headOfHousehold parameter value cannot contain a number";


    private String message;

    public ValidationError(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String toString() {
        return message;
    }
}
