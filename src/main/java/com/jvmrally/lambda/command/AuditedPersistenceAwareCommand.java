package com.jvmrally.lambda.command;

import com.jvmrally.lambda.injectable.Auditor;
import org.jooq.DSLContext;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * AuditedPersistenceAwareCommand
 */
public class AuditedPersistenceAwareCommand extends PersistenceAwareCommand {

    protected final Auditor audit;

    public AuditedPersistenceAwareCommand(MessageReceivedEvent e, DSLContext dsl) {
        super(e, dsl);
        this.audit = new Auditor(dsl);
    }
}
