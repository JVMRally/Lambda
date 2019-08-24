package com.jvmrally.lambda.command.moderation;

import static com.jvmrally.lambda.db.tables.Mute.MUTE;
import java.util.List;
import java.util.Optional;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * UnMute
 */
public class UnMute {

    @CommandHandler(commandName = "unmute", description = "Unmute mentioned users.")
    public static void mute(DSLContext dsl, MessageReceivedEvent e) {
        List<Member> members = e.getMessage().getMentionedMembers();
        if (members.isEmpty()) {
            Messenger.toChannel(messenger -> messenger.to(e.getChannel())
                    .message("Must mention at least one user"));
            return;
        }
        Optional<Role> role = Util.getRole(e.getGuild(), "muted");
        role.ifPresentOrElse(r -> {
            for (Member member : members) {
                e.getGuild().removeRoleFromMember(member, r);
                dsl.deleteFrom(MUTE).where(MUTE.USERID.eq(member.getIdLong())).execute();
            }
        }, () -> Messenger.toChannel(
                messenger -> messenger.to(e.getChannel()).message("Role does not exist")));
    }
}
