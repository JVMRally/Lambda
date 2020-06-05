package com.jvmrally.lambda.command.moderation;

import static com.jvmrally.lambda.db.tables.Audit.AUDIT;
import com.jvmrally.lambda.command.AuditedPersistenceAwareCommand;
import com.jvmrally.lambda.command.entities.ReasonRequest;
import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.discord.AbstractPermission;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Warn
 */
public final class Warn extends AuditedPersistenceAwareCommand {

    private final ReasonRequest req;

    private Warn(MessageReceivedEvent e, DSLContext dsl, ReasonRequest req) {
        super(e, dsl);
        this.req = req;
    }

    /**
     * Gives a warning to a user
     *
     * @param req the request entity containing command flags and values
     * @param e   the message entity received
     */
    @CommandHandler(commandName = "warn",
            description = "Give an official warning to a user. The user will receive a direct message informing them of the reason.",
            perms = AbstractPermission.ADMINISTRATOR)
    public static void warn(MessageReceivedEvent e, DSLContext dsl, ReasonRequest req) {
        Warn warn = new Warn(e, dsl, req);
        warn.getMentionedMember().ifPresentOrElse(warn::warnMember, warn::userError);
    }

    private void warnMember(Member member) {
        sendWarningMessage(member);
        auditWarning(member);
        sendWarningAcknowledgement();
        addWarnedRoleToMember(member);
    }

    private void addWarnedRoleToMember(Member member) {
        Util.addRoleToUser(e.getGuild(), member, "warned");
    }

    private void sendWarningAcknowledgement() {
        Messenger.send(e.getChannel(), "Warning has been sent to the user and logged.");
    }

    private void auditWarning(Member member) {
        audit.log(AuditAction.WARNED, e.getAuthor().getIdLong(), member.getIdLong(),
                req.getReason());
    }

    private void sendWarningMessage(Member member) {
        int warnings = getNumberOfCurrentWarnings(member);
        String warning = buildWarning(warnings, req.getReason());
        Messenger.send(member, warning);
    }

    private int getNumberOfCurrentWarnings(Member member) {
        return dsl.fetchCount(dsl.selectFrom(AUDIT).where(AUDIT.MOD_ACTION.eq(AuditAction.WARNED)
                .and(AUDIT.TARGET_USER.eq(member.getIdLong()))));
    }

    private void userError() {
        Messenger.send(e.getChannel(), "Must mention at least one user");
    }

    private static String buildWarning(int warnings, String reason) {
        return "**Warning #" + warnings + 1 + "**\n"
                + "You have been warned by a member of staff. Please read our rules for guidelines of how you should behave.\n "
                + "You have been warned for the following reason: " + reason + "\n\n"
                + "To ensure you have received and understood this, you have been automatically muted. Respond with `ACK` and you will be unmuted.";
    }
}
