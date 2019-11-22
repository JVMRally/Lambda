package com.jvmrally.lambda.command.moderation;

import java.util.List;
import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * ModMail
 */
public class ModMail extends Command {

    private ModMail(MessageReceivedEvent e) {
        super(e);
    }

    @CommandHandler(commandName = "modmail",
            description = "Reply to a user via the bot via direct message.", roles = "admin")
    public static void execute(List<String> args, MessageReceivedEvent e) {
        ModMail modMail = new ModMail(e);
        Util.getMentionedMember(e).ifPresentOrElse(member -> modMail.sendReply(args, member),
                () -> Messenger.send(e.getChannel(), "Must provide a user"));
    }

    private void sendReply(List<String> args, Member member) {
        String message = "**Staff Reply: ** " + Util.rebuildArgsToString(args);
        Messenger.send(member, message);
    }
}
