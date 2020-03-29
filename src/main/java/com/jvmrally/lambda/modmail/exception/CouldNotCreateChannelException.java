package com.jvmrally.lambda.modmail.exception;

/**
 * CouldNotCreateChannelException
 */
public class CouldNotCreateChannelException extends RuntimeException {
    private static final long serialVersionUID = -4925638998536771992L;

    public CouldNotCreateChannelException(String message, Throwable cause) {
        super(message, cause);
    }
}