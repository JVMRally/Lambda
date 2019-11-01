package com.jvmrally.lambda.utility.messaging;

import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageChannel;
import net.dv8tion.jda.api.entities.MessageEmbed;

/**
 * Messenger
 */
public class Messenger {

    private Messenger() {
    }

    /**
     * Send a text message to a discord channel
     * 
     * @param channel the target discord channel
     * @param message the message to send
     */
    public static void send(MessageChannel channel, String message) {
        channel.sendMessage(message).queue();
    }

    /**
     * Send an embed message to a discord channel
     * 
     * @param channel the target discord channel
     * @param message the embed message to send
     */
    public static void send(MessageChannel channel, MessageEmbed message) {
        channel.sendMessage(message).queue();
    }

    /**
     * Send a text message to a user
     * 
     * @param member  the target user
     * @param message the text message
     */
    public static void send(Member member, String message) {
        member.getUser().openPrivateChannel()
                .queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }

    /**
     * Send an embed message to a user
     * 
     * @param member  the target user
     * @param message the embed message
     */
    public static void send(Member member, MessageEmbed message) {
        member.getUser().openPrivateChannel()
                .queue(privateChannel -> privateChannel.sendMessage(message).queue());
    }

    /**
     * Send a text message to a channel and return the created message
     * 
     * @param channel the target channel
     * @param message the text message
     * @return the created message
     */
    public static Message sendReturning(MessageChannel channel, String message) {
        return channel.sendMessage(message).complete();
    }

    /**
     * Send an embed message to a channel and return the created message
     * 
     * @param channel the target channel
     * @param message the embed message
     * @return the created message
     */
    public static Message sendReturning(MessageChannel channel, MessageEmbed message) {
        return channel.sendMessage(message).complete();
    }
}
