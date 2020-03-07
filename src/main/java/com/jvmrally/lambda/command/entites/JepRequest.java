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
        final String FORMAT = "%s: '%s'";
        final String DELIMITER = ", ";
        var readableString = new StringBuilder();

        if(!Objects.equals(jepId, -1)){
            readableString.append(String.format(FORMAT, "id", getJepId()));
        }

        if(!searchParam.isEmpty()){
            if(readableString.length() != 0){
                readableString.append(DELIMITER);
            }
            readableString.append(String.format(FORMAT, "term", getSearchParam()));
        }

        if(!statusName.isEmpty() ){
            if(readableString.length() != 0){
                readableString.append(DELIMITER);
            }
            readableString.append(String.format(FORMAT, "status", getStatusName()));
        }

        if(!type.isEmpty() ){
            if(readableString.length() != 0){
                readableString.append(DELIMITER);
            }
            readableString.append(String.format(FORMAT, "type", getType()));
        }

        if(!release.isEmpty() ){
            if(readableString.length() != 0){
                readableString.append(DELIMITER);
            }
            readableString.append(String.format(FORMAT, "release", getRelease()));
        }

        return readableString.toString();
    }
}
