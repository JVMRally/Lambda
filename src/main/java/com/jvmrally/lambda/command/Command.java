package com.jvmrally.lambda.command;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command
 */
public class Command {

    protected final MessageReceivedEvent e;

    public Command(MessageReceivedEvent e) {
        this.e = e;
    }

}
