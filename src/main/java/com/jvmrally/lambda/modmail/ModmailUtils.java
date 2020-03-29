package com.jvmrally.lambda.modmail;

import java.util.Optional;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

/**
 * ModmailUtils
 */
public class ModmailUtils {
    public static Optional<TextChannel> getReportsArchiveChannel(Guild guild) {
        return guild.getTextChannelsByName("reports-archive", false).stream().reduce((x, ignore) -> x);
    }

}