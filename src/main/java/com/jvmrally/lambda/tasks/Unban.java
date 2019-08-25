package com.jvmrally.lambda.tasks;

import static com.jvmrally.lambda.db.Tables.BAN;
import java.util.List;
import java.util.concurrent.TimeUnit;
import com.jvmrally.lambda.annotation.Task;
import com.jvmrally.lambda.db.tables.pojos.Ban;
import com.jvmrally.lambda.injectable.JooqConn;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import net.dv8tion.jda.api.JDA;

/**
 * Unban
 */
@Task(unit = TimeUnit.HOURS, frequency = 1)
public class Unban implements Runnable {

    private static final Logger logger = LogManager.getLogger(Unban.class);
    private JDA jda;

    public Unban(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        DSLContext dsl = JooqConn.getJooqContext();
        List<Ban> bans = dsl.selectFrom(BAN).where(BAN.BAN_EXPIRY.le(System.currentTimeMillis()))
                .fetchInto(Ban.class);
        var guild = jda.getGuilds().get(0);
        for (Ban ban : bans) {
            guild.unban(ban.getUserid().toString()).queue();
            logger.info("Unbanning userid: {}", ban.getUserid());
            dsl.deleteFrom(BAN).where(BAN.USERID.eq(ban.getUserid())).execute();
        }
    }
}
