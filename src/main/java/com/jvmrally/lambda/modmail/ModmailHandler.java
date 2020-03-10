package com.jvmrally.lambda.modmail;

import java.util.Date;
import java.util.List;
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

    private Optional<TextChannel> fetchOpenCaseChannel(User user, JDA jda) {
        List<Category> categories = jda.getCategoriesByName("reports", false);
        Optional<TextChannel> potentialChannel = categories.stream().map(x -> x.getTextChannels())
                .flatMap(x -> x.stream()).filter(x -> x.getName().contains(user.getAsTag())).reduce((x, ignore) -> x);
        return potentialChannel;
    }

}