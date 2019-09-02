package com.jvmrally.lambda.utility;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import net.dv8tion.jda.api.entities.Guild;
import net.dv8tion.jda.api.entities.Member;
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
     * Returns a list of channel targetted for a command.
     * 
     * @param e the message event
     * @return a list of TextChannels
     */
    public static Optional<TextChannel> getTargetChannel(MessageReceivedEvent e) {
        List<TextChannel> channels = e.getMessage().getMentionedChannels();
        if (channels.isEmpty() || channels.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(channels.get(0));
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

    /**
     * Checks whether the target user has a specific role
     * 
     * @param member     the user to check
     * @param targetRole the name of the target role
     * @return
     */
    public static boolean hasRole(Member member, String targetRole) {
        return member.getRoles().stream().filter(role -> role.getName().equals(targetRole))
                .count() == 1;
    }

    /**
     * Returns a single member from the mentioned members. If 0 or more than 1 member is mentioned,
     * returns an empty optional
     * 
     * @param e the message event
     * @return the mentioned member
     */
    public static Optional<Member> getMentionedMember(MessageReceivedEvent e) {
        List<Member> members = e.getMessage().getMentionedMembers();
        if (members.size() == 1) {
            return Optional.of(members.get(0));
        } else {
            return Optional.empty();
        }
    }

    /**
     * Returns a list of mentioned members. If no members are mentioned return an empty optional
     * 
     * @param e the message event
     * @return list of members
     */
    public static Optional<List<Member>> getMentionedMembers(MessageReceivedEvent e) {
        List<Member> members = e.getMessage().getMentionedMembers();
        if (members.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(members);
        }
    }
}
