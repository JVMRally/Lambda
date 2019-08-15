package com.jvmrally.lambda;

import java.util.List;
import java.util.StringJoiner;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Util
 */
public class Util {

    /**
     * Returns a list of channels targetted for a command. If no channels are mentioned in the
     * command message, it returns the channel the received message was sent to.
     * 
     * @param e the message event
     * @return a list of TextChannels
     */
    public static List<TextChannel> getTargetChannels(MessageReceivedEvent e) {
        List<TextChannel> channels = e.getMessage().getMentionedChannels();
        if (channels.isEmpty()) {
            channels = List.of(e.getTextChannel());
        }
        return channels;
    }

    /**
     * Takes the list of arguments passed to a command method and rebuilds them into a single
     * string.
     * 
     * @param args the list of arguments to rebuild
     * @return a string
     */
    public static String rebuildArgsToString(List<String> args) {
        StringJoiner sj = new StringJoiner(" ");
        for (String s : args) {
            sj.add(s);
        }
        return sj.toString();
    }
}
