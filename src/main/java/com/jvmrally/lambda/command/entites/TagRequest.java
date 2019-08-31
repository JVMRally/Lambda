package com.jvmrally.lambda.command.entites;

import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;

/**
 * TagRequest
 */
@ParsedEntity
public class TagRequest {

    @Flag(shortName = 'c', longName = "create", description = "Create a new tag")
    private Boolean create = Boolean.FALSE;

    @Flag(shortName = 'e', longName = "edit", description = "Alter the content of an existing tag")
    private Boolean edit = Boolean.FALSE;

    @Flag(shortName = 'n', longName = "name", description = "Name of the tag")
    private String name = "";

    @Flag(shortName = 'd', longName = "delete", description = "Delete a tag")
    private Boolean delete = Boolean.FALSE;

    @Flag(shortName = 'l', longName = "list", description = "List all tags")
    private Boolean list = Boolean.FALSE;

    @Flag(longName = "content", description = "Content to create or edit a tag")
    private String content = "";

    /**
     * @return the create
     */
    public Boolean getCreate() {
        return create;
    }

    /**
     * @return the edit
     */
    public Boolean getEdit() {
        return edit;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the delete
     */
    public Boolean getDelete() {
        return delete;
    }

    /**
     * @return the list
     */
    public Boolean getList() {
        return list;
    }

    /**
     * @return the content
     */
    public String getContent() {
        return content;
    }

    /**
     * 
     */
    public boolean isManipulatingTag() {
        return create | edit | delete;
    }
}