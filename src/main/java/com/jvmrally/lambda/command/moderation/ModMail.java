package com.jvmrally.lambda.command.moderation;

import java.util.List;
import java.util.stream.Collectors;

import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.modmail.*;

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

    @CommandHandler(commandName = "modmail.open", description = "If not already opened, opens a modmail channel for each given id.", roles = "admin")
    public static void open(List<String> userIds, MessageReceivedEvent e) {
        logCommand(e);
        List<Guild> guilds = e.getJDA().getGuilds();
        for (var userId : userIds) {
            new ModmailChannelManagement(e.getJDA()).openNewChannel(getUser(userId, guilds));
        }
    }

    @CommandHandler(commandName = "modmail.close", description = "Closes the current modmail channel.")
    public static void close(MessageReceivedEvent e) {
        logCommand(e);
        new ModmailChannelManagement(e.getJDA()).deleteChannel(e.getChannel(), e.getGuild());
    }

    @CommandHandler(commandName = "modmail.note", description = "Posts a note embed in the current channel.")
    public static void note(List<String> args, MessageReceivedEvent e) {
        logCommand(e);
        var content = new StringBuilder();
        args.forEach(word -> content.append(content.length() == 0 ? "" : " ").append(word));
        new ModmailNoteHandler().postNote(e.getChannel(), e.getAuthor(), content.toString());
    }

    @CommandHandler(commandName = "modmail.tag", description = "Prepends an emote to the channel name.")
    public static void tag(List<String> args, MessageReceivedEvent e) {
        logCommand(e);
        var modmailTagHandler = new ModmailTagHandler();
        args.forEach(emote -> modmailTagHandler.tagChannel(e.getTextChannel(), emote));
    }

    @CommandHandler(commandName = "modmail.cleartags", description = "Clears all tags from the channel")
    public static void clearTags(MessageReceivedEvent e) {
        logCommand(e);
        new ModmailTagHandler().clearTags(e.getTextChannel());
    }

    @CommandHandler(commandName = "modmail.archive", description = "Posts a log file to the reports-archive channel")
    public static void archive(List<String> args, MessageReceivedEvent e) {
        logCommand(e);
        var additionalNote = new StringBuilder();
        args.forEach(word -> additionalNote.append(additionalNote.length() == 0 ? "" : " ").append(word));
        new ModmailChannelArchiver(e.getTextChannel(), e.getGuild()).archive(additionalNote.toString());
    }

    private static User getUser(String userId, List<Guild> guilds) {
        List<User> foundUsers = guilds.stream().map(guild -> guild.getMemberById(userId))
                .map(member -> member.getUser()).collect(Collectors.toList());

        if (foundUsers.isEmpty()) {
            throw new IllegalStateException(
                    "Cannot find user " + userId + ". User with id does not share any server with the bot.");
        }

        return foundUsers.get(0);
    }

    private static void logCommand(MessageReceivedEvent e) {
        ModmailUtils.logInfo(LOGGER, String.format("%s in channel %s: %s", e.getAuthor().getAsTag(),
                e.getChannel().getName(), e.getMessage().getContentRaw()));
    }

}
