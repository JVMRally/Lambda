package com.jvmrally.lambda.modmail.exception;

/**
 * ArchivingException
 */
public class ArchivingException extends RuntimeException {
    public ArchivingException(String message, Throwable cause) {
        super(message, cause);
    }

    public ArchivingException(String message) {
        super(message);
    }
}