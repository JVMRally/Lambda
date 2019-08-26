package com.jvmrally.lambda.listener;

import java.awt.Color;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.injectable.Auditor;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.Message.Attachment;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * SpamListener
 */
public class SpamListener extends ListenerAdapter {

    private static final Logger logger = LogManager.getLogger(SpamListener.class);

    private static final String ATTACHMENT_FILTER = "^.*\\.(jpg|jpeg|gif|png|mov|mp4|webm)";

    public SpamListener() {
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot() || Util.hasRole(e.getMember(), "Admin")) {
            return;
        }
        Message message = e.getMessage();
        logger.info("Attachments: {}", message.getAttachments().size());
        logger.info("Attachment name: {}", message.getAttachments().stream()
                .map(Attachment::getFileName).collect(Collectors.joining(" - ")));
        logger.info("Mentions: {}", message.getMentionedMembers().size());
        logger.info("Channels: {}", message.getMentionedChannels().size());
        logger.info("Invites: {}", message.getInvites());
        if (testMessage(message, getMessagePredicates())) {
            message.delete().queue();
            Messenger.toUser(m -> m.to(e.getMember()).message(getWarning()));
            Auditor.getAuditor().log(AuditAction.AUTOMATED_WARN,
                    e.getJDA().getSelfUser().getIdLong(), e.getMember().getIdLong(),
                    "Automatic warning from spam prevention.");
        }
    }

    private EmbedBuilder getWarning() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**Warning**");
        eb.setColor(Color.RED);
        eb.setDescription("Your last message was deleted by our spam filter.");
        eb.addField("Invites", "We don't allow advertising. Don't post invite links.", false);
        eb.addField("Channel Mentions", "Don't spam channel mentions in messages.", false);
        eb.addField("User Mentions", "Don't spam user mentions in messages.", false);
        eb.addField("Attachments",
                "We only allow images and videos as attachments. If you're trying to post some code, please use a code sharing site and post the link.",
                false);
        return eb;
    }

    private boolean testMessage(Message message, List<Predicate<Message>> predicates) {
        for (var predicate : predicates) {
            if (predicate.test(message)) {
                return true;
            }
        }
        return false;
    }

    private List<Predicate<Message>> getMessagePredicates() {
        List<Predicate<Message>> messagePredicates = new ArrayList<>();
        messagePredicates.add(Message::mentionsEveryone);
        messagePredicates.add(m -> m.getMentionedUsers().size() > 4);
        messagePredicates.add(m -> m.getMentionedChannels().size() > 4);
        messagePredicates.add(m -> !m.getInvites().isEmpty());
        messagePredicates.add(m -> !m.getAttachments().stream()
                .filter(attachment -> !attachment.getFileName().matches(ATTACHMENT_FILTER))
                .collect(Collectors.toList()).isEmpty());
        return messagePredicates;
    }

}
