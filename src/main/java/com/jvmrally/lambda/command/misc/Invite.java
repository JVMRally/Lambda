package com.jvmrally.lambda.command.misc;

import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Invite
 */
public class Invite {

    @CommandHandler(commandName = "invite", description = "Contains the invite link.")
    public static void invite(MessageReceivedEvent e) {
        Messenger.send(e.getChannel(), "https://discord.gg/MuZKQWM");
    }

}
