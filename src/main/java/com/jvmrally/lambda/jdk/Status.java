package com.jvmrally.lambda.jdk;

import java.util.HashMap;
import java.util.Map;

/**
 * Status
 */
public enum Status {

    ACTIVE("Active"), CLOSED("Closed / Delivered"), WITHDRAWN("Closed / Withdrawn"), CANDIDATE(
            "Candidate"), DRAFT("Draft"), TARGETED("Targeted"), PROPOSED_TO_TARGET(
                    "Proposed to Target"), SUBMITTED("Submitted"), INTEGRATED("Integrated"), COMPLETED("Completed");

    private final String sourceName;
    private static final Map<String, Status> lookup = new HashMap<>();

    static {
        for (Status status : Status.values()) {
            lookup.put(status.getSourceName(), status);
        }
    }

    private Status(String sourceName) {
        this.sourceName = sourceName;
    }

    /**
     * @return the sourceName
     */
    private String getSourceName() {
        return sourceName;
    }

    public static Status get(String sourceName) {
        return lookup.get(sourceName);
    }

}
