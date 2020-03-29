package com.jvmrally.lambda.modmail;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * TagHandler
 */
public class ModmailTagHandler {

    private final JDA jda;

    public ModmailTagHandler(JDA jda) {
        this.jda = jda;
    }

    public void clearTags(TextChannel channel) {
        var initialChannelName = channel.getName();
        var newName = initialChannelName.replaceAll("[\\p{P}\\p{S}]", "");

        channel.getManager().setName(newName).submit();
    }

    public void tagChannel(TextChannel channel, String emote) {
        var initialChannelName = channel.getName();
        channel.getManager().setName(emote + initialChannelName).submit();
    }
}