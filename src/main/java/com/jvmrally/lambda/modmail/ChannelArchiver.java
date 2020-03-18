package com.jvmrally.lambda.modmail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.OffsetDateTime;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jvmrally.lambda.modmail.exception.ArchivingException;

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
        private OffsetDateTime lastMessage;

        private String determineFileName() {

            return String.format("[%s][ID=%s]");
        }

        public Path saveInDirectory(Path directory) {
            if (!Files.isDirectory(directory)) {
                throw new ArchivingException(directory.toAbsolutePath().toUri() + " is not a directory");
            }

            var filePath = Paths.get(directory.toAbsolutePath().toString(), computeFileName());

            if (Files.exists(filePath)) {
                throw new ArchivingException(directory.toAbsolutePath().toUri() + " already exists");
            }

            String content = messages.stream().reduce((x, acc) -> x + '\n' + acc).orElseGet(() -> "");
            try {
                Files.createFile(filePath);
                Files.writeString(filePath, content, StandardOpenOption.APPEND);
            } catch (IOException e) {
                throw new ArchivingException("Could not write to file", e);
            }
            return filePath;
        }

        private Map<String, String> getUserData() {
            Map<String, String> data = new HashMap<>();
            firstMessage.getEmbeds().stream().map(embed -> embed.getFields()).flatMap(fields -> fields.stream())
                    .forEach(field -> data.put(field.getName(), field.getValue()));
            return Collections.unmodifiableMap(data);
        }

        private String computeFileName() {
            var userData = getUserData();
            return String.format("[TAG:%s][ID:%s][BEG:%s][END:%s]", userData.get("Tag"), userData.get("ID"),
                    formatDate(firstMessage.getTimeCreated()), formatDate(lastMessage));
        }

        private void setFirstMessageIfOlder(Message message) {
            if (firstMessage == null) {
                firstMessage = message;
            } else if (firstMessage.getTimeCreated().isAfter(message.getTimeCreated())) {
                firstMessage = message;
            }
        }

        private void setLastMessageIfNewer(Message message) {
            if (lastMessage == null) {
                lastMessage = message.getTimeCreated();
            } else if (lastMessage.isBefore(message.getTimeCreated())) {
                lastMessage = message.getTimeCreated();
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
