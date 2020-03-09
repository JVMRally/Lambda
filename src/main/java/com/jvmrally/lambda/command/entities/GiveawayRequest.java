package com.jvmrally.lambda.command.entities;

import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;

/**
 * GiveawayRequest
 */
@ParsedEntity
public class GiveawayRequest {

    @Flag(shortName = 't', longName = "title", description = "Title of giveaway.")
    private String title = "";

    @Flag(shortName = 'd', longName = "description", description = "Description of giveaway.")
    private String description = "";

    /**
     * @return the title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

}
