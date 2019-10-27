package com.jvmrally.lambda.command.utility;

import static com.jvmrally.lambda.db.tables.Tags.TAGS;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import com.jvmrally.lambda.command.entites.TagRequest;
import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.db.tables.pojos.Tags;
import com.jvmrally.lambda.injectable.Auditor;
import com.jvmrally.lambda.utility.Util;
import com.jvmrally.lambda.utility.messaging.Messenger;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * Tag
 */
public class Tag {

    @CommandHandler(commandName = "tag",
            description = "Create, edit, or display useful messages. For creating and editing, place all message content between two `\"`")
    public static void execute(Auditor auditor, DSLContext dsl, TagRequest req,
            MessageReceivedEvent e) {
        if (req.isManipulatingTag()) {
            if (!Util.hasRole(e.getMember(), "admin")) {
                Messenger.send(e.getChannel(), "You don't have permission to do that");
                return;
            }
            manipulateTag(auditor, dsl, req, e);
            return;
        }
        if (req.getList()) {
            listTags(dsl, e);
            return;
        }
        if (!req.getName().isEmpty()) {
            dsl.selectFrom(TAGS).where(TAGS.TAGNAME.eq(req.getName())).fetchOptionalInto(Tags.class)
                    .ifPresentOrElse(tag -> Messenger.send(e.getChannel(), tag.getContent()),
                            () -> Messenger.send(e.getChannel(), "Tag does not exist"));
        }
    }

    private static void manipulateTag(Auditor auditor, DSLContext dsl, TagRequest req,
            MessageReceivedEvent e) {
        if (req.getCreate()) {
            createTag(auditor, dsl, req, e);
            return;
        }
        if (req.getEdit()) {
            editTag(auditor, dsl, req, e);
            return;
        }
        if (req.getDelete()) {
            deleteTag(auditor, dsl, req, e);
            return;
        }
    }

    private static void createTag(Auditor auditor, DSLContext dsl, TagRequest req,
            MessageReceivedEvent e) {
        if (isContentEmpty(req, e)) {
            return;
        }
        findTag(dsl, req.getName()).ifPresentOrElse(tag -> Messenger.send(e.getChannel(),
                "Tag `" + tag.getTagname() + "` already exists."), () -> {
                    dsl.insertInto(TAGS).columns(TAGS.TAGNAME, TAGS.CONTENT, TAGS.UPDATED_AT)
                            .values(req.getName(), req.getContent(), OffsetDateTime.now())
                            .execute();
                    auditor.log(AuditAction.CREATED_TAG, e.getAuthor().getIdLong());
                });
        Messenger.send(e.getChannel(), "Tag created!");
    }

    private static void editTag(Auditor auditor, DSLContext dsl, TagRequest req,
            MessageReceivedEvent e) {
        if (isContentEmpty(req, e)) {
            return;
        }
        findTag(dsl, req.getName()).ifPresentOrElse(tag -> {
            dsl.update(TAGS).set(TAGS.CONTENT, req.getContent()).set(TAGS.UPDATED_AT,
                    OffsetDateTime.now());
            auditor.log(AuditAction.EDITED_TAG, e.getAuthor().getIdLong());
        }, () -> Messenger.send(e.getChannel(), "Tag does not exist."));
    }

    private static void deleteTag(Auditor auditor, DSLContext dsl, TagRequest req,
            MessageReceivedEvent e) {
        findTag(dsl, req.getName()).ifPresentOrElse(tag -> {
            dsl.deleteFrom(TAGS).where(TAGS.TAGNAME.eq(tag.getTagname())).execute();
            auditor.log(AuditAction.DELETED_TAG, e.getAuthor().getIdLong());
            Messenger.send(e.getChannel(), "Tag `" + tag.getTagname() + "` has been deleted.");
        }, () -> Messenger.send(e.getChannel(), "Tag does not exist."));
    }

    /**
     * Lists all tags. If no tags are found a default message is sent
     * 
     * @param dsl
     * @param e
     */
    private static void listTags(DSLContext dsl, MessageReceivedEvent e) {
        List<String> tags = dsl.select(TAGS.TAGNAME).from(TAGS).fetchInto(String.class);
        String response;
        if (tags.isEmpty()) {
            response = "There are no tags yet";
        } else {
            response = "**Tags: **" + String.join(", ", tags);
        }
        Messenger.send(e.getChannel(), response);
    }

    /**
     * Finds a tag by name
     * 
     * @param dsl  dslcontext
     * @param name the name of the tag to find
     * @return
     */
    private static Optional<Tags> findTag(DSLContext dsl, String name) {
        return dsl.selectFrom(TAGS).where(TAGS.TAGNAME.eq(name)).fetchOptionalInto(Tags.class);
    }

    /**
     * Checks whether the content supplied is empty
     * 
     * @param req the request flags
     * @param e   the message event
     * @return true if the content is empty
     */
    private static boolean isContentEmpty(TagRequest req, MessageReceivedEvent e) {
        if (req.getContent().isEmpty()) {
            Messenger.send(e.getChannel(), "You must supply content.");
            return true;
        }
        return false;
    }

}
