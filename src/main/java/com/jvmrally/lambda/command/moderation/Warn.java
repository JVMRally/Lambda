package com.jvmrally.lambda.command.moderation;

import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.command.entites.ReasonRequest;
import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.injectable.Auditor;
import com.jvmrally.lambda.utility.messaging.Messenger;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import static com.jvmrally.lambda.db.tables.Audit.AUDIT;

/**
 * Warn
 */
public final class Warn {

    private Warn() {
    }

    /**
     * Gives a warning to a user
     * 
     * @param req the request entity containing command flags and values
     * @param e   the message entity received
     */
    @CommandHandler(commandName = "warn",
            description = "Give an official warning to a user. The user will receive a direct message informing them of the reason.",
            roles = "admin")
    public static void warn(DSLContext dsl, Auditor auditor, ReasonRequest req,
            MessageReceivedEvent e) {
        Util.getMentionedMember(e).ifPresentOrElse(member -> {
            int warnings = dsl.fetchCount(dsl.selectFrom(AUDIT).where(AUDIT.MOD_ACTION
                    .eq(AuditAction.WARNED).and(AUDIT.TARGET_USER.eq(member.getIdLong()))));
            String warning = buildWarning(warnings, req.getReason());
            Messenger.send(member, warning);
            auditor.log(AuditAction.WARNED, e.getAuthor().getIdLong(), member.getIdLong(),
                    req.getReason());
            Messenger.send(e.getChannel(), "Warning has been sent to the user and logged.");
            Util.addRoleToUser(e.getGuild(), member, "warned");
        }, () -> Messenger.send(e.getChannel(), "Must mention at least one user"));
    }

    private static String buildWarning(int warnings, String reason) {
        return "**Warning #" + warnings + "**\n"
                + "You have been warned by a member of staff. Please read our rules for guidelines of how you should behave.\n "
                + "You have been warned for the following reason: " + reason + "\n\n"
                + "To ensure you have received and understood this, you have been automatically muted. Respond with `ACK` and you will be unmuted.";
    }
}
