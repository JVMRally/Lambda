package com.jvmrally.lambda.injectable;

import org.jooq.DSLContext;
import disparse.parser.reflection.Injectable;
import com.jvmrally.lambda.db.enums.AuditAction;

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

    public void log(AuditAction action, long initiator) {

    }

    public void log(AuditAction action, long initiator, long target) {

    }

    public void log(AuditAction action, long initiator, long target, String reason) {

    }
}
