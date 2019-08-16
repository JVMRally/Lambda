package com.jvmrally.lambda.command.moderation;

import static com.jvmrally.lambda.db.tables.Mute.MUTE;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import com.jvmrally.lambda.Util;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Mute
 */
public class Mute {

    @ParsedEntity
    static class MuteRequest {
        @Flag(shortName = 'd', longName = "days")
        private Integer days = 0;

        @Flag(shortName = 'h', longName = "hours")
        private Integer hours = 1;

        @Flag(shortName = 'r', longName = "reason")
        private String reason = "";
    }

    @CommandHandler(commandName = "mute")
    public static void mute(DSLContext dsl, MuteRequest req, MessageReceivedEvent e) {
        List<Member> members = e.getMessage().getMentionedMembers();
        if (members.isEmpty()) {
            e.getChannel().sendMessage("Must mention at least one user").queue();
            return;
        }
        Optional<Role> role = Util.getRole(e.getGuild(), "muted");
        role.ifPresentOrElse(r -> {
            for (Member member : members) {
                if (!member.getRoles().contains(r)) {
                    e.getGuild().addRoleToMember(member, r).queue();
                    logMute(dsl, member, e, req);
                }
            }
        }, () -> e.getChannel().sendMessage("Role does not exist").queue());
    }

    private static void logMute(DSLContext dsl, Member member, MessageReceivedEvent e,
            MuteRequest req) {
        long expiryMillis = TimeUnit.HOURS.toMillis(req.hours) + TimeUnit.DAYS.toMillis(req.days);
        dsl.insertInto(MUTE).values(member.getIdLong(), System.currentTimeMillis() + expiryMillis)
                .execute();
        TextChannel channel = e.getGuild().getTextChannelsByName("modlog", true).get(0);
        String message =
                String.format("%s muted %s for %d days, %d hours for: %s", e.getAuthor().getName(),
                        member.getEffectiveName(), req.days, req.hours, req.reason);
        channel.sendMessage(message).queue();
    }
}
