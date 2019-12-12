package com.jvmrally.lambda.command.utility;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Consumer;
import java.util.stream.Collectors;
import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.command.entites.GiveawayResultRequest;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.MessageReaction;
import net.dv8tion.jda.api.entities.TextChannel;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * GiveawayResult
 */
public class GiveawayResult extends Command {

    private GiveawayResultRequest req;

    public GiveawayResult(MessageReceivedEvent e, GiveawayResultRequest req) {
        super(e);
        this.req = req;
    }

    @CommandHandler(commandName = "giveaway.result", description = "Draw a result from a giveaway.",
            roles = "admin")
    public static void execute(MessageReceivedEvent e, GiveawayResultRequest req) {
        new GiveawayResult(e, req).execute();
    }

    public void execute() {
        if (req.getNum() == 0) {
            return;
        }
        var channels = e.getGuild().getTextChannelsByName("giveaways", true);
        if (!channels.isEmpty()) {
            var channel = channels.get(0);
            Message m = channel.retrieveMessageById(req.getMessageId()).complete();
            var winners = getWinner(m.getReactions());
            announceWinners(winners, channel);
            selfDestruct();
        }
    }

    private void selfDestruct() {
        e.getMessage().delete().queue();
    }

    private void announceWinners(List<User> users, TextChannel channel) {
        StringBuilder sb = new StringBuilder();
        sb.append("Congratulations to");
        for (User user : users) {
            sb.append(" ").append(user.getAsMention());
        }
        sb.append(". Contact <@227810440560902144> to receive your prize!");
        Messenger.send(channel, sb.toString());
    }

    private List<User> getWinner(List<MessageReaction> reactions) {
        for (var reaction : reactions) {
            if (reaction.getReactionEmote().getEmoji().equals("🎉")) {
                return selectRandomWinner(reaction);
            }
        }
        return Collections.emptyList();
    }

    private List<User> selectRandomWinner(MessageReaction reaction) {
        var users = getValidUsers(reaction);
        List<User> winners = new ArrayList<>();
        int limit = calculateNumberOfWinners(users);
        for (int i = 0; i < limit; i++) {
            User winner = null;
            do {
                winner = users.get(ThreadLocalRandom.current().nextInt(users.size()));
            } while (winners.contains(winner));
            winners.add(winner);
        }
        return winners;
    }

    private List<User> getValidUsers(MessageReaction reaction) {
        var users = reaction.retrieveUsers().complete();
        users = users.stream().filter(user -> !user.isBot()).collect(Collectors.toList());
        return users;
    }

    private int calculateNumberOfWinners(List<User> users) {
        return users.size() <= req.getNum() ? 1 : req.getNum();
    }

}
