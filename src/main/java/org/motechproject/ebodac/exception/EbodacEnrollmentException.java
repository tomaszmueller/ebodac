package org.motechproject.ebodac.exception;

public class EbodacEnrollmentException extends RuntimeException {

    public EbodacEnrollmentException(String message, Throwable cause) {
        super(message, cause);
    }

    public EbodacEnrollmentException(String message) {
        super(message);
    }
}
