package com.jvmrally.lambda.command.misc;

import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Invite
 */
public class Invite extends Command {

    private Invite(MessageReceivedEvent e) {
        super(e);
    }

    @CommandHandler(commandName = "invite", description = "Contains the invite link.")
    public static void invite(MessageReceivedEvent e) {
        new Invite(e).execute();
    }

    private void execute() {
        Messenger.send(e.getChannel(), "https://discord.gg/MuZKQWM");
    }

}
