package com.peerrequest.app.services.messages;

import lombok.Getter;

/**
 * Message templates for a review messages.
 */
public enum ReviewMessageTemplates {
    /**
     * Message template when a researcher presses the 'notify reviewer' button.
     * Informs the reviewer about changes of the answers-from-the-authors-box of a review.
     */
    EDIT_RESEARCHER(
        "PeerRequest - Researcher Update",
        "Hi %s,\n%s updated the the \"answers from the authors\"-box of the review of the entry %s."
            + "\nTake a look!"
    ),
    /**
     * Message template when a reviewer presses the 'notify researcher' button.
     * Informs the researcher about changes of the comment boxes except
     * answers-from-the-authors-box of a review.
     */
    EDIT_REVIEWER(
        "PeerRequest - Reviewer Update",
        "Hi %s,\n%s updated their review of the entry %s.\nTake a look!"
    ),

    /**
     * Message template when a message is posted to a message board of a review.
     * Informs the other user about the post.
     */
    ADD_MESSAGE(
        "PeerRequest - New Message",
        "Hi %s,\n%s posted a comment on the message board of the review of the entry %s.\nTake a look!"
    );

    public String getMessage(String receiver, String emitter, String entryName) {
        return String.format(receiver, message, emitter, entryName);
    }

    @Getter
    private final String subject;

    private final String message;

    ReviewMessageTemplates(String subject, String message) {
        this.subject = subject;
        this.message = message;
    }
}
