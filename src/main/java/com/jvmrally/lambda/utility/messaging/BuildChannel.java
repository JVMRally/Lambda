package com.jvmrally.lambda.utility.messaging;

import net.dv8tion.jda.api.entities.MessageChannel;

/**
 * BuildChannel
 */
public interface BuildChannel {

    BuildMessage to(MessageChannel channel);
}
