package com.jvmrally.lambda;

import javax.security.auth.login.LoginException;
import disparse.discord.Dispatcher;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;

/**
 * App Entry
 *
 */
public class App {
    private static final String TOKEN = "LAMBDA_TOKEN";
    private static final String PREFIX = "!";

    public static void main(String[] args) throws LoginException, InterruptedException {
        JDA jda = Dispatcher.init(new JDABuilder(System.getenv(TOKEN)), PREFIX).build();
        jda.awaitReady();
    }
}
