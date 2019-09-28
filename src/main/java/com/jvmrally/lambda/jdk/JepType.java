package com.jvmrally.lambda.jdk;

import java.util.HashMap;
import java.util.Map;

/**
 * JEPType
 */
public enum JepType {

    PROCESS('P'), INFORMATIONAL('I'), FEATURE('F'), INFRASTRUCTURE('S');

    private final char abbreviation;
    private static final Map<Character, JepType> lookup = new HashMap<>();

    static {
        for (JepType type : JepType.values()) {
            lookup.put(type.getAbbreviation(), type);
        }
    }

    private JepType(char abbreviation) {
        this.abbreviation = abbreviation;
    }

    /**
     * @return the abbreviation
     */
    private char getAbbreviation() {
        return abbreviation;
    }

    public static JepType get(char abbreviation) {
        return lookup.get(abbreviation);
    }
}
