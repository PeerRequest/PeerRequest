package com.peerrequest.app.services;

import com.peerrequest.app.model.Review;
import java.time.ZonedDateTime;

public interface ReviewService<F, U> extends CrudService<Review, Review.ReviewSelector, F, U> {
    void addMessage(long reviewID, long userID, ZonedDateTime timeStamp, String content);

    void deleteMessage(long commentID);

    void notifyResearcher(long reviewID);

    void notifyReviewerOfEdit(long reviewID);
}