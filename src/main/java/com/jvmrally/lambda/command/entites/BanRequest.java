package com.jvmrally.lambda.command.entites;

import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;

/**
 * BanRequest
 */
@ParsedEntity
public class BanRequest extends TimedReasonRequest {

    @Flag(shortName = 'c', longName = "clear",
            description = "Whether or not to delete the last 7 days of messages.")
    private boolean clear = false;

    /**
     * @return the clear
     */
    public boolean shouldClear() {
        return clear;
    }
}
