package com.jvmrally.lambda.command.moderation;

import static com.jvmrally.lambda.db.tables.Ban.BAN;
import com.jvmrally.lambda.command.AuditedPersistenceAwareCommand;
import com.jvmrally.lambda.command.entities.BanRequest;
import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.discord.AbstractPermission;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Ban
 */
public class Ban extends AuditedPersistenceAwareCommand {

    private static final int DELETE_DAYS = 7;
    private final BanRequest req;

    private Ban(MessageReceivedEvent e, DSLContext dsl, BanRequest req) {
        super(e, dsl);
        this.req = req;
    }

    /**
     * Bans a user
     *
     * @param req the request entity containing command flags and values
     * @param e   the message entity received
     */
    @CommandHandler(commandName = "ban",
            description = "Give an official warning to a user. The user will receive a direct message informing them of the reason.",
            perms = AbstractPermission.BAN_MEMBERS)
    public static void execute(MessageReceivedEvent e, DSLContext dsl, BanRequest req) {
        Ban ban = new Ban(e, dsl, req);
        ban.getMentionedMember().ifPresentOrElse(ban::executeBan, ban::sendMissingUserError);
    }

    private void sendMissingUserError() {
        Messenger.send(e.getChannel(), "Must provide a user");
    }

    private void executeBan(Member member) {
        banUser(member);
        logBan(member);
    }

    /**
     * If the clear flag is true, will delete the last 7 days of messages from the target user
     *
     * @param member the target member
     */
    public void banUser(Member member) {
        int deleteDays = req.shouldClear() ? DELETE_DAYS : 0;
        member.ban(deleteDays, req.getReason()).queue();
    }

    private void logBan(Member member) {
        dsl.insertInto(BAN).values(member.getIdLong(), req.getExpiry()).execute();
        audit.log(AuditAction.WARNED, e.getAuthor().getIdLong(), member.getIdLong(),
                req.getReason());
    }
}
