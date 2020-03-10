package com.jvmrally.lambda.modmail;

import java.nio.channels.UnsupportedAddressTypeException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.Category;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.Invite.Channel;
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;

/**
 * ModmailHandler
 */
public class ModmailHandler {

    private final JDA jda;

    public ModmailHandler(JDA jda) {
        this.jda = Objects.requireNonNull(jda);
    }

    private Category fetchModmailCategory() {
        final String CATEGORY_NAME = "reports";
        Optional<Category> potentialCategory = jda.getCategoriesByName(CATEGORY_NAME, false).stream()
                .reduce((x, ignore) -> x);

        if (!potentialCategory.isPresent()) {
            throw new NoSuchCategoryException("Could not find a category named: " + CATEGORY_NAME);
        }
        return potentialCategory.get();
    }

    private Optional<TextChannel> fetchOpenCaseChannel(User user) {
        Category category = fetchModmailCategory();
        Optional<TextChannel> potentialChannel = category.getTextChannels().stream()
                .filter(x -> x.getName().contains(user.getAsTag())).reduce((x, ignore) -> x);
        return potentialChannel;
    }

}