package com.jvmrally.lambda.modmail;

import java.time.Instant;
import java.util.Objects;
import java.util.Optional;

import com.jvmrally.lambda.modmail.exception.CouldNotCreateChannelException;
import com.jvmrally.lambda.modmail.exception.CouldNotDeleteChannelException;
import com.jvmrally.lambda.modmail.exception.NoSuchCategoryException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.exceptions.RateLimitedException;

/**
 * ModmailHandler
 */
public class ModmailChannelManagement {

    private static final String CATEGORY_NAME = "reports";
    private static final Logger LOGGER = LogManager.getLogger(ModmailChannelManagement.class);

    private final JDA jda;

    public ModmailChannelManagement(JDA jda) {
        this.jda = Objects.requireNonNull(jda);
    }

    public void deleteChannel(MessageChannel channel, Guild guild) {
        if (verifyModmailChannelCategory(channel, guild)) {
            guild.getTextChannelById(channel.getId()).delete().queue();
        } else {
            throw new CouldNotDeleteChannelException("Could not delete channel " + channel.getName() + " in guild "
                    + guild.getName() + ". Channel is not in category " + CATEGORY_NAME);
        }
    }

    public TextChannel openNewChannel(User user) {
        var category = fetchModmailCategory();
        var channelName = computeCaseChannelName(user);
        try {
            var createdChannel = category.createTextChannel(channelName).complete(true);
            createdChannel.sendMessage(createCaseStartEmbed(user)).queue();
            sendMessageToUser(user,
                    "A report has been opened. JVMRally staff will get back to you as soon as possible. \nAny following messages will also be attached to the report.");
            return createdChannel;
        } catch (RateLimitedException e) {
            throw new CouldNotCreateChannelException("Could not create channel: " + channelName, e);
        }
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

    private String computeCaseChannelName(User user) {
        return user.getAsTag().toLowerCase().replace("#", "").replace(" ", "-");
    }

    public TextChannel getCaseChannel(User user) {
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
        Category category = fetchModmailCategory();
        var textChannels = category.getTextChannels();
        Optional<TextChannel> potentialChannel = textChannels.stream()
                .filter(x -> x.getName().contains(computeCaseChannelName(user))).reduce((x, ignore) -> x);
        return potentialChannel;
    }

    public static void sendMessageToUser(User user, String message) {
        user.openPrivateChannel().queue(directChannel -> directChannel.sendMessage(message).queue(null, exception -> {
            LOGGER.error("Could not send message '{}' to user {}", message, user.getAsTag());
            LOGGER.error(exception);
        }));
    }

}