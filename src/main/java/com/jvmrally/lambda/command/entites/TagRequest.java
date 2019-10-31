package com.jvmrally.lambda.command.entites;

import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;

/**
 * TagRequest
 */
@ParsedEntity
public class TagRequest {

    @Flag(shortName = 'n', longName = "name", description = "Name of the tag")
    private String name = "";

    @Flag(shortName = 'c', longName = "content", description = "Content to create or edit a tag")
    private String content = "";

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }
}
