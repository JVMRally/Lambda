package com.jvmrally.lambda.command.misc;

import java.awt.Color;
import java.util.stream.Collectors;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Whois
 */
public class Whois {

    @CommandHandler(commandName = "whois", description = "Details information about a user")
    public static void execute(MessageReceivedEvent e) {
        Util.getMentionedMember(e).ifPresentOrElse(
                member -> Messenger
                        .toChannel(m -> m.to(e.getChannel()).message(prepareWhoisEmbed(member))),
                () -> Messenger
                        .toChannel(m -> m.to(e.getChannel()).message("Must provide a user")));
    }

    private static EmbedBuilder prepareWhoisEmbed(Member member) {
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
