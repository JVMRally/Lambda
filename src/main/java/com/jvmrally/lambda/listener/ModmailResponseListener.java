package com.jvmrally.lambda.listener;

import com.jvmrally.lambda.modmail.ModmailCommunicationHandler;

import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;

/**
 * ModmailResponseListener
 */
public class ModmailResponseListener extends ListenerAdapter {

    @Override
    public void onMessageReceived(MessageReceivedEvent event) {
        new ModmailCommunicationHandler().handleModResponse(event);
    }

}