package com.jvmrally.lambda.tasks;

import static com.jvmrally.lambda.db.Tables.JEPS;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import com.jvmrally.lambda.annotation.Task;
import com.jvmrally.lambda.db.tables.pojos.Jeps;
import com.jvmrally.lambda.db.tables.records.JepsRecord;
import com.jvmrally.lambda.injectable.JooqConn;
import com.jvmrally.lambda.jdk.Jep;
import com.jvmrally.lambda.utility.messaging.Messenger;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jooq.DSLContext;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import java.awt.Color;

/**
 * JepScraper
 */
@Task(unit = TimeUnit.HOURS, frequency = 12)
public class JepScraper implements Runnable {

    private static final Logger logger = LogManager.getLogger(JepScraper.class);
    private static final Color EMBED_COLOR = new Color(0xe66d1d);
    private static final String JEP_BASE = "https://openjdk.java.net/jeps/";
    private static final String JEP_URL = JEP_BASE + "0";
    private static final String USER_AGENT = "JVMRally Discord - OpenJDK Updates Bot";

    private JDA jda;

    public JepScraper(JDA jda) {
        this.jda = jda;
    }

    @Override
    public void run() {
        logger.info("Running OpenJDK Scraper");
        List<Jep> jeps = new ArrayList<>();
        try {
            jeps = getSiteJeps();
        } catch (IOException e) {
            logger.error("Error scraping jdk website.", e);
        }

        List<Jep> dbJeps = getDbJeps();
        if (dbJeps.isEmpty()) {
            insertJeps(jeps);
        } else {
            updateAndPostJeps(jeps, dbJeps);
        }
    }

    private void updateAndPostJeps(List<Jep> scrapedJeps, List<Jep> existingJeps) {
        for (Jep jep : scrapedJeps) {
            boolean jepFound = false;
            for (Jep existingJep : existingJeps) {
                if (jep.getId() == existingJep.getId()) {
                    existingJeps.remove(existingJep);
                    if (!jep.equals(existingJep)) {
                        updateJep(jep);
                    }
                    jepFound = true;
                    break;
                }
            }
            if (!jepFound) {
                insertJep(jep);
            }
        }
    }

    private void sendEmbed(Jep jep, String type) {
        TextChannel channel = jda.getTextChannelsByName("java_updates", true).get(0);
        EmbedBuilder eb = new EmbedBuilder();
        eb.setTitle("**" + type + " " + jep.getId() + ": " + jep.getTitle() + "**");
        eb.addField("Type", jep.getJepType().name(), true);
        eb.addField("Status", jep.getStatus().name(), true);
        eb.addBlankField(true);
        eb.addField("Java Release", jep.getRelease(), true);
        eb.addField("JDK Component", jep.getComponent(), true);
        eb.addBlankField(true);
        eb.addField("URL", jep.getUrl(), false);
        eb.setColor(EMBED_COLOR);
        Messenger.toChannel(m -> m.to(channel).message(eb));
    }

    private void insertJep(Jep jep) {
        insertJeps(List.of(jep));
    }

    private void insertJeps(List<Jep> jeps) {
        DSLContext dsl = JooqConn.getJooqContext();
        List<JepsRecord> jepRecords =
                jeps.stream().map(Jep::toRecord).collect(Collectors.toList());
        dsl.batchInsert(jepRecords).execute();
        for (Jep jep : jeps) {
            sendEmbed(jep, "NEW");
        }
    }

    private void updateJep(Jep jep) {
        DSLContext dsl = JooqConn.getJooqContext();
        dsl.update(JEPS).set(JEPS.JEP_STATUS, jep.getStatus().name())
                .set(JEPS.JEP_TYPE, jep.getJepType().name()).set(JEPS.COMPONENT, jep.getComponent())
                .set(JEPS.RELEASE, jep.getRelease()).set(JEPS.TITLE, jep.getTitle())
                .where(JEPS.ID.eq(jep.getId())).execute();
        sendEmbed(jep, "UPDATED");
    }

    private List<Jep> getSiteJeps() throws IOException {
        Document doc = Jsoup.connect(JEP_URL).userAgent(USER_AGENT).get();
        Elements jepTables = doc.getElementsByClass("jeps");
        List<Jep> jeps = new ArrayList<>();
        for (Element table : jepTables) {
            Elements rows = table.select("tr");
            for (Element row : rows) {
                Elements columns = row.select("td");
                jeps.add(Jep.buildJepFromHtml(columns));

            }
        }
        return jeps;
    }

    private List<Jep> getDbJeps() {
        DSLContext dsl = JooqConn.getJooqContext();
        List<Jeps> dbJeps = dsl.selectFrom(JEPS).fetchInto(Jeps.class);
        return dbJeps.stream().map(jep -> new Jep(jep)).collect(Collectors.toList());
    }

}
