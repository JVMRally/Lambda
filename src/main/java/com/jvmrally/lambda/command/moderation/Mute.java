package com.jvmrally.lambda.command.moderation;

import static com.jvmrally.lambda.db.tables.Mute.MUTE;
import java.util.List;
import javax.management.relation.RoleNotFoundException;
import com.jvmrally.lambda.command.AuditedPersistenceAwareCommand;
import com.jvmrally.lambda.command.entities.TimedReasonRequest;
import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.discord.AbstractPermission;
import net.dv8tion.jda.api.entities.Guild;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Mute
 */
public class Mute extends AuditedPersistenceAwareCommand {

    private static final String MUTED_ROLE = "muted";
    private final TimedReasonRequest request;

    private Mute(MessageReceivedEvent e, DSLContext dsl, TimedReasonRequest req) {
        super(e, dsl);
        this.request = req;
    }

    @CommandHandler(commandName = "mute",
            description = "Mute someone for the specified amount of time. Defaults to 1 hour.",
            perms = AbstractPermission.ADMINISTRATOR)
    public static void execute(MessageReceivedEvent e, DSLContext dsl, TimedReasonRequest req) {
        Mute mute = new Mute(e, dsl, req);
        Util.getRole(e.getGuild(), MUTED_ROLE).ifPresentOrElse(mute::muteMembers,
                () -> new RoleNotFoundException(MUTED_ROLE));
    }

    private void muteMembers(Role role) {
        getMentionedMembers().ifPresentOrElse(members -> filterMembers(role, members),
                this::sendMissingUserError);
    }

    private void sendMissingUserError() {
        Messenger.send(e.getChannel(), "Must mention at least one user");
    }

    private void filterMembers(Role role, List<Member> members) {
        for (Member member : members) {
            if (!member.getRoles().contains(role)) {
                muteMember(role, member);
            }
        }
    }

    private void muteMember(Role role, Member member) {
        Guild guild = e.getGuild();
        guild.addRoleToMember(member, role).queue();
        logMute(guild, member);
    }

    /**
     * Log the mute action
     *
     * @param guild
     * @param member
     */
    private void logMute(Guild guild, Member member) {
        saveMute(guild, member);
        audit.log(AuditAction.MUTED, e.getAuthor().getIdLong(), member.getIdLong(),
                request.getReason(), e.getGuild().getIdLong());
    }

    /**
     * Save a mute record into the database
     *
     * @param guild
     * @param member
     */
    private void saveMute(Guild guild, Member member) {
        dsl.insertInto(MUTE).columns(MUTE.USERID, MUTE.MUTE_EXPIRY, MUTE.GUILD_ID)
                .values(member.getIdLong(), request.getExpiry(), guild.getIdLong()).execute();
    }
}
