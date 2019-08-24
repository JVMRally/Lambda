package com.jvmrally.lambda.utility.messaging;

import java.util.function.Function;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * Messenger
 */
public class Messenger implements BuildChannel, BuildMessage, BuildMember {

    private MessageChannel channel;
    private String message;
    private Member member;

    @Override
    public BuildMessage to(MessageChannel channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public Messenger message(String message) {
        this.message = message;
        return this;
    }

    @Override
    public BuildMessage to(Member member) {
        this.member = member;
        return this;
    }

    /**
     * Send a message to a channel
     * 
     * @param block
     */
    public static void toChannel(Function<BuildChannel, Messenger> block) {
        Messenger messenger = new Messenger();
        block.apply(messenger);
        messenger.send();
    }

    /**
     * Send a message to a user via a private chat channel
     * 
     * @param block
     */
    public static void toUser(Function<BuildMember, Messenger> block) {
        Messenger messenger = new Messenger();
        block.apply(messenger);
        messenger.sendDirectMessage();
    }

    /**
     * Send a direct message
     */
    private void sendDirectMessage() {
        member.getUser().openPrivateChannel()
                .queue(channel -> channel.sendMessage(message).queue());
    }

    /**
     * Send a message to a channel
     */
    private void send() {
        channel.sendMessage(message).queue();
    }
}
