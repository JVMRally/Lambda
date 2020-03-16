package com.jvmrally.lambda.command.utility;

import java.io.IOException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.utility.messaging.EmbedMessage;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import disparse.parser.reflection.Flag;
import disparse.parser.reflection.ParsedEntity;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Embed
 */
public class Embed extends Command {
    private static final Logger logger = LogManager.getLogger(Embed.class);
    private EmbedRequest req;

    private Embed(MessageReceivedEvent e, EmbedRequest req) {
        super(e);
        this.req = req;
    }

    @ParsedEntity
    static class EmbedRequest {
        @Flag(shortName = 'j', longName = "json", description = "Json input to create embed.")
        private String json;
    }

    @CommandHandler(commandName = "embed", description = "Create and post an embed from json input",
            roles = "admin")
    public static void execute(MessageReceivedEvent e, EmbedRequest req) {
        new Embed(e, req).execute();
    }

    private void execute() {
        ObjectMapper om = new ObjectMapper();
        EmbedMessage embed;
        try {
            embed = om.readValue(req.json, EmbedMessage.class);
        } catch (IOException e) {
            logger.error(e);
            return;
        }
        getTargetChannel().ifPresentOrElse(channel -> Messenger.send(channel, embed.build()),
                this::sendMissingChannelError);
    }

    private void sendMissingChannelError() {
        Messenger.send(e.getChannel(), "Must provide a target channel");
    }
}
