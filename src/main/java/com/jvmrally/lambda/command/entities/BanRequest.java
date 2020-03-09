package com.jvmrally.lambda.command.entities;

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
     * @return Whether or not to delete the last 7 days of messages.
     */
    public boolean shouldClear() {
        return clear;
    }
}
