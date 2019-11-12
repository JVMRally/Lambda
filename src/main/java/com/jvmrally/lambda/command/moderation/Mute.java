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

    private Auditor audit;
    private DSLContext dsl;
    private TimedReasonRequest req;
    private MessageReceivedEvent e;

    private Mute() {
    }

    private Mute(Auditor audit, DSLContext dsl, TimedReasonRequest req, MessageReceivedEvent e) {
        this.audit = audit;
        this.dsl = dsl;
        this.req = req;
        this.e = e;
    }

    @CommandHandler(commandName = "mute",
            description = "Mute someone for the specified amount of time. Defaults to 1 hour.",
            roles = "admin")
    public static void execute(Auditor audit, DSLContext dsl, TimedReasonRequest req,
            MessageReceivedEvent e) {
        Mute mute = new Mute(audit, dsl, req, e);
        Util.getRole(e.getGuild(), "muted").ifPresentOrElse(mute::muteMembers,
                () -> Messenger.send(e.getChannel(), "Role does not exist."));
    }

    private void muteMembers(Role role) {
        Util.getMentionedMembers(e).ifPresentOrElse(members -> filterMembers(role, members),
                () -> Messenger.send(e.getChannel(), "Must mention at least one user"));
    }

    private void filterMembers(Role role, List<Member> members) {
        for (Member member : members) {
            if (!member.getRoles().contains(role)) {
                muteMember(role, member);
            }
        }
    }

    private void muteMember(Role role, Member member) {
        e.getGuild().addRoleToMember(member, role).queue();
        logMute(member);
    }

    /**
     * Log the mute action
     * 
     * @param member
     */
    private void logMute(Member member) {
        saveMute(member);
        audit.log(AuditAction.MUTED, e.getAuthor().getIdLong(), member.getIdLong(),
                req.getReason());
    }

    /**
     * Save a mute record into the database
     * 
     * @param member
     */
    private void saveMute(Member member) {
        dsl.insertInto(MUTE).values(member.getIdLong(), req.getExpiry()).execute();
    }
}
