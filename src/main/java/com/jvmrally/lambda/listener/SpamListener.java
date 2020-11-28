package com.jvmrally.lambda.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.injectable.Auditor;
import com.jvmrally.lambda.utility.Util;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * SpamListener
 */
public class SpamListener extends ListenerAdapter {

    private static final String ATTACHMENT_FILTER = "^.*\\.(jpg|jpeg|gif|png|mov|mp4|webm)";

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (e.getAuthor().isBot() || Util.hasRole(e.getMember(), "Admin")) {
            return;
        }
        Message message = e.getMessage();
        if (testMessage(message, getMessagePredicates())) {
            message.delete().queue();
            Auditor.getAuditor().log(AuditAction.AUTOMATED_WARN,
                    e.getJDA().getSelfUser().getIdLong(), e.getMember().getIdLong(),
                    "Automatic warning from spam prevention.", e.getGuild().getIdLong());
        }
    }

    /**
     * Tests the input message against a collection of predicates
     * 
     * @param message    the message to test
     * @param predicates the predicates to test against
     * @return true if a message evaluates truthfully against one of the predicates
     */
    private boolean testMessage(final Message message, final List<Predicate<Message>> predicates) {
        return predicates.stream().anyMatch(e -> e.test(message));
    }

    /**
     * Returns a list of message predicates
     * 
     * @return the list of predicates
     */
    private List<Predicate<Message>> getMessagePredicates() {
        List<Predicate<Message>> messagePredicates = new ArrayList<>();
        messagePredicates.add(Message::mentionsEveryone);
        messagePredicates.add(m -> m.getMentionedUsers().size() > 4);
        messagePredicates.add(m -> m.getMentionedChannels().size() > 4);
        messagePredicates.add(m -> !m.getInvites().isEmpty());
        messagePredicates.add(m -> !m.getAttachments().stream().filter(
                attachment -> !attachment.getFileName().toLowerCase().matches(ATTACHMENT_FILTER))
                .collect(Collectors.toList()).isEmpty());
        return messagePredicates;
    }
}
