package com.peerrequest.app.services.messages;

import lombok.Getter;

/**
 * Message templates for category messages.
 */
public enum CategoryMessageTemplates {
    CATEGORY_DELETED(
            "PeerRequest - Category deleted",
            "Category was deleted, therefore your review is no longer needed.");

    public String getMessage(String receiver, String entryName) {
        return String.format(message, receiver, entryName);
    }

    @Getter
    private final String subject;
    @Getter
    private final String message;

    CategoryMessageTemplates(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }
}



