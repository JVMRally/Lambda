package com.jvmrally.lambda.command.entites;

import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;

/**
 * ReasonRequest
 */
@ParsedEntity
public class ReasonRequest {

    @Flag(shortName = 'r', longName = "reason", description = "The reason for the action.")
    private String reason = "";

    /**
     * @return the reason
     */
    public String getReason() {
        return reason;
    }

}
