package com.jvmrally.lambda.listener;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.injectable.Auditor;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * SpamListener
 */
public class SpamListener extends ListenerAdapter {

    public SpamListener() {
    }

    private static final String WARNING =
            "Your last message was deleted by our spam filter. Try not to ping everyone, mention lots of users, or post invite links to other servers.";

    @Override
    public void onMessageReceived(MessageReceivedEvent e) {
        if (Util.hasRole(e.getMember(), "Admin") || e.getAuthor().isBot()) {
            return;
        }
        Message message = e.getMessage();
        if (testMessage(message, getMessagePredicates())) {
            message.delete().queue();
            Messenger.toUser(m -> m.to(e.getMember()).message(WARNING));
            Auditor.getAuditor().log(AuditAction.WARNED, e.getJDA().getSelfUser().getIdLong(),
                    e.getMember().getIdLong(), "Automatic warning from spam prevention.");
        }
    }

    private boolean testMessage(Message message, List<Predicate<Message>> predicates) {
        boolean result = false;
        for (var predicate : predicates) {
            result = predicate.test(message);
        }
        return result;
    }

    private List<Predicate<Message>> getMessagePredicates() {
        List<Predicate<Message>> messagePredicates = new ArrayList<>();
        messagePredicates.add(Message::mentionsEveryone);
        messagePredicates.add(m -> m.getMentionedUsers().size() > 4);
        messagePredicates.add(m -> m.getMentionedChannels().size() > 4);
        messagePredicates.add(m -> !m.getInvites().isEmpty());
        return messagePredicates;
    }

}
