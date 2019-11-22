package com.jvmrally.lambda.command.utility;

import static com.jvmrally.lambda.db.tables.Tags.TAGS;
import java.time.OffsetDateTime;
import java.util.Optional;
import com.jvmrally.lambda.command.AuditedPersistenceAwareCommand;
import com.jvmrally.lambda.command.entites.TagRequest;
import com.jvmrally.lambda.db.enums.AuditAction;
import com.jvmrally.lambda.db.tables.pojos.Tags;
import com.jvmrally.lambda.utility.messaging.Messenger;
import org.jooq.DSLContext;
import disparse.parser.reflection.CommandHandler;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;

/**
 * TagAdmin
 */
public class TagAdmin extends AuditedPersistenceAwareCommand {

    private TagRequest req;

    public TagAdmin(MessageReceivedEvent e, DSLContext dsl, TagRequest req) {
        super(e, dsl);
        this.req = req;
    }

    @CommandHandler(commandName = "tag.admin.create",
            description = "Create a tag. Place all message content between two `\"`",
            roles = "admin")
    public static void executeCreateTag(MessageReceivedEvent e, DSLContext dsl, TagRequest req) {
        new TagAdmin(e, dsl, req).createTag();
    }

    @CommandHandler(commandName = "tag.admin.edit",
            description = "Edit a tag. Place all message content between two `\"`", roles = "admin")
    public static void executeEditTag(MessageReceivedEvent e, DSLContext dsl, TagRequest req) {
        new TagAdmin(e, dsl, req).editTag();
    }

    @CommandHandler(commandName = "tag.admin.delete", description = "Delete a tag", roles = "admin")
    public static void executeDelete(MessageReceivedEvent e, DSLContext dsl, TagRequest req) {
        new TagAdmin(e, dsl, req).deleteTag();
    }

    private void createTag() {
        if (!isContentEmpty()) {
            findTagByName(req.getName()).ifPresentOrElse(this::sendTagAlreadyExistsError,
                    this::saveTag);
        } else {
            sendMissingContentError();
        }
    }

    private void sendTagAlreadyExistsError(Tags tag) {
        Messenger.send(e.getChannel(), "Tag `" + tag.getTagname() + "` already exists.");
    }

    private void editTag() {
        if (!isContentEmpty()) {
            findTagByName(req.getName()).ifPresentOrElse(tag -> updateTag(),
                    this::sendTagDoesNotExistError);
        } else {
            sendMissingContentError();
        }
    }

    private void sendTagDoesNotExistError() {
        Messenger.send(e.getChannel(), "Tag does not exist.");
    }


    private void deleteTag() {
        findTagByName(req.getName()).ifPresentOrElse(tag -> removeTag(tag),
                this::sendTagDoesNotExistError);
    }

    private Optional<Tags> findTagByName(String name) {
        return dsl.selectFrom(TAGS).where(TAGS.TAGNAME.eq(name)).fetchOptionalInto(Tags.class);
    }

    private void saveTag() {
        dsl.insertInto(TAGS).columns(TAGS.TAGNAME, TAGS.CONTENT, TAGS.UPDATED_AT)
                .values(req.getName(), req.getContent(), OffsetDateTime.now()).execute();
        auditTag(AuditAction.CREATED_TAG);
        Messenger.send(e.getChannel(), "Tag created!");
    }

    private void updateTag() {
        dsl.update(TAGS).set(TAGS.CONTENT, req.getContent())
                .set(TAGS.UPDATED_AT, OffsetDateTime.now()).where(TAGS.TAGNAME.eq(req.getName()))
                .execute();
        auditTag(AuditAction.EDITED_TAG);
    }


    private void removeTag(Tags tag) {
        dsl.deleteFrom(TAGS).where(TAGS.TAGNAME.eq(tag.getTagname())).execute();
        auditTag(AuditAction.DELETED_TAG);
        Messenger.send(e.getChannel(), "Tag `" + tag.getTagname() + "` has been deleted.");
    }

    private void auditTag(AuditAction action) {
        audit.log(action, e.getAuthor().getIdLong());
    }

    /**
     * Checks whether the content supplied is empty
     * 
     * @return true if the content is empty
     */
    private boolean isContentEmpty() {
        if (req.getContent().isEmpty()) {
            return true;
        }
        return false;
    }

    private void sendMissingContentError() {
        Messenger.send(e.getChannel(), "You must supply content.");
    }

}
