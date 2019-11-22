package com.jvmrally.lambda.command.utility;

import static com.jvmrally.lambda.db.tables.Tags.TAGS;
import java.util.List;
import java.util.Optional;
import com.jvmrally.lambda.command.PersistenceAwareCommand;
import com.jvmrally.lambda.command.entites.TagRequest;
import com.jvmrally.lambda.db.tables.pojos.Tags;
import com.jvmrally.lambda.utility.messaging.Messenger;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Tag
 */
public class Tag extends PersistenceAwareCommand {

    private Tag(MessageReceivedEvent e, DSLContext dsl) {
        super(e, dsl);
    }

    @CommandHandler(commandName = "tag", description = "Find a tag by name")
    public static void displayTag(MessageReceivedEvent e, DSLContext dsl, TagRequest req) {
        if (req.getName().isEmpty()) {
            return;
        }
        new Tag(e, dsl).getTagByName(req.getName()).ifPresentOrElse(
                tag -> Messenger.send(e.getChannel(), tag.getContent()),
                () -> Messenger.send(e.getChannel(), "Tag does not exist"));
    }

    @CommandHandler(commandName = "tag.list", description = "List tags")
    public static void displayTagList(MessageReceivedEvent e, DSLContext dsl) {
        new Tag(e, dsl).listTags();
    }

    /**
     * Lists all tags. If no tags are found a default message is sent
     *
     */
    private void listTags() {
        List<String> tags = getAllTags();
        String response = buildListResponse(tags);
        Messenger.send(e.getChannel(), response);
    }

    private String buildListResponse(List<String> tags) {
        return tags.isEmpty() ? "There are no tags." : "**Tags: **" + String.join(", ", tags);
    }

    private List<String> getAllTags() {
        return dsl.select(TAGS.TAGNAME).from(TAGS).fetchInto(String.class);
    }

    /**
     * Finds a tag by name
     * 
     * @param name the name of the tag to find
     * @return
     */
    private Optional<Tags> getTagByName(String name) {
        return dsl.selectFrom(TAGS).where(TAGS.TAGNAME.eq(name)).fetchOptionalInto(Tags.class);
    }
}
