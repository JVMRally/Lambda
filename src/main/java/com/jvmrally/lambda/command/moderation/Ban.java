package com.jvmrally.lambda.command.moderation;

import static com.jvmrally.lambda.db.tables.Ban.BAN;
import com.jvmrally.lambda.command.entites.BanRequest;
import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.injectable.Auditor;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Ban
 */
public class Ban {

    private static final int DELETE_DAYS = 7;

    private Ban() {
    }

    /**
     * Bans a user
     * 
     * @param req the request entity containing command flags and values
     * @param e   the message entity received
     */
    @CommandHandler(commandName = "ban",
            description = "Give an official warning to a user. The user will receive a direct message informing them of the reason.",
            roles = "admin")
    public static void execute(DSLContext dsl, Auditor auditor, BanRequest req,
            MessageReceivedEvent e) {
        Util.getMentionedMember(e).ifPresentOrElse(
                member -> executeBan(dsl, auditor, req, e, member),
                () -> Messenger.send(e.getChannel(), "Must provide a user"));
    }

    private static void executeBan(DSLContext dsl, Auditor auditor, BanRequest req,
            MessageReceivedEvent e, Member member) {
        banUser(dsl, member, req);
        logBan(dsl, auditor, req, e, member);
    }


    /**
     * If the clear flag is true, will delete the last 7 days of messages from the target user
     * 
     * @param dsl
     * @param member the target member
     * @param req
     */
    public static void banUser(DSLContext dsl, Member member, BanRequest req) {
        int deleteDays = req.shouldClear() ? DELETE_DAYS : 0;
        member.ban(deleteDays, req.getReason()).queue();
    }

    private static void logBan(DSLContext dsl, Auditor auditor, BanRequest req,
            MessageReceivedEvent e, Member member) {
        dsl.insertInto(BAN).values(member.getIdLong(), req.getExpiry()).execute();
        auditor.log(AuditAction.WARNED, e.getAuthor().getIdLong(), member.getIdLong(),
                req.getReason());
    }
}
