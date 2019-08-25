package com.jvmrally.lambda.utility.messaging;

import net.dv8tion.jda.api.EmbedBuilder;

/**
 * BuildMesage
 */
public interface BuildMessage {

    CompletedMessenger message(String message);

    CompletedMessenger message(EmbedBuilder embed);
}
