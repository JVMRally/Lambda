package com.jvmrally.lambda.command.misc;

import java.awt.Color;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * About
 */
public class About {

    @CommandHandler(commandName = "about", description = "Details information about the bot.")
    public static void execute(MessageReceivedEvent e) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**Lambda Bot**");
        eb.addField("Source Code", "https://www.github.com/TobyLarone85/Lambda", false);
        eb.setColor(Color.WHITE);
        eb.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        Messenger.toChannel(messenger -> messenger.to(e.getChannel()).message(eb));
    }
}
