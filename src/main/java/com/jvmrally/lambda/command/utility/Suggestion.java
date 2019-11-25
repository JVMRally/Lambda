package com.jvmrally.lambda.command.utility;

import java.util.List;
import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Suggestion
 */
public class Suggestion extends Command {

    private String message;

    private Suggestion(MessageReceivedEvent e, List<String> args) {
        super(e);
        this.message = Util.rebuildArgsToString(args);
    }

    @CommandHandler(commandName = "suggest", description = "Submit a suggestion for approval")
    public static void execute(MessageReceivedEvent e, List<String> args) {
        new Suggestion(e, args).execute();
    }

    private void execute() {
        var channels = getApprovalChannels();
        MessageEmbed embed = buildEmbedMessage();
        for (TextChannel channel : channels) {
            Message m = sendAndReturnMessage(embed, channel);
            addReactions(m);
        }
    }

    private void addReactions(Message m) {
        m.addReaction("👍").queue();
        m.addReaction("👎").queue();
    }

    private Message sendAndReturnMessage(MessageEmbed embed, TextChannel channel) {
        return Messenger.sendReturning(channel, embed);
    }

    private MessageEmbed buildEmbedMessage() {
        return new EmbedBuilder().setTitle(e.getAuthor().getName() + "'s suggestion")
                .setDescription(message).build();
    }

    private List<TextChannel> getApprovalChannels() {
        return e.getJDA().getTextChannelsByName("approvals", true);
    }
}
