package com.jvmrally.lambda.command.moderation;

import static com.jvmrally.lambda.db.tables.Mute.MUTE;
import java.util.List;
import com.jvmrally.lambda.command.PersistenceAwareCommand;
import com.jvmrally.lambda.command.exception.RoleNotFoundException;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import disparse.discord.AbstractPermission;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.Role;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * UnMute
 */
public class UnMute extends PersistenceAwareCommand {

    private static final String TARGET_ROLE = "muted";
    private final Role role;

    private UnMute(MessageReceivedEvent e, DSLContext dsl) throws RoleNotFoundException {
        super(e, dsl);
        role = Util.getRole(e.getGuild(), TARGET_ROLE)
                .orElseThrow(() -> new RoleNotFoundException(TARGET_ROLE));
    }

    @CommandHandler(commandName = "unmute", description = "Unmute mentioned users.",
            perms = AbstractPermission.ADMINISTRATOR)
    public static void execute(MessageReceivedEvent e, DSLContext dsl)
            throws RoleNotFoundException {
        var unmute = new UnMute(e, dsl);
        unmute.unmute();
    }

    private void unmute() {
        getMentionedMembers().ifPresentOrElse(this::unmuteMembers, this::userError);
    }

    private void unmuteMembers(List<Member> members) {
        members.forEach(this::unmuteMember);
    }

    private void unmuteMember(Member member) {
        e.getGuild().removeRoleFromMember(member, role).queue();
        dsl.deleteFrom(MUTE).where(MUTE.USERID.eq(member.getIdLong())).execute();
    }

    private void userError() {
        Messenger.send(e.getChannel(), "Must mention at least one user");
    }
}
