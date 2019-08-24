package com.jvmrally.lambda.command.entites;

import disparse.parser.reflection.Flag;

/**
 * UserRequest
 */
public class UserRequest {

    @Flag(shortName = 'u', longName = "user",
            description = "The target user to respond to as a mention. e.g. `@Lambda#0313`")
    private String user = "";

    /**
     * @return the user
     */
    public String getUser() {
        return user;
    }
}
