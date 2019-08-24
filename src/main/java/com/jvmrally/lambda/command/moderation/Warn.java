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

	/**
	 * Gives a warning to a user
	 * 
	 * @param req the request entity containing command flags and values
	 * @param e   the message entity received
	 */
	@CommandHandler(commandName = "warn",
			description = "Give an official warning to a user. The user will receive a direct message informing them of the reason.")
	public static void warn(DSLContext dsl, Auditor auditor, ReasonRequest req,
			MessageReceivedEvent e) {
		Util.getMentionedMember(e).ifPresentOrElse(member -> {
			int warnings = dsl.fetchCount(dsl.selectFrom(AUDIT).where(AUDIT.MOD_ACTION
					.eq(AuditAction.WARNED).and(AUDIT.TARGET_USER.eq(member.getIdLong()))));
			String message = buildWarning(warnings, req.getReason());
			Messenger.toUser(messenger -> messenger.to(member).message(message));
			auditor.log(AuditAction.WARNED, e.getAuthor().getIdLong(), member.getIdLong(),
					req.getReason());
		}, () -> Messenger.toChannel(
				messenger -> messenger.to(e.getChannel()).message("Must provide a user")));
	}

	private static String buildWarning(int warnings, String reason) {
		return "**Warning #" + warnings + "\n"
				+ "You have been warned by a member of staff. Please read our rules for guidelines of how you should behave.\n "
				+ "You have been warned for the following reason: " + reason;
	}
}
