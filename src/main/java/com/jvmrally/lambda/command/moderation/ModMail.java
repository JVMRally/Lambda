package com.jvmrally.lambda.command.moderation;

import java.util.List;
import com.jvmrally.lambda.Util;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * ModMail
 */
public class ModMail {

    @ParsedEntity
    static class ModMailRequest {
        @Flag(shortName = 'u', longName = "user")
        private String user = "";
    }

    @CommandHandler(commandName = "modmail")
    public static void delete(ModMailRequest req, List<String> args, MessageReceivedEvent e) {
        List<Member> members = e.getMessage().getMentionedMembers();
        if (members.size() > 1) {
            e.getChannel().sendMessage("Can only modmail 1 user").queue();
            return;
        }
        if (req.user.isEmpty()) {
            e.getChannel().sendMessage("Must provide a user").queue();
            return;
        }
        Member member = members.get(0);
        String message = "**Staff Reply: ** " + Util.rebuildArgsToString(args);
        member.getUser().openPrivateChannel()
                .queue(channel -> channel.sendMessage(message).queue());
    }
}
