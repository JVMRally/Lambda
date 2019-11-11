package com.jvmrally.lambda.command.moderation;

import static com.jvmrally.lambda.db.tables.Mute.MUTE;
import java.util.List;
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

    private Mute() {
    }

    @CommandHandler(commandName = "mute",
            description = "Mute someone for the specified amount of time. Defaults to 1 hour.",
            roles = "admin")
    public static void mute(Auditor audit, DSLContext dsl, TimedReasonRequest req,
            MessageReceivedEvent e) {
        Util.getRole(e.getGuild(), "muted").ifPresentOrElse(
                role -> muteMembers(audit, dsl, req, e, role),
                () -> Messenger.send(e.getChannel(), "Role does not exist."));
    }



    private static void muteMembers(Auditor audit, DSLContext dsl, TimedReasonRequest req,
            MessageReceivedEvent e, Role role) {
        Util.getMentionedMembers(e).ifPresentOrElse(
                members -> filterMembers(audit, dsl, req, e, role, members),
                () -> Messenger.send(e.getChannel(), "Must mention at least one user"));
    }

    private static void filterMembers(Auditor audit, DSLContext dsl, TimedReasonRequest req,
            MessageReceivedEvent e, Role role, List<Member> members) {
        for (Member member : members) {
            if (!member.getRoles().contains(role)) {
                muteMember(audit, dsl, req, e, role, member);
            }
        }
    }

    private static void muteMember(Auditor audit, DSLContext dsl, TimedReasonRequest req,
            MessageReceivedEvent e, Role role, Member member) {
        e.getGuild().addRoleToMember(member, role).queue();
        logMute(audit, dsl, req, e, member);
    }

    private static void logMute(Auditor audit, DSLContext dsl, TimedReasonRequest req,
            MessageReceivedEvent e, Member member) {
        saveMute(dsl, member, req);
        audit.log(AuditAction.MUTED, e.getAuthor().getIdLong(), member.getIdLong(),
                req.getReason());
    }

    private static void saveMute(DSLContext dsl, Member member, TimedReasonRequest req) {
        dsl.insertInto(MUTE).values(member.getIdLong(), req.getExpiry()).execute();
    }
}
