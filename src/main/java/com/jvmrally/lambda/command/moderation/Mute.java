package com.jvmrally.lambda.command.moderation;

import static com.jvmrally.lambda.db.tables.Mute.MUTE;
import java.util.List;
import java.util.Optional;
import com.jvmrally.lambda.command.entites.TimedReasonRequest;
import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.injectable.Auditor;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Mute
 */
public class Mute {

    @CommandHandler(commandName = "mute",
            description = "Mute someone for the specified amount of time. Defaults to 1 hour.")
    public static void mute(Auditor audit, DSLContext dsl, TimedReasonRequest req,
            MessageReceivedEvent e) {
        List<Member> members = e.getMessage().getMentionedMembers();
        if (members.isEmpty()) {
            Messenger.toChannel(messenger -> messenger.to(e.getChannel())
                    .message("Must mention at least one user."));
            return;
        }
        Optional<Role> role = Util.getRole(e.getGuild(), "muted");
        role.ifPresentOrElse(r -> {
            for (Member member : members) {
                if (!member.getRoles().contains(r)) {
                    e.getGuild().addRoleToMember(member, r).queue();
                    logMute(dsl, member, e, req);
                    audit.log(AuditAction.MUTED, e.getAuthor().getIdLong(), member.getIdLong(),
                            req.getReason());
                }
            }
        }, () -> Messenger.toChannel(
                messenger -> messenger.to(e.getChannel()).message("Role does not exist")));

    }

    private static void logMute(DSLContext dsl, Member member, MessageReceivedEvent e,
            TimedReasonRequest req) {
        dsl.insertInto(MUTE).values(member.getIdLong(), req.getExpiry())
                .execute();
    }
}
