package com.jvmrally.lambda.modmail;

import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import com.jvmrally.lambda.command.moderation.ModMail;
import com.jvmrally.lambda.modmail.exception.ArchivingException;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;

/**
 * ChannelArchiver
 */
public class ModmailChannelArchiver {
    private final TextChannel channel;
    private final Guild guild;

    public ModmailChannelArchiver(TextChannel channel, Guild guild) {
        this.channel = channel;
        this.guild = guild;
    }

    public void archive(String note) {
        if (ModmailChannelManagement.verifyModmailChannelCategory(channel, guild)) {
            var archive = collectMessages(channel).setNote(note);
            var archiveChannel = ModmailUtils.findTextChannelByName("reports-archive", guild)
                    .orElseThrow(() -> new ArchivingException("message"));
            archive.saveToChannel(archiveChannel);
        } else {
            throw new ArchivingException("Channel is not a modmailchannel");
        }
    }

    public void archive() {
        archive(null);
    }

    private ArchivedChannel collectMessages(TextChannel channel) {
        var archivedChannel = new ArchivedChannel();
        channel.getIterableHistory().stream().forEach(archivedChannel::addMessage);

        return archivedChannel;
    }

    public static class ArchivedChannel {
        private List<Message> messages = new ArrayList<>();
        private String note = "";

        public void saveToChannel(TextChannel channel) {
            channel.sendMessage(generateInfoEmbed()).addFile(serializeContent().getBytes(), "log.txt").queue();
        }

        public ArchivedChannel setNote(String note) {
            this.note = Objects.requireNonNull(note);
            return this;
        }

        private MessageEmbed generateInfoEmbed() {
            var id = getFirstMessage().getEmbeds().get(0).getFields().stream()
                    .filter(field -> field.getName().contains("ID")).map(field -> field.getValue())
                    .reduce((result, ignore) -> result)
                    .orElseThrow(() -> new IllegalStateException("Field 'ID' does not exist"));

            return new EmbedBuilder().setTitle("Log").setDescription("See the attached file for more details.")
                    .setColor(0x00FF00).addField("User", "<@" + id + ">", false).addField("ID", id, false)
                    .addField("Note", note, false).build();

        }

        private String serializeContent() {
            return messages.stream().map(this::serializeMessage).reduce((x, acc) -> x + '\n' + acc)
                    .orElseThrow(() -> new ArchivingException("Could not serialize messages"));
        }

        private String serializeMessage(Message message) {
            String serializedMessage = "";
            if (isModeratorAnswer(message)) {
                serializedMessage = serializeModeratorAnswer(message);
            } else if (isNote(message)) {
                serializedMessage = serializeNote(message);
            } else if (isCommand(message)) {
                serializedMessage = serializeCommand(message);
            } else if (isParticipiantMessage(message)) {
                serializedMessage = serializeParticipiantMessage(message);
            }
            return serializedMessage;
        }

        private String serializeParticipiantMessage(Message message) {
            String author = getParticipiantIdentification();

            return String.format("[%s][%s][%s]: %s", formatDate(message.getTimeCreated()), author, "MESSAGE",
                    message.getContentRaw());
        }

        private boolean isParticipiantMessage(Message message) {
            return message.getAuthor().isBot() && message.getEmbeds().isEmpty();
        }

        private String serializeCommand(Message message) {
            return formatMessage(formatDate(message.getTimeCreated()), message.getAuthor(), "COMMAND",
                    message.getContentRaw());
        }

        private boolean isCommand(Message message) {
            return message.getContentRaw().contains(ModMail.Constants.MODMAIL_COMMAND_PREFIX);
        }

        private String serializeNote(Message message) {
            var time = formatDate(message.getTimeCreated());
            var type = "NOTE";
            var content = message.getEmbeds().get(0).getFields().stream()
                    .filter(field -> field.getName().contains("Content")).map(field -> field.getValue())
                    .reduce((result, ignore) -> result)
                    .orElseThrow(() -> new IllegalStateException("Field 'Content' does not exist in embed"));

            return formatEmbed(time, type, content);
        }

        private boolean isNote(Message message) {
            return message.getEmbeds().stream().anyMatch(embed -> embed.getTitle().contains("Note"));
        }

        private boolean isModeratorAnswer(Message message) {
            return !message.getAuthor().isBot()
                    && !message.getContentRaw().contains(ModMail.Constants.MODMAIL_COMMAND_PREFIX);
        }

        private String serializeModeratorAnswer(Message message) {
            return formatMessage(formatDate(message.getTimeCreated()), message.getAuthor(), "ANSWER",
                    message.getContentRaw());
        }

        private Message getFirstMessage() {
            return messages.get(messages.size() - 1);
        }

        private String getParticipiantIdentification() {
            var infoEmbed = getFirstMessage().getEmbeds().get(0);
            var tag = infoEmbed.getFields().stream().filter(field -> field.getName().contains("Tag"))
                    .map(field -> field.getValue()).reduce((result, ignore) -> result)
                    .orElseThrow(() -> new IllegalStateException("Field 'Tag' does not exist"));
            var id = infoEmbed.getFields().stream().filter(field -> field.getName().contains("ID"))
                    .map(field -> field.getValue()).reduce((result, ignore) -> result)
                    .orElseThrow(() -> new IllegalStateException("Field 'ID' does not exist"));
            return tag + '|' + id;
        }

        private void addMessage(Message message) {
            messages.add(message);
        }

        private String formatEmbed(String time, String type, String content) {
            return String.format("[%s][%s][%s]: %s", time, "BOT", type, content);
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
    }
}
