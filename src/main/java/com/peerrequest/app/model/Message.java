package com.peerrequest.app.model;

import java.time.ZonedDateTime;
import lombok.Getter;

/**
 * This class represents a message.
 */
public record Message(@Getter com.peerrequest.app.model.Message.MessageSelector id,
                      @Getter Review.ReviewSelector reviewSelector, @Getter User.UserSelector creatorSelector,
                      @Getter ZonedDateTime timeStamp, @Getter String content) {
    /**
     * Constructor of a message.
     *
     * @param id              id of the message
     * @param reviewSelector  id of the review of this message
     * @param creatorSelector id of the user who created this message
     * @param timeStamp       timestamp of the creation of this message
     * @param content         content of this message
     */
    public Message {
    }

    /**
     * Identifies a Message.
     *
     * @param id id of a message
     */
    public record MessageSelector(long id) {
    }
}
