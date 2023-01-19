package com.peerrequest.app.services;

import com.peerrequest.app.model.Review;
import com.peerrequest.app.model.User;
import com.peerrequest.app.model.Messsage;

import java.time.ZonedDateTime;

public interface ReviewService<F, U> extends CrudService<Review, Review.ReviewSelector, F, U> {
    void addMessage(Review.ReviewSelector reviewID, User.UserSelector userID, ZonedDateTime timeStamp, String content);

    void deleteMessage(Message.MessageSelector commentID);

    void notifyResearcherOfEdit(Review.ReviewSelector reviewID);

    void notifyReviewerOfEdit(Review.ReviewSelector reviewID);
}