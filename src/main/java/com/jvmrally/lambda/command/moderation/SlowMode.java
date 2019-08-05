package com.jvmrally.lambda.command.moderation;

import java.util.List;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * SlowMode
 */
public class SlowMode {

    @ParsedEntity
    static class SlowModeRequest {
        @Flag(shortName = 't', longName = "time")
        private Integer time = 0;

        @Flag(shortName = 'r', longName = "reset")
        private Boolean reset = false;
    }

    /**
     * Modifies the slowmode of mentioned channels. If no channels are mentioned, it defaults to the
     * channel of the received message.
     * 
     * @param req the request entity containing command flags and values
     * @param e   the message entity received
     */
    @CommandHandler(commandName = "slow")
    public static void slow(SlowModeRequest req, MessageReceivedEvent e) {
        List<TextChannel> channels = e.getMessage().getMentionedChannels();
        if (req.reset) {
            req.time = 0;
        }
        if (channels.isEmpty()) {
            setSlowMode(e.getTextChannel(), req.time);
            return;
        }
        for (TextChannel channel : channels) {
            setSlowMode(channel, req.time);
        }
    }

    /**
     * Set the slowmode of a text channel
     * 
     * @param channel the channel to modify
     * @param time    the number of seconds to set. 0 will remove slow mode
     */
    private static void setSlowMode(TextChannel channel, int time) {
        channel.getManager().setSlowmode(time).queue();
    }
}
