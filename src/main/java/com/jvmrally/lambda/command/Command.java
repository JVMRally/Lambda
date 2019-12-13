package com.jvmrally.lambda.command;

import java.util.List;
import java.util.Optional;
import java.util.StringJoiner;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Command
 */
public class Command {

    protected final MessageReceivedEvent e;

    public Command(MessageReceivedEvent e) {
        this.e = e;
    }

    /**
     * Takes the list of arguments passed to a command method and rebuilds them into a single
     * string.
     * 
     * @param args the list of arguments to rebuild
     * @return a string
     */
    protected static String rebuildArgsToString(List<String> args) {
        StringJoiner sj = new StringJoiner(" ");
        for (String s : args) {
            sj.add(s);
        }
        return sj.toString();
    }

    /**
     * Returns a list of channels targetted for a command. If no channels are mentioned in the
     * command message, it returns the channel the received message was sent to.
     * 
     * @param e the message event
     * @return a list of TextChannels
     */
    protected List<TextChannel> getTargetChannels() {
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
    protected Optional<TextChannel> getTargetChannel() {
        List<TextChannel> channels = e.getMessage().getMentionedChannels();
        if (channels.isEmpty() || channels.size() != 1) {
            return Optional.empty();
        }
        return Optional.of(channels.get(0));
    }

    /**
     * Returns a single member from the mentioned members. If 0 or more than 1 member is mentioned,
     * returns an empty optional
     * 
     * @param e the message event
     * @return the mentioned member
     */
    protected Optional<Member> getMentionedMember() {
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
    protected Optional<List<Member>> getMentionedMembers() {
        List<Member> members = e.getMessage().getMentionedMembers();
        if (members.isEmpty()) {
            return Optional.empty();
        } else {
            return Optional.of(members);
        }
    }

    /**
     * Delete original message containing command
     */
    protected void selfDestruct() {
        e.getMessage().delete().queue();
    }


}
