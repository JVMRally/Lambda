package com.jvmrally.lambda.command.moderation;

import static com.jvmrally.lambda.db.tables.Mute.MUTE;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.injectable.Auditor;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Mute
 */
public class Mute {

    @ParsedEntity
    static class MuteRequest {
        @Flag(shortName = 'd', longName = "days", description = "Number of days to mute.")
        private Integer days = 0;

        @Flag(longName = "hours", description = "Number of hours to mute.")
        private Integer hours = 1;

        @Flag(shortName = 'r', longName = "reason", description = "The reason for the mute.")
        private String reason = "";
    }

    @CommandHandler(commandName = "mute",
            description = "Mute someone for the specified amount of time. Defaults to 1 hour.")
    public static void mute(Auditor audit, DSLContext dsl, MuteRequest req,
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
                            req.reason);
                }
            }
        }, () -> Messenger.toChannel(
                messenger -> messenger.to(e.getChannel()).message("Role does not exist")));

    }

    private static void logMute(DSLContext dsl, Member member, MessageReceivedEvent e,
            MuteRequest req) {
        long expiryMillis = TimeUnit.HOURS.toMillis(req.hours) + TimeUnit.DAYS.toMillis(req.days);
        dsl.insertInto(MUTE).values(member.getIdLong(), System.currentTimeMillis() + expiryMillis)
                .execute();
    }
}
