package com.jvmrally.lambda.command.exception;

/**
 * RoleNotFoundException
 */
public class RoleNotFoundException extends Exception {

    private static final long serialVersionUID = 1L;

    public RoleNotFoundException(String role) {
        super("Role: " + role + ", does not exist");
    }

}
