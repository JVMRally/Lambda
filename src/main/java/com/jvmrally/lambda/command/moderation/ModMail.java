package com.jvmrally.lambda.command.moderation;

import java.util.List;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * ModMail
 */
public class ModMail {

    @CommandHandler(commandName = "modmail",
            description = "Reply to a user via the bot via direct message.", roles = "admin")
    public static void modmail(List<String> args, MessageReceivedEvent e) {
        Util.getMentionedMember(e).ifPresentOrElse(member -> {
            String message = "**Staff Reply: ** " + Util.rebuildArgsToString(args);
            Messenger.send(member, message);
        }, () -> Messenger.send(e.getChannel(), "Must provide a user"));
    }
}
