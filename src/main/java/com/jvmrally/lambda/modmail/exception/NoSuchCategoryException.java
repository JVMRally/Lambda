package com.jvmrally.lambda.modmail.exception;

/**
 * NoSuchCategoryException
 */
public class NoSuchCategoryException extends RuntimeException {
    private static final long serialVersionUID = -349788360938475945L;

    public NoSuchCategoryException(String cause) {
        super(cause);
    }
}