package com.jvmrally.lambda.command.misc;

import java.util.stream.Collectors;
import com.jvmrally.lambda.command.Command;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Whois
 */
public class Whois extends Command {

    private Whois(MessageReceivedEvent e) {
        super(e);
    }

    @CommandHandler(commandName = "whois", description = "Details information about a user")
    public static void execute(MessageReceivedEvent e) {
        new Whois(e).execute();
    }

    private void execute() {
        getMentionedMember().ifPresentOrElse(this::sendWhoisEmbed, this::sendMissingUserError);
    }

    private void sendWhoisEmbed(Member member) {
        Messenger.send(e.getChannel(), prepareWhoisEmbed(member).build());
    }

    private void sendMissingUserError() {
        Messenger.send(e.getChannel(), "Must provide a user");
    }

    private EmbedBuilder prepareWhoisEmbed(Member member) {
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**" + member.getUser().getName() + "**");
        eb.setColor(member.getColor());
        eb.setThumbnail(member.getUser().getEffectiveAvatarUrl());
        eb.addField("Status", member.getAsMention() + " - " + member.getOnlineStatus().name(),
                false);
        eb.addField("Join date", member.getTimeJoined().toString(), false);
        eb.addField("Registered Date", member.getTimeCreated().toString(), false);
        eb.addField("Roles", member.getRoles().stream().map(Role::getAsMention)
                .collect(Collectors.joining(", ")), false);
        eb.setFooter(member.getId());
        return eb;
    }
}
