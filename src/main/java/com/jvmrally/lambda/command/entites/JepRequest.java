package com.jvmrally.lambda.command.entites;

import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;

@ParsedEntity
public class JepRequest {

    @Flag(longName = "id", description = "ID of the JEP")
    private Integer jepId = -1;

    @Flag(shortName = 's', longName = "search", description = "Searches JEP list by title")
    private String searchParam = "";

    @Flag(longName = "status", description = "Searches JEP list by status")
    private String statusName = "";

    @Flag(longName = "type", description = "Returns list of all JEPs for specified type")
    private String type = "";

    @Flag(longName = "release", description = "Returns list of all JEPs for specified target")
    private Integer target = -1;

    public Integer getJepId() {
        return jepId;
    }

    public String getSearchParam() {
        return searchParam;
    }

    public String getStatusName() {
        return statusName;
    }

    public String getType() {
        return type;
    }

    public Integer getTarget() {
        return target;
    }

 }