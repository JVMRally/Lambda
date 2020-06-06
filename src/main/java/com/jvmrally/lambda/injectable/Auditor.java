package com.jvmrally.lambda.injectable;

import static com.jvmrally.lambda.db.tables.Audit.AUDIT;
import java.time.OffsetDateTime;
import com.jvmrally.lambda.db.enums.AuditAction;
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

    public Auditor(DSLContext dsl) {
        this.dsl = dsl;
    }

    private Auditor() {
        this.dsl = JooqConn.getJooqContext();
    }

    /**
     * Log an action to the database
     * 
     * @param action    the action type
     * @param initiator the id of the initiator
     */
    public void log(AuditAction action, long initiator, long guildid) {
        insert(action, initiator, guildid);
    }

    /**
     * Log an action to the database
     * 
     * @param action    the action type
     * @param initiator the id of the initiator
     * @param target    the id of the target user
     */
    public void log(AuditAction action, long initiator, long target, long guildid) {
        insert(action, initiator, target, guildid);
    }

    /**
     * Log an action to the database
     * 
     * @param action    the action type
     * @param initiator the id of the initiator
     * @param target    the id of the target user
     * @param reason    the reason of the action
     */
    public void log(AuditAction action, long initiator, long target, String reason, long guildid) {
        if (reason.isEmpty()) {
            reason = null;
        }
        insert(action, initiator, target, reason, guildid);
    }

    private void insert(AuditAction action, long initiator, long guildid) {
        insert(action, initiator, null, null, guildid);
    }

    private void insert(AuditAction action, Long initiator, Long target, long guildid) {
        insert(action, initiator, target, null, guildid);
    }

    private void insert(AuditAction action, Long initiator, Long target, String reason,
            long guildid) {
        dsl.insertInto(AUDIT)
                .columns(AUDIT.MOD_ACTION, AUDIT.USER_ID, AUDIT.TARGET_USER, AUDIT.REASON,
                        AUDIT.CREATED, AUDIT.GUILD_ID)
                .values(action, initiator, target, reason, OffsetDateTime.now(), guildid).execute();
    }
}
