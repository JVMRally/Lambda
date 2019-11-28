package com.jvmrally.lambda.command.misc;

import java.awt.Color;
import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * About
 */
public class About extends Command {

    private About(MessageReceivedEvent e) {
        super(e);
    }

    @CommandHandler(commandName = "about", description = "Details information about the bot.")
    public static void execute(MessageReceivedEvent e) {
        new About(e).execute();
    }

    private void execute() {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**Lambda Bot**");
        eb.addField("Source Code", "https://github.com/JVMRally/Lambda", false);
        eb.setColor(Color.WHITE);
        eb.setThumbnail(e.getJDA().getSelfUser().getEffectiveAvatarUrl());
        Messenger.send(e.getChannel(), eb.build());
    }
}
