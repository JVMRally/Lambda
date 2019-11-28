package com.jvmrally.lambda.listener;

import com.jvmrally.lambda.utility.messaging.Messenger;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.guild.react.GuildMessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * SuggestionApproval
 */
public class SuggestionApproval extends ListenerAdapter {

    private GuildMessageReactionAddEvent e;

    @Override
    public void onGuildMessageReactionAdd(GuildMessageReactionAddEvent e) {
        this.e = e;
        execute();
    }

    private void execute() {
        if (e.getUser().isBot()) {
            return;
        }
        if (channelIsCorrect()) {
            String emoji = e.getReactionEmote().getEmoji();
            if (emoji.equals("👍")) {
                postSuggestion();
                deleteSuggestion();
            } else if (emoji.equals("👎")) {
                denySuggestion();
            }
        }
    }

    private void postSuggestion() {
        Message message = e.getChannel().retrieveMessageById(e.getMessageId()).complete();
        var embeds = message.getEmbeds();
        for (MessageEmbed embed : embeds) {
            var channels = e.getGuild().getTextChannelsByName("suggestions", true);
            if (channels.size() == 1) {
                sendMessage(embed, channels.get(0));
            }
        }
    }

    private void sendMessage(MessageEmbed embed, TextChannel channel) {
        Message returnedMessage = Messenger.sendReturning(channel, embed);
        returnedMessage.addReaction("👍").queue();
        returnedMessage.addReaction("👎").queue();
    }

    private void deleteSuggestion() {
        e.getChannel().deleteMessageById(e.getMessageId()).queue();
    }

    private void denySuggestion() {
        Message message = e.getChannel().retrieveMessageById(e.getMessageId()).complete();
        var embeds = message.getEmbeds();
        for (MessageEmbed embed : embeds) {
            editEmbed(message, embed);
        }
    }

    private void editEmbed(Message message, MessageEmbed embed) {
        MessageEmbed newEmbed = new EmbedBuilder().setTitle("DENIED: " + embed.getTitle())
                .setDescription(embed.getDescription()).build();
        message.editMessage(newEmbed).queue();
        message.clearReactions().queue();
    }

    private boolean channelIsCorrect() {
        return e.getChannel().getName().equalsIgnoreCase("approvals");
    }

}
