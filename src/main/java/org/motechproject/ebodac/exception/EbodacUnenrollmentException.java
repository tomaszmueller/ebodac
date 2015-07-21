package org.motechproject.ebodac.exception;

public class EbodacUnenrollmentException extends RuntimeException {

    public EbodacUnenrollmentException(String message) {
        super(message);
    }

    public EbodacUnenrollmentException(String message, Throwable cause) {
        super(message, cause);
    }
}
