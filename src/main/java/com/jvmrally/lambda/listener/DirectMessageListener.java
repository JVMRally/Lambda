package com.jvmrally.lambda.listener;

import static com.jvmrally.lambda.db.tables.DmTimeouts.DM_TIMEOUTS;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import com.jvmrally.lambda.db.tables.pojos.DmTimeouts;
import com.jvmrally.lambda.injectable.JooqConn;
import com.jvmrally.lambda.modmail.ModmailCommunicationHandler;
import com.jvmrally.lambda.utility.Util;
import org.jooq.DSLContext;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * DirectMessageListener
 */
public class DirectMessageListener extends ListenerAdapter {
    private static DSLContext dsl = JooqConn.getJooqContext();

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        if (e.getAuthor().isBot()) {
            return;
        }
        if (e.getMessage().getContentRaw().equalsIgnoreCase("ACK")) {
            handleWarningAcknowledgement(e);
            return;
        }
        handleModmailMessage(e);
    }

    private void handleModmailMessage(PrivateMessageReceivedEvent e) {
        long authorId = e.getAuthor().getIdLong();
        long now = System.currentTimeMillis();
        getUserMessageTimeouts(authorId).ifPresentOrElse(timeout -> updateResponseTimeout(e, now, timeout),
                () -> insertNewResponseTimeout(e, authorId, now));
        logMessage(e);
    }

    private void insertNewResponseTimeout(PrivateMessageReceivedEvent e, long authorId, long now) {
        insertTimeout(authorId, now);
        sendAcknowledgement(e);
    }

    private Optional<DmTimeouts> getUserMessageTimeouts(long authorId) {
        return dsl.selectFrom(DM_TIMEOUTS).where(DM_TIMEOUTS.USERID.eq(authorId)).fetchOptionalInto(DmTimeouts.class);
    }

    private void handleWarningAcknowledgement(PrivateMessageReceivedEvent e) {
        for (Guild guild : e.getJDA().getGuilds()) {
            Util.removeRoleFromUser(guild, guild.getMember(e.getAuthor()), "warned");
        }
        logMessage(e);
    }

    private void updateResponseTimeout(PrivateMessageReceivedEvent e, long now, DmTimeouts timeout) {
        updateTimeout(timeout, now);
        if (now - timeout.getLastMessageTime() > TimeUnit.HOURS.toMillis(24)) {
            sendAcknowledgement(e);
        }
    }

    /**
     * Send achknowledgement message to user
     * 
     * @param e the event to respond to
     */
    private void sendAcknowledgement(PrivateMessageReceivedEvent e) {
        e.getChannel().sendMessage(
                "Thank you for your message. Our staff will get back to you asap. Subsequent replies will also be forwarded to staff.");
    }

    /**
     * Updates the time of the last message sent by the user
     * 
     * @param timeout the existing timeout record
     * @param now     the current time in milliseconds from the epoch
     */
    private void updateTimeout(DmTimeouts timeout, long now) {
        dsl.update(DM_TIMEOUTS).set(DM_TIMEOUTS.LAST_MESSAGE_TIME, now)
                .where(DM_TIMEOUTS.USERID.eq(timeout.getUserid())).execute();
    }

    /**
     * Inserts a new record of a timeout for a user
     * 
     * @param authorId the userid
     * @param now      the current time in milliseconds from the epoch
     */
    private void insertTimeout(long authorId, long now) {
        dsl.insertInto(DM_TIMEOUTS).values(authorId, now).execute();
    }

    /**
     * Sends the received message to any modmail channel the bot has access to
     * 
     * @param e the received message event
     */
    private void logMessage(PrivateMessageReceivedEvent e) {
        new ModmailCommunicationHandler().handleDirectMessage(e);
    }
}
