package com.jvmrally.lambda.command.entites;

import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;

/**
 * MessageIdRequest
 */
@ParsedEntity
public class GiveawayResultRequest {

    @Flag(shortName = 'i', longName = "id", description = "ID of giveaway message.")
    private String messageId = "";

    @Flag(shortName = 'n', longName = "num", description = "Number of winners to pick.")
    private Integer num = 1;

    /**
     * @return the messageId
     */
    public String getMessageId() {
        return messageId;
    }

    /**
     * @return the num
     */
    public Integer getNum() {
        return num;
    }

}
