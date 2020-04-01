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
        return guild.getTextChannelsByName("reports-archive", false).stream().reduce((x, ignore) -> x);
    }

    public static class Constants {
        public static final String MODMAIL_COMMAND_PREFIX = "!modmail";
    }

    public static void logInfo(Logger logger, String message) {
        logger.info(String.format("[%s] %s", "modmail", message));
    }

}