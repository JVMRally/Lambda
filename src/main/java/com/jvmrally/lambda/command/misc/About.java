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

    private About() {
    }

    @CommandHandler(commandName = "about", description = "Details information about the bot.")
    public static void execute(MessageReceivedEvent e) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**Lambda Bot**");
        eb.addField("Source Code", "https://github.com/JVMRally/Lambda", false);
        eb.setColor(Color.WHITE);
        eb.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        Messenger.send(e.getChannel(), eb.build());
    }
}
