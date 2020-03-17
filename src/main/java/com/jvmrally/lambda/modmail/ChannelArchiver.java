package com.jvmrally.lambda.modmail;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * ChannelArchiver
 */
public class ChannelArchiver {
    private final TextChannel channel;

    public ChannelArchiver(TextChannel channel) {
        this.channel = channel;
    }

    public ArchivedChannel archive() {
        var archivedChannel = new ArchivedChannel();
        channel.getIterableHistory().stream().forEach(archivedChannel::addMessage);
        archivedChannel.setChannelName(channel.getName());

        return archivedChannel;
    }

    public static class ArchivedChannel {
        private String channelName;
        private List<String> messages = new ArrayList<>();
        private Message firstMessage;
        private Message lastMessage;

        private void setFirstMessageIfOlder(Message message) {
            if (firstMessage == null) {
                firstMessage = message;
            } else if (firstMessage.getTimeCreated().isAfter(message.getTimeCreated())) {
                firstMessage = message;
            }

        }

        private void setLastMessageIfNewer(Message message) {
            if (lastMessage == null) {
                lastMessage = message;
            } else if (lastMessage.getTimeCreated().isBefore(message.getTimeCreated())) {
                lastMessage = message;
            }
        }

        private void setChannelName(String channelName) {
            this.channelName = channelName;
        }

        private void addMessage(Message message) {
            setFirstMessageIfOlder(message);
            setLastMessageIfNewer(message);
            if (message.getAuthor().isBot()) {

            } else {
                messages.add(userMessageToString(message));
            }
        }

        private String userMessageToString(Message message) {
            String time = formatDate(message.getTimeCreated());
            User author = message.getAuthor();
            String type = determineTypeOfMessage(message.getContentRaw());
            String content = message.getContentRaw();
            return formatMessage(time, author, type, content);
        }

        private String formatMessage(String time, User author, String type, String content) {
            return String.format("[%s][%s|%s][%s]: %s", time, author.getAsTag(), author.getId(), type, content);
        }

        private String formatDate(TemporalAccessor time) {
            return String.format("%04d-%02d-%02d %02d:%02d:%02d", time.get(ChronoField.YEAR),
                    time.get(ChronoField.MONTH_OF_YEAR), time.get(ChronoField.DAY_OF_MONTH),
                    time.get(ChronoField.HOUR_OF_DAY), time.get(ChronoField.MINUTE_OF_HOUR),
                    time.get(ChronoField.SECOND_OF_MINUTE));
        }

        private String determineTypeOfMessage(String content) {
            if (content.contains("!modmail")) {
                return "COMMAND";
            }
            return "MESSAGE";
        }

    }
}
