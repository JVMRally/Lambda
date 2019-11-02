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
 * JepScraper is task which is used to scrap JEP pages (https://openjdk.java.net/jeps/0) 
 * for updates, add them (if they exist) to the database and post an update via Messenger.
 *   
 */
@Task(unit = TimeUnit.HOURS, frequency = 3)
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
        try {
            logger.info("Running OpenJDK Scraper");
            List<Jep> jeps = getSiteJeps();
            List<Jep> dbJeps = getDbJeps();
            if (dbJeps.isEmpty()) {
                insertJeps(jeps);
            } else {
                updateAndPostJeps(jeps, dbJeps);
            }
        } catch (Exception e) {
            logger.error("Error scraping jeps", e);
        }
    }

    /**
     * Searches through list of existing jeps list for news and changes from scraped jeps list.
     * Updates all existing Jeps if there are changes and adds all non existing jeps to list.
     * 
     * @param scrapedJeps
     * @param existingJeps
     */
    private void updateAndPostJeps(List<Jep> scrapedJeps, List<Jep> existingJeps) {

        List<Jep> newJeps = new ArrayList<>();
        for (Jep jep : scrapedJeps) {
            boolean jepFound = false;
            for (Jep existingJep : existingJeps) {
                if (jep.getId() == existingJep.getId()) {
                    existingJeps.remove(existingJep);
                    if (!jep.equals(existingJep)) {
                        updateJep(existingJep, jep);
                    }
                    jepFound = true;
                    break;
                }
            }
            if (!jepFound) {
                newJeps.add(jep);
            }
        }
        insertJeps(newJeps);
    }

    /**
     * Builds new JepEmbed message for updated JEP and sends it via Messenger to appropriate channel.
     * 
     * @param existingJep
     * @param jep
     */
    private void sendUpdatedEmbed(Jep existingJep, Jep jep) {
        EmbedBuilder eb = new EmbedBuilder();
        buildJepEmbed(eb, jep, "UPDATED");
        eb.addBlankField(false);
        eb.addField("Changes", getChanges(existingJep, jep), false);
        sendEmbed(eb);
    }

    /**
     * Builds new JepEmbed message for new JEP and sends it via Messenger to appropriate channel
     * 
     * @param jep
     */
    private void sendNewEmbed(Jep jep) {
        EmbedBuilder eb = new EmbedBuilder();
        buildJepEmbed(eb, jep, "NEW");
        sendEmbed(eb);
    }

    /**
     * Used to send embed message via Messenger to "java_updates" channel
     *  
     * @param eb builder of the message
     */
    private void sendEmbed(EmbedBuilder eb) {
        // TODO at least pull out constant for channel name
        TextChannel channel = jda.getTextChannelsByName("java_updates", true).get(0); 
        Messenger.send(channel, eb.build());
    }

    private void buildJepEmbed(EmbedBuilder eb, Jep jep, String type) {
        eb.setTitle("**" + type + " " + jep.getId() + ": " + jep.getTitle() + "**");
        eb.addField("Type", jep.getJepType().name(), true);
        eb.addField("Status", jep.getStatus().name(), true);
        eb.addBlankField(true);
        eb.addField("Java Release", jep.getRelease(), true);
        eb.addField("JDK Component", jep.getComponent(), true);
        eb.addBlankField(true);
        eb.addField("URL", jep.getUrl(), false);
        eb.setColor(EMBED_COLOR);
    }


    /**
     * 
     * Builds string of changes between 2 Jep objects - new one and old one.
     * 
     * @param existingJep old one that has changes
     * @param jep         one that carries wind of change
     * @return string of changes
     */
    private String getChanges(Jep existingJep, Jep jep) {
        StringBuilder changes = new StringBuilder();
        if (existingJep.getJepType() != jep.getJepType()) {
            changes.append(existingJep.getJepType().name()).append(" -> ")
                    .append(jep.getJepType().name()).append('\n');
        }
        if (existingJep.getStatus() != jep.getStatus()) {
            changes.append(existingJep.getStatus().name()).append(" -> ")
                    .append(jep.getStatus().name()).append('\n');
        }
        if (!existingJep.getRelease().equals(jep.getRelease())) {
            changes.append(existingJep.getRelease()).append(" -> ").append(jep.getRelease())
                    .append('\n');
        }
        if (!existingJep.getComponent().equals(jep.getComponent())) {
            changes.append(existingJep.getComponent()).append(" -> ").append(jep.getComponent())
                    .append('\n');
        }
        return changes.toString();
    }

    /**
     *  Currently does 2 jobs: 1. Inserting list of Jeps into the database
     *                         2. Sending every inserted Jep to the appropriate channel via Messenger
     * @param jeps
     */
    private void insertJeps(List<Jep> jeps) {
        DSLContext dsl = JooqConn.getJooqContext();
        List<JepsRecord> jepRecords = jeps.stream().map(Jep::toRecord).collect(Collectors.toList());
        dsl.batchInsert(jepRecords).execute();

        jeps.forEach(this::sendNewEmbed);
    }

    /**
     * Currently does 2 jobs: 1. Updating JEP entity in the database
     *                        2. Sending update message to the appropriate channel via Messenger 
     * 
     * @param existingJep Jep to be updated
     * @param jep         One that carries the update
     */
    private void updateJep(Jep existingJep, Jep jep) {
        DSLContext dsl = JooqConn.getJooqContext();
        dsl.update(JEPS).set(JEPS.JEP_STATUS, jep.getStatus().name())
                .set(JEPS.JEP_TYPE, jep.getJepType().name()).set(JEPS.COMPONENT, jep.getComponent())
                .set(JEPS.RELEASE, jep.getRelease()).set(JEPS.TITLE, jep.getTitle())
                .where(JEPS.ID.eq(jep.getId())).execute();
        sendUpdatedEmbed(existingJep, jep);
    }

    /**
     * Uses Jsoup to connect to JEP_URL in order to scrape html into com.jvmrally.lambda.jdk.Jep objects.
     * 
     * @return List of scrapped jeps
     * @throws IOException caught by run() method if fails
     */
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

    /**
     * Queries the db for all Jeps contained and returns them in list of com.jvmrally.lambda.jdk.Jep objects. 
     *  
     * @return list of JEPs.
     */
    private List<Jep> getDbJeps() {
        DSLContext dsl = JooqConn.getJooqContext();
        List<Jeps> dbJeps = dsl.selectFrom(JEPS).orderBy(JEPS.ID).fetchInto(Jeps.class);
        return dbJeps.stream().map(Jep::new).collect(Collectors.toList());
    }

}
