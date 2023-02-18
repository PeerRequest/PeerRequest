package com.peerrequest.app.services.messages;

import lombok.Getter;

/**
 * Message templates for entry messages.
 */
public enum EntryMessageTemplates {
    /**
     * Message template when an entry got deleted.
     * Notifies all reviewers of the entry about the deletion.
     */
    ENTRY_DELETED(
        "PeerRequest - Entry Deleted",
        "Hi %s,\n%s deleted their entry %s. Therefore your review is no longer needed."
    ),

    /**
     * Message template when a researcher of an entry requests a user to review their entry.
     * Notifies requested user about the direct request.
     */
    DIRECT_REQUEST(
        "PeerRequest - Review Request",
        "Hi %s,\n%s has requested a review for their entry %s from you.\nTake a look!"
    ),

    /**
     * Message template when a user claims an open slot of a direct request process.
     * Notifies the researcher about the claim.
     */
    OPEN_SLOT_CLAIMED(
        "PeerRequest - New Reviewer",
        "Hi %s,\n%s claimed an open slot of your entry %s."
    ),

    /**
     * Message template when a requested user accepts a direct request for a review of the entry.
     * Notifies the researcher that the requested user accepted.
     */
    REQUEST_ACCEPTED(
        "PeerRequest - New Reviewer",
        "Hi %s,\n%s accepted your review request for your entry %s."
    ),

    /**
     * Message template when a requested user declines a direct request for a review of the entry.
     * Notifies the researcher that the requested user declined.
     */
    REQUEST_DECLINED(
        "PeerRequest - Request Declined",
        "Hi %s,\n%s declined your review request for your entry %s."
    ),
    /**
     * Message template when an entry got deleted and requests for the entry were ongoing.
     * Notifies a requested user with the request state PENDING about the deletion.
     */
    REQUEST_WITHDRAWN(
        "PeerRequest - Request Withdrawn",
        "Hi %s,\n%s withdrew their request to you for their entry %s."
    );

    public String getMessage(String receiver, String emitter, String entryName) {
        return String.format(message, receiver, emitter, entryName);
    }

    @Getter
    private final String subject;
    @Getter
    private final String message;

    EntryMessageTemplates(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }
}
