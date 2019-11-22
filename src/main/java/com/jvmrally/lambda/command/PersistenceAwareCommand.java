package com.jvmrally.lambda.command;

import org.jooq.DSLContext;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * DataCommand
 * 
 * Command that requires access to the database
 */
public class PersistenceAwareCommand extends Command {

    protected final DSLContext dsl;

    public PersistenceAwareCommand(MessageReceivedEvent e, DSLContext dsl) {
        super(e);
        this.dsl = dsl;
    }
}
