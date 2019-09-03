package com.jvmrally.lambda.command.moderation;

import static com.jvmrally.lambda.db.tables.Mute.MUTE;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * UnMute
 */
public class UnMute {

    @CommandHandler(commandName = "unmute", description = "Unmute mentioned users.",
            roles = "admin")
    public static void mute(DSLContext dsl, MessageReceivedEvent e) {
        Util.getRole(e.getGuild(), "muted").ifPresentOrElse(role -> Util.getMentionedMembers(e)
                .ifPresentOrElse(members -> members.forEach(member -> {
                    e.getGuild().removeRoleFromMember(member, role).queue();
                    dsl.deleteFrom(MUTE).where(MUTE.USERID.eq(member.getIdLong())).execute();
                }), () -> Messenger.toChannel(messenger -> messenger.to(e.getChannel())
                        .message("Must mention at least one user"))),
                () -> Messenger.toChannel(
                        messenger -> messenger.to(e.getChannel()).message("Role does not exist")));
    }
}
