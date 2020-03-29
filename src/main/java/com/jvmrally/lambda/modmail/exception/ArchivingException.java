package com.jvmrally.lambda.modmail.exception;

/**
 * ArchivingException
 */
public class ArchivingException extends RuntimeException {
    private static final long serialVersionUID = 2649393988071667847L;

    public ArchivingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchivingException(String message) {
        super(message);
    }
}