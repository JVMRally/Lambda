package com.jvmrally.lambda.modmail;

import java.util.Optional;

import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.TextChannel;

import org.apache.logging.log4j.Logger;

/**
 * ModmailUtils
 */
public class ModmailUtils {
    public static Optional<TextChannel> findTextChannelByName(String channelname, Guild guild) {
        return guild.getTextChannelsByName(channelname, false).stream().reduce((result, ignore) -> result);
    }

}