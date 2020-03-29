package com.jvmrally.lambda.command.moderation;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.modmail.*;

import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.MessageChannel;
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

    @CommandHandler(commandName = "modmail.open", description = "If not already opened, opens a modmail channel for each given id.", roles = "admin")
    public static void open(List<String> userIds, MessageReceivedEvent e) {
        List<Guild> guilds = e.getJDA().getGuilds();
        for (var userId : userIds) {
            fetchUser(userId, guilds).ifPresentOrElse(user -> new ModmailHandler(e.getJDA()).openNewChannel(user),
                    () -> sendError("Cannot open channel: Cannot find user " + userId, e));
        }
    }

    @CommandHandler(commandName = "modmail.close", description = "Closes the current modmail channel.")
    public static void close(MessageReceivedEvent e) {
        new ModmailHandler(e.getJDA()).deleteChannel(e.getChannel(), e.getGuild());
    }

    @CommandHandler(commandName = "modmail.note", description = "Posts a note embed in the current channel.")
    public static void note(List<String> args, MessageReceivedEvent e) {
        var content = new StringBuilder();
        args.forEach(word -> content.append(content.length() == 0 ? "" : " ").append(word));
        new ModmailNoteHandler().postNote(e.getChannel(), e.getAuthor(), content.toString());
    }

    @CommandHandler(commandName = "modmail.tag", description = "Prepends an emote to the channel name.")
    public static void tag(List<String> args, MessageReceivedEvent e) {
        var modmailTagHandler = new ModmailTagHandler(e.getJDA());
        args.forEach(emote -> modmailTagHandler.tagChannel(e.getTextChannel(), emote));
    }

    @CommandHandler(commandName = "modmail.cleartags", description = "Clears all tags from the channel")
    public static void clearTags(MessageReceivedEvent e) {
        new ModmailTagHandler(e.getJDA()).clearTags(e.getTextChannel());
    }

    @CommandHandler(commandName = "modmail.archive", description = "Clears all tags from the channel")
    public static void archive(MessageReceivedEvent e) {
        new ModmailChannelArchiver(e.getTextChannel(), e.getGuild()).archive();
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
