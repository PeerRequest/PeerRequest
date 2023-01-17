package com.peerrequest.app.services;

import com.peerrequest.app.model.Review;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.UUID;

public interface ReviewService<F, U> extends CrudService<Review, Review.ReviewSelector, F, U> {
    void addMessage(int reviewID, UUID userID, DateTimeFormat timeStamp, String content);

    void deleteMessage(UUID commentID);

    void notifyResearcher(UUID reviewID);

    void notifyReviewerOfEdit(UUID reviewID);
}