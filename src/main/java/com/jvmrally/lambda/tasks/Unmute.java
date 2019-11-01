package com.jvmrally.lambda.tasks;

import static com.jvmrally.lambda.db.tables.Mute.MUTE;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.jvmrally.lambda.annotation.Task;
import com.jvmrally.lambda.db.tables.pojos.Mute;
import com.jvmrally.lambda.injectable.JooqConn;
import com.jvmrally.lambda.utility.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import net.dv8tion.jda.api.JDA;

/**
 * UnmuteTask
 */
@Task(unit = TimeUnit.MINUTES, frequency = 5)
public class Unmute implements Runnable {

    private static final Logger logger = LogManager.getLogger(Unmute.class);

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
        List<Mute> mutes = dsl.selectFrom(MUTE)
                .where(MUTE.MUTE_EXPIRY.le(System.currentTimeMillis())).fetchInto(Mute.class);
        var guild = jda.getGuilds().get(0);
        for (Mute mute : mutes) {
            Util.getRole(guild, "muted").ifPresentOrElse(role -> {
                guild.removeRoleFromMember(guild.getMemberById(mute.getUserid()), role).queue();
                dsl.deleteFrom(MUTE).where(MUTE.USERID.eq(mute.getUserid())).execute();
            }, () -> logger.warn("Role was not found"));
        }
    }
}
