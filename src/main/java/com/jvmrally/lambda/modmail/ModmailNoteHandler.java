package com.jvmrally.lambda.modmail;

import java.time.Instant;
import java.time.temporal.TemporalAccessor;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

/**
 * ModmailNoteHandler
 */
public class ModmailNoteHandler {

    public void postNote(MessageChannel channel, User member, String content) {
        var note = new Note(member, content, Instant.now());
        channel.sendMessage(note.toMessageEmbed()).queue();
    }

    public static class Note {

        private final User creator;
        private final String content;
        private final TemporalAccessor timeStamp;

        public Note(User creator, String content, TemporalAccessor timeStamp) {
            this.creator = creator;
            this.content = content;
            this.timeStamp = timeStamp;
        }

        public MessageEmbed toMessageEmbed() {
            return new EmbedBuilder().setTitle("Note").addField("Creator", creator.getAsMention(), false)
                    .addField("Content", content, false).setTimestamp(timeStamp).setColor(0xFFFF00).build();
        }
    }
}