package com.jvmrally.lambda.modmail.entities;

import java.time.temporal.TemporalAccessor;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.User;

/**
 * ModmailNote
 */
public class Note {

    private final User creator;
    private final String content;
    private final TemporalAccessor timeStamp;

    public Note(User creator, String content, TemporalAccessor timeStamp) {
        this.creator = creator;
        this.content = content;
        this.timeStamp = timeStamp;
    }

    public User getCreator() {
        return creator;
    }

    public String getContent() {
        return content;
    }

    public TemporalAccessor getTimeStamp() {
        return timeStamp;
    }

    public MessageEmbed toMessageEmbed() {
        return new EmbedBuilder().setTitle("Note").addField("Creator", creator.getAsMention(), false)
                .addField("Content", content, false).setTimestamp(timeStamp).setColor(0xFFFF00).build();
    }
}