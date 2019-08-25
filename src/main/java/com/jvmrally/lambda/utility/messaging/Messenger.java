package com.jvmrally.lambda.utility.messaging;

import java.util.Optional;
import java.util.function.Function;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Messenger
 */
public class Messenger implements BuildChannel, BuildMessage, BuildMember, CompletedMessenger {

    private MessageChannel channel;
    private Optional<String> message;
    private Optional<MessageEmbed> embed;
    private Member member;

    @Override
    public BuildMessage to(MessageChannel channel) {
        this.channel = channel;
        return this;
    }

    @Override
    public CompletedMessenger message(String message) {
        this.message = Optional.of(message);
        this.embed = Optional.empty();
        return this;
    }

    @Override
    public CompletedMessenger message(EmbedBuilder embed) {
        this.message = Optional.empty();
        this.embed = Optional.of(embed.build());
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
    public static void toChannel(Function<BuildChannel, CompletedMessenger> block) {
        Messenger messenger = new Messenger();
        block.apply(messenger);
        messenger.send();
    }

    /**
     * Send a message to a user via a private chat channel
     * 
     * @param block
     */
    public static void toUser(Function<BuildMember, CompletedMessenger> block) {
        Messenger messenger = new Messenger();
        block.apply(messenger);
        messenger.sendDirectMessage();
    }

    /**
     * Send a direct message
     */
    private void sendDirectMessage() {
        member.getUser().openPrivateChannel().queue(privateChannel -> {
            message.ifPresent(m -> privateChannel.sendMessage(m).queue());
            embed.ifPresent(e -> privateChannel.sendMessage(e).queue());
        });
    }

    /**
     * Send a message to a channel
     */
    private void send() {
        message.ifPresent(m -> channel.sendMessage(m).queue());
        embed.ifPresent(e -> channel.sendMessage(e).queue());
    }
}
