package com.jvmrally.lambda.utility.messaging;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Messenger
 */
public class Messenger {

    public static void send(MessageChannel channel, String message) {
        channel.sendMessage(message).queue();
    }

    public static void send(MessageChannel channel, MessageEmbed message) {
        channel.sendMessage(message).queue();
    }

    public static void send(Member member, String message) {
        member.getUser().openPrivateChannel()
                .queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }

    public static void send(Member member, MessageEmbed message) {
        member.getUser().openPrivateChannel()
                .queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }

    public static Message sendReturning(MessageChannel channel, String message) {
        return channel.sendMessage(message).complete();
    }

    public static Message sendReturning(MessageChannel channel, MessageEmbed message) {
        return channel.sendMessage(message).complete();
    }
}
