package com.jvmrally.lambda.command.moderation.modmail;

import java.util.List;
import java.util.Optional;

import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.modmail.ModmailHandler;

import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * ModMail
 */
public class ModMail extends Command {

    private static final Logger LOGGER = LogManager.getLogger(ModMail.class);

    private ModMail(MessageReceivedEvent e) {
        super(e);
    }

    // FIXME: Does not open channels
    @CommandHandler(commandName = "modmail.open", description = "If not already opened, opens a modmail channel for each given id.", roles = "admin")
    public static void open(List<String> userIds, MessageReceivedEvent e) {
        List<Guild> guilds = e.getJDA().getGuilds();
        for (var userId : userIds) {
            fetchUser(userId, guilds).ifPresentOrElse(user -> new ModmailHandler(e.getJDA()).openNewChannel(user),
                    () -> sendError("Cannot open channel: Cannot find user " + userId, e));
        }

    }

    // FIXME: throws NPE when id is unknown/not in any server
    private static Optional<User> fetchUser(String userId, List<Guild> guilds) {
        return guilds.stream().map(guild -> guild.getMemberById(userId)).map(member -> member.getUser())
                .reduce((result, ignore) -> result);
    }

    private static void sendError(String message, MessageReceivedEvent e) {
        LOGGER.warn(message);
        e.getChannel().sendMessage(message).queue();
    }

}
