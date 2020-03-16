package com.jvmrally.lambda.command.entities;

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

    private static final String DELIMITER = ", ";
    private static final String FORMAT = "%s: '%s'";

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
        return Objects.equals(jepId, -1) && searchParam.isEmpty() && statusName.isEmpty()
                && type.isEmpty() && release.isEmpty();
    }

    public String toHumanReadableString() {
        var readableString = new StringBuilder();

        if (!Objects.equals(jepId, -1)) {
            readableString.append(String.format(FORMAT, "id", getJepId()));
        }

        readableString.append(
                fieldToHumanReadableString("term", getSearchParam(), readableString.length() != 0));
        readableString.append(fieldToHumanReadableString("status", getStatusName(),
                readableString.length() != 0));
        readableString.append(
                fieldToHumanReadableString("type", getType(), readableString.length() != 0));
        readableString.append(
                fieldToHumanReadableString("release", getRelease(), readableString.length() != 0));

        return readableString.toString();
    }

    private String fieldToHumanReadableString(String fieldName, String fieldValue,
            boolean delimit) {
        var returnedString = new StringBuilder();
        if (!fieldValue.isEmpty()) {
            if (delimit) {
                returnedString.append(DELIMITER);
            }
            returnedString.append(String.format(FORMAT, fieldName, fieldValue));
        }
        return returnedString.toString();
    }
}
