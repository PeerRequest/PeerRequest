package com.peerrequest.app.services;

import com.peerrequest.app.model.Review;
import org.springframework.format.annotation.DateTimeFormat;

public interface ReviewService<F, U> extends CrudService<Review, Review.ReviewSelector, F, U> {
    void addMessage(long reviewID, long userID, DateTimeFormat timeStamp, String content);

    void deleteMessage(long commentID);

    void notifyResearcher(long reviewID);

    void notifyReviewerOfEdit(long reviewID);
}