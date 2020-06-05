package com.jvmrally.lambda.tasks;

import static com.jvmrally.lambda.db.tables.Mute.MUTE;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import javax.management.relation.RoleNotFoundException;
import com.jvmrally.lambda.annotation.Task;
import com.jvmrally.lambda.db.tables.pojos.Mute;
import com.jvmrally.lambda.injectable.JooqConn;
import com.jvmrally.lambda.utility.Util;
import net.dv8tion.jda.api.entities.Guild;
import org.jooq.DSLContext;
import net.dv8tion.jda.api.JDA;

/**
 * UnmuteTask
 */
@Task(unit = TimeUnit.MINUTES, frequency = 5)
public class Unmute implements Runnable {

    private static final String MUTED_ROLE = "muted";
    private JDA jda;

    public Unmute(JDA jda) {
        this.jda = jda;
    }

    /**
     * Periodically unmute any user where their mute time has expired
     */
    @Override
    public void run() {
        DSLContext dsl = JooqConn.getJooqContext();
        Map<Long, Mute> mutes = dsl.selectFrom(MUTE)
                .where(MUTE.MUTE_EXPIRY.le(System.currentTimeMillis())).fetchInto(Mute.class)
                .stream()
                .collect(Collectors.toMap(Mute::getGuildId, m -> m));

        mutes.forEach((guildId, mute) -> {
            Guild guild = jda.getGuildById(guildId);
            if (guild == null) {
                return;
            }
            Util.getRole(guild, MUTED_ROLE).ifPresentOrElse(role -> {
                guild.removeRoleFromMember(guild.getMemberById(mute.getUserid()), role).queue();
                dsl.deleteFrom(MUTE)
                        .where(MUTE.USERID.eq(mute.getUserid()))
                        .and(MUTE.GUILD_ID.eq(guildId)).execute();
            }, () -> new RoleNotFoundException(MUTED_ROLE));
        });
    }
}
