package com.jvmrally.lambda.command.moderation;

import com.jvmrally.lambda.command.Command;
import disparse.discord.AbstractPermission;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * SlowMode
 */
public class SlowMode extends Command {

    private final SlowModeRequest req;

    private SlowMode(MessageReceivedEvent e, SlowModeRequest req) {
        super(e);
        this.req = req;
    }

    @ParsedEntity
    static class SlowModeRequest {
        @Flag(shortName = 't', longName = "time", description = "The time for the slowmode.")
        private Integer time = 0;

        @Flag(shortName = 'r', longName = "reset",
                description = "Whether or not to reset the slowmode time.")
        private Boolean reset = Boolean.FALSE;
    }

    /**
     * Modifies the slowmode of mentioned channels. If no channels are mentioned, it defaults to the
     * channel of the received message.
     *
     * @param req the request entity containing command flags and values
     * @param e   the message entity received
     */
    @CommandHandler(commandName = "slow",
            description = "Modifies the slowmode of mentioned channels. If no channel is mentioned it defaults to the current channel.",
            perms = AbstractPermission.MANAGE_CHANNEL)
    public static void execute(MessageReceivedEvent e, SlowModeRequest req) {
        new SlowMode(e, req).configureSlowMode();
    }

    private void configureSlowMode() {
        int time = Boolean.TRUE.equals(req.reset) ? 0 : req.time;
        setSlowMode(time);
    }

    /**
     * Set the slowmode of targeted text channel
     *
     * @param time the number of seconds to set. 0 will remove slow mode
     */
    private void setSlowMode(int time) {
        for (TextChannel channel : getTargetChannels()) {
            channel.getManager().setSlowmode(time).queue();
        }
    }
}
