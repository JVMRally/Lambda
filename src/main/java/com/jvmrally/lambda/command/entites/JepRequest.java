package com.jvmrally.lambda.command.entites;

import java.util.Objects;

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

    @Flag(longName = "release", description = "Returns list of all JEPs for specified java version")
    private String release = "";

    public Integer getJepId() {
        return jepId;
    }

    public String getSearchParam() {
        return searchParam;
    }

    public String getStatusName() {
        return statusName.toUpperCase();
    }

    public String getType() {
        return type.toUpperCase();
    }

    public String getRelease() {
        return release;
    }

    public boolean isEmptyRequest() {
        return Objects.equals(jepId, -1) 
            && searchParam.isEmpty() 
            && statusName.isEmpty() 
            && type.isEmpty() 
            && release.isEmpty() ;
    }

    public String toHumanReadableString() {
        throw new UnsupportedOperationException("Not implemented yet. Only there so no compile errors occur while writing tests.");
    }
}
