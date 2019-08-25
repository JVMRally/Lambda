package com.jvmrally.lambda.utility.messaging;

import net.dv8tion.jda.api.EmbedBuilder;

/**
 * BuildMesage
 */
public interface BuildMessage {

    Messenger message(String message);

    Messenger message(EmbedBuilder embed);
}
