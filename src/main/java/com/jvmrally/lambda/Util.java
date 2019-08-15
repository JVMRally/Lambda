package com.jvmrally.lambda;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Role;
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

    /**
     * Method to find a role in a guild. If role is not found, returns Optional.empty
     * 
     * @param guild the guild to search
     * @param name  the role to find
     * @return the found role, or Optional.empty
     */
    public static Optional<Role> getRole(Guild guild, String name) {
        List<Role> roles = guild.getRolesByName(name, true);
        if (roles.size() == 1) {
            return Optional.of(roles.get(0));
        } else {
            return Optional.empty();
        }
    }
}
