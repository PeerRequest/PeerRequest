package com.peerrequest.app.services;

import com.peerrequest.app.data.Message;
import com.peerrequest.app.data.Review;
import java.util.List;
import java.util.Optional;

/**
 * Describes the functionality of a ReviewService.
 */
public interface ReviewService
    extends CrudService<Review, Long, Review.Dto> {

    /**
     * Adds a Message represented by a DTO to the message board of a Review.
     *
     * @param newEntity DTO of a Message
     */
    Message createMessage(Message.Dto newEntity);

    /**
     * Returns a list of max maxCount messages after a cursor filtered by an given filter.
     *
     * @param cursor   cursor, may be null
     * @param maxCount maxCount
     * @param filter   filter, may be null
     * @return the filtered list
     */
    List<Message> listMessages(Long cursor, int maxCount, Message.Dto filter);

    /**
     * Returns a message.
     *
     * @param cursor id of the message
     * @return message
     */
    Optional<Message> getMessage(Long cursor);

    /**
     * Updates a Message.
     *
     * @param cursor   id of the message
     * @param newProps update
     * @return updated message or empty Optional
     */
    Optional<Message> updateMessage(Long cursor, Message.Dto newProps);

    /**
     * Deletes a message with the given id.
     *
     * @param cursor id of a Message
     * @return deleted message
     */
    Optional<Message> deleteMessage(Long cursor);

    /**
     * Notifies a researcher about an edit of the review form of a review from a reviewer.
     *
     * @param cursor id of the Review
     */
    void notifyResearcherOfEdit(Long cursor);

    /**
     * Notifies a reviewer about an edit of the review form of a review from a researcher.
     *
     * @param cursor id of the Review
     */
    void notifyReviewerOfEdit(Long cursor);
}
