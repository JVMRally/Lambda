package com.jvmrally.lambda.command.utility;

import java.awt.Color;
import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.command.entites.GiveawayRequest;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Giveaway
 */
public class Giveaway extends Command {

    private GiveawayRequest req;

    public Giveaway(MessageReceivedEvent e, GiveawayRequest req) {
        super(e);
        this.req = req;
    }

    @CommandHandler(commandName = "giveaway", description = "Create a giveaway.", roles = "admin")
    public static void execute(MessageReceivedEvent e, GiveawayRequest req) {
        new Giveaway(e, req).execute();
    }

    private void execute() {
        var channels = e.getGuild().getTextChannelsByName("giveaways", true);
        if (!channels.isEmpty()) {
            var channel = channels.get(0);
            var message = Messenger.sendReturning(channel, getGiveawayMessage());
            message.addReaction("🎉").queue();
        }
    }

    private Message getGiveawayMessage() {
        return new MessageBuilder().setEmbed(buildEmbed()).append("@everyone").build();
    }

    private MessageEmbed buildEmbed() {
        return new EmbedBuilder().setTitle(req.getTitle()).setDescription(req.getDescription())
                .setFooter("React to this message with 🎉 for a chance to win!")
                .setColor(Color.CYAN).build();
    }


}
