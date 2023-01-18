package com.peerrequest.app.model;

import lombok.Getter;

import java.time.ZonedDateTime;

/**
 * This class represents a message.
 *
 * @author User5 Mildt
 * @version 0.0.1
 */
public class Message {

    /**
     * Constructor of a message.
     *
     * @param id         id of the message
     * @param reviewID   id of the review of this message
     * @param creatorID  id of the user who created this message
     * @param timeStamp  timestamp of the creation of this message
     * @param content    content of this message
     */
    public Message(MessageSelector id, Review.ReviewSelector reviewID,
                   User.UserSelector creatorID, ZonedDateTime timeStamp, String content) {
        this.id = id;
        this.reviewID = reviewID;
        this.creatorID = creatorID;
        this.timeStamp = timeStamp;
        this.content = content;
    }

    @Getter
    private final MessageSelector id;

    @Getter
    private final Review.ReviewSelector reviewID;

    @Getter
    private final User.UserSelector creatorID;

    @Getter
    private final ZonedDateTime timeStamp;

    @Getter
    private final String content;

    public record MessageSelector(long messageID) {}
}
