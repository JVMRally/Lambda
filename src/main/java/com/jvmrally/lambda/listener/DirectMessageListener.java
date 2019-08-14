package com.jvmrally.lambda.listener;

import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * DirectMessageListener
 */
public class DirectMessageListener extends ListenerAdapter {

    private static final Logger logger = LogManager.getLogger(DirectMessageListener.class);

    @Override
    public void onPrivateMessageReceived(PrivateMessageReceivedEvent e) {
        String author = e.getAuthor().getName();
        String message = e.getMessage().getContentRaw();
        logger.info("Message received from {}: {}", author, message);
    }
}
