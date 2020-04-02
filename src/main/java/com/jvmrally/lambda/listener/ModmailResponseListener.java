package com.jvmrally.lambda.listener;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.MessageEmbed.Field;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * ModmailResponseListener
 */
public class ModmailResponseListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        var modMessage = event.getMessage();
        if (isInModMailChannel(event) && !event.getAuthor().isBot() && messageIsNotCommand(modMessage)) {
            var participiant = getModMailParticipiant(event.getJDA(), getParticipiantId(event.getTextChannel()));
            participiant.openPrivateChannel().queue(channel -> {
                channel.sendMessage("Staff reply:").queue(null, (ignore) -> modMessage.addReaction("❌").queue());
                channel.sendMessage(modMessage).queue();
            });
        }
    }

    private boolean isInModMailChannel(MessageReceivedEvent event) {
        var categories = event.getGuild().getCategoriesByName("reports", true);
        return categories.stream().map(category -> category.getChannels()).flatMap(channels -> channels.stream())
                .anyMatch(channel -> channel.getIdLong() == event.getChannel().getIdLong());
    }

    private boolean messageIsNotCommand(Message message) {
        return !message.getContentRaw().trim().startsWith("!modmail");
    }

    private User getModMailParticipiant(JDA jda, long participiantId) {
        return jda.getGuilds().stream().map(guild -> guild.getMemberById(participiantId))
                .reduce((result, ignore) -> result)
                .orElseThrow(() -> new IllegalStateException("Could not find user with ID: " + participiantId))
                .getUser();
    }

    private long getParticipiantId(TextChannel modmailChannel) {
        return modmailChannel.getHistoryFromBeginning(1).complete().getRetrievedHistory().get(0).getEmbeds().stream()
                .limit(1).map(embed -> embed.getFields()).flatMap(fields -> fields.stream())
                .filter(field -> field.getName().equals("ID")).map(Field::getValue).map(Long::parseLong)
                .reduce((result, ignore) -> result)
                .orElseThrow(() -> new IllegalStateException(
                        "Could not find embed with field 'ID' in the first message of channel: "
                                + modmailChannel.getName()));
    }
}