package com.jvmrally.lambda.command.moderation;

import java.util.List;
import com.jvmrally.lambda.command.entites.UserRequest;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * ModMail
 */
public class ModMail {

    @CommandHandler(commandName = "modmail",
            description = "Reply to a user via the bot via direct message.")
    public static void modmail(UserRequest req, List<String> args, MessageReceivedEvent e) {
        List<Member> members = e.getMessage().getMentionedMembers();
        if (members.size() > 1) {
            Messenger.toChannel(
                    messenger -> messenger.to(e.getChannel()).message("Can only modmail 1 user"));
            return;
        }
        if (req.getUser().isEmpty()) {
            Messenger.toChannel(
                    messenger -> messenger.to(e.getChannel()).message("Must provide a user"));
            return;
        }
        Member member = members.get(0);
        String message = "**Staff Reply: ** " + Util.rebuildArgsToString(args);
        Messenger.toUser(messenger -> messenger.to(member).message(message));
    }
}
