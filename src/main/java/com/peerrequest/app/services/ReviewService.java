package com.peerrequest.app.services;

import com.peerrequest.app.model.Message;
import com.peerrequest.app.model.Review;
import com.peerrequest.app.model.User;
import java.time.ZonedDateTime;


/**
 * Describes the functionality of a  ReviewService.
 *
 * @param <F> Review Filter
 * @param <U> Review Updater
 */
public interface ReviewService<F, U> extends CrudService<Review, Review.ReviewSelector, F, U> {
    void addMessage(Review.ReviewSelector reviewSelector, User.UserSelector userSelector, ZonedDateTime timeStamp,
                    String content);

    void deleteMessage(Message.MessageSelector messageSelector);

    void notifyResearcherOfEdit(Review.ReviewSelector reviewSelector);

    void notifyReviewerOfEdit(Review.ReviewSelector reviewSelector);
}