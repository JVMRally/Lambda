package com.jvmrally.lambda.command.moderation;

import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.jvmrally.lambda.utility.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * DeleteCommand
 */
public class DeleteMessage {
    private static final Logger logger = LogManager.getLogger(DeleteMessage.class);
    private static final int HISTORY_LIMIT = 10000;

    @ParsedEntity
    static class DeleteRequest {
        @Flag(shortName = 'l', longName = "limit",
                description = "The maxmimum number of messages to delete.")
        private Integer limit = 25;
    }

    /**
     * Deletes messages sent by mentioned users from all mentioned channels. Defaults to the last 25
     * messages
     * 
     * @param req the request entity containing command flags and values
     * @param e   the message entity received
     */
    @CommandHandler(commandName = "delete",
            description = "Deletes messages sent by mentioned users from all mentioned channels. Defaults to the last 25 messages.")
    public static void delete(DeleteRequest req, MessageReceivedEvent e) {
        List<TextChannel> channels = Util.getTargetChannels(e);
        for (Member member : e.getMessage().getMentionedMembers()) {
            for (TextChannel channel : channels) {
                deleteMessages(channel, member, req.limit);
            }
        }
    }

    /**
     * Deletes messages sent by a specific users from a specific channel
     * 
     * @param channel the channel to delete messages from
     * @param member  the target user
     * @param limit   the maximum number of messages to delete
     */
    private static void deleteMessages(TextChannel channel, Member member, int limit) {
        List<Message> messages = channel.getIterableHistory().stream().limit(HISTORY_LIMIT)
                .filter(isMemberEqualToAuthor(member)).limit(limit).collect(Collectors.toList());
        logger.info("Deleting {} messages in channel {} by user {}", messages.size(),
                channel.getName(), member.getUser().getName());
        for (Message message : messages) {
            message.delete().queue();
        }
    }

    /**
     * Method to return a predicate for the equailty between a message author and a member
     * 
     * @param member the member to test against the author of the message
     * @return the predicate for message author equailty
     */
    private static Predicate<Message> isMemberEqualToAuthor(Member member) {
        return message -> message.getAuthor().getId().equals(member.getUser().getId());
    }
}
