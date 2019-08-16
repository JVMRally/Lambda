package com.jvmrally.lambda.injectable;

import org.jooq.DSLContext;
import disparse.parser.reflection.Injectable;

/**
 * Auditor
 */
public class Auditor {

    private DSLContext dsl;

    @Injectable
    public static Auditor getAuditor() {
        return new Auditor();
    }

    private Auditor() {
        this.dsl = JooqConn.getJooqContext();
    }


}
