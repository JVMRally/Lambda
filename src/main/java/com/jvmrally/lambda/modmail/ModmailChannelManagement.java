package com.jvmrally.lambda.modmail;

import java.nio.file.Paths;
import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import com.jvmrally.lambda.modmail.exception.ArchivingException;
import com.jvmrally.lambda.modmail.exception.CouldNotCreateChannelException;
import com.jvmrally.lambda.modmail.exception.NoSuchCategoryException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

/**
 * ModmailHandler
 */
public class ModmailChannelManagement {

    private static final String CATEGORY_NAME = "reports";
    private static final Logger LOGGER = LoggerFactory.getLogger(ModmailChannelManagement.class);

    private final JDA jda;

    public ModmailChannelManagement(JDA jda) {
        this.jda = Objects.requireNonNull(jda);
    }

    public void deleteChannel(MessageChannel channel, Guild guild) {
        if (verifyModmailChannelCategory(channel, guild)) {
            guild.getTextChannelById(channel.getId()).delete().queue();
        } else {
            postError(guild.getTextChannelById(channel.getIdLong()),
                    "Could not delete channel, channel is not in Category " + CATEGORY_NAME);
        }
    }

    public TextChannel openNewChannel(User user) {
        var category = fetchModmailCategory();
        var channelName = computeCaseChannelName(user);
        try {
            var createdChannel = category.createTextChannel(channelName).complete(true);
            createdChannel.sendMessage(createCaseStartEmbed(user)).queue();
            return createdChannel;
        } catch (RateLimitedException e) {
            throw new CouldNotCreateChannelException("Could not create channel: " + channelName, e);
        }
    }

    private void postError(TextChannel channel, String message) {
        channel.sendMessage("Error: " + message).queue();
        LOGGER.error(message);
    }

    public static boolean verifyModmailChannelCategory(MessageChannel channel, Guild guild) {
        var categories = guild.getCategoriesByName(CATEGORY_NAME, false);
        return categories.stream().map(category -> category.getTextChannels()).flatMap(channels -> channels.stream())
                .anyMatch(potentialChannel -> potentialChannel.getIdLong() == channel.getIdLong());
    }

    private MessageEmbed createCaseStartEmbed(User user) {
        return new EmbedBuilder().setTitle("Opened case").setColor(0x00FF00).setThumbnail(user.getAvatarUrl())
                .setDescription("Use this channel to send staff messages.").addField("User", user.getAsMention(), false)
                .addField("ID", user.getId(), false).addField("Tag", user.getAsTag(), false).setTimestamp(Instant.now())
                .build();
    }

    public void manageDirectMessage(PrivateMessageReceivedEvent event) {
        var caseChannel = getCaseChannel(event.getAuthor());
        caseChannel.sendMessage(formatDirectMessage(event)).queue();
    }

    private Message formatDirectMessage(PrivateMessageReceivedEvent event) {
        return event.getMessage();
    }

    private String computeCaseChannelName(User user) {
        return user.getAsTag().toLowerCase().replace("#", "");
    }

    private TextChannel getCaseChannel(User user) {
        var potentialChannel = fetchOpenCaseChannel(user);
        if (potentialChannel.isPresent()) {
            return potentialChannel.get();
        }
        return openNewChannel(user);
    }

    private Category fetchModmailCategory() {
        Optional<Category> potentialCategory = jda.getCategoriesByName(CATEGORY_NAME, false).stream()
                .reduce((x, ignore) -> x);

        if (!potentialCategory.isPresent()) {
            throw new NoSuchCategoryException("Could not find a category named: " + CATEGORY_NAME);
        }
        return potentialCategory.get();
    }

    private Optional<TextChannel> fetchOpenCaseChannel(User user) {
        var tag = user.getAsTag();
        Category category = fetchModmailCategory();
        var textChannels = category.getTextChannels();
        Optional<TextChannel> potentialChannel = textChannels.stream()
                .filter(x -> x.getName().contains(computeCaseChannelName(user))).reduce((x, ignore) -> x);
        return potentialChannel;
    }

}