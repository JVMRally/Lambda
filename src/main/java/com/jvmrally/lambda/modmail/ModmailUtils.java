package com.jvmrally.lambda.modmail;

import java.util.Optional;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import org.apache.logging.log4j.Logger;

/**
 * ModmailUtils
 */
public class ModmailUtils {

    public static Optional<TextChannel> getReportsArchiveChannel(Guild guild) {
        return getChannelByName("reports-archive", guild);
    }

    public static Optional<TextChannel> getChannelByName(String channelname, Guild guild) {
        return guild.getTextChannelsByName(channelname, false).stream().reduce((result, ignore) -> result);
    }

    public static void logInfo(Logger logger, String message) {
        logger.info(String.format("[%s] %s", "modmail", message));
    }

}