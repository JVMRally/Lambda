package com.jvmrally.lambda.command.moderation;

import static com.jvmrally.lambda.db.tables.Ban.BAN;
import java.util.concurrent.TimeUnit;
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

    /**
     * Bans a user
     * 
     * @param req the request entity containing command flags and values
     * @param e   the message entity received
     */
    @CommandHandler(commandName = "ban",
            description = "Give an official warning to a user. The user will receive a direct message informing them of the reason.")
    public static void execute(DSLContext dsl, Auditor auditor, BanRequest req,
            MessageReceivedEvent e) {
        Util.getMentionedMember(e).ifPresentOrElse(member -> {
            banUser(dsl, member, req);
            auditor.log(AuditAction.WARNED, e.getAuthor().getIdLong(), member.getIdLong(),
                    req.getReason());
        }, () -> Messenger.toChannel(
                messenger -> messenger.to(e.getChannel()).message("Must provide a user")));
    }

    public static void banUser(DSLContext dsl, Member member, BanRequest req) {
        int deleteDays = req.shouldClear() ? DELETE_DAYS : 0;
        member.ban(deleteDays, req.getReason());
        dsl.insertInto(BAN).values(member.getIdLong(), req.getExpiry()).execute();
    }
}
