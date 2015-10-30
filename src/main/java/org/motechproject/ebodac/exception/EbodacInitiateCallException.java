package org.motechproject.ebodac.exception;

public class EbodacInitiateCallException extends EbodacException {

    public EbodacInitiateCallException(String message, Throwable cause, String messageKey, String... params) {
        super(message, cause, messageKey, params);
    }

    public EbodacInitiateCallException(String message, String messageKey, String... params) {
        super(message, messageKey, params);
    }
}
