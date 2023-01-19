package com.peerrequest.app.services;

import com.peerrequest.app.model.*;

import java.util.List;

/**
 * Interface class responsible for CRUD operations regarding Document objects as well as sending those notifications.
 * @param <F> EntityFilter
 * @param <U> EntityUpdater
 */
public interface NotificationService<F, U> extends CrudService<Notification, Notification.NotificationSelector,
        F, U> {

    /**
     * Sending a notification to a user in context of an ongoing bidding process.
     * @param sendingUserID id of the user that started the bidding
     * @param receivingUserID id of the user to be notified
     * @param categoryID id of the category of the bidding
     * @param message id of the actual notification message
     */
    void sendBiddingNotification(User.UserSelector sendingUserID, User.UserSelector receivingUserID,
                                 Category.CategorySelector categoryID,
                                 Notification.BiddingProcessNotification.BiddingProcessMessage message);

    /**
     * Sending a notification to a user containing their results of a bidding process.
     * @param sendingUserID id of the user that started the bidding
     * @param receivingUserID id of the user to be notified
     * @param categoryID id of the category of the bidding
     * @param entries the entries that have been allocated to the receiving user
     */
    void sendBiddingAllocationNotification(User.UserSelector sendingUserID, User.UserSelector receivingUserID,
                                           Category.CategorySelector categoryID, List<Entry> entries);

    /**
     * Sending a notification to a user in context of an entry.
     * @param sendingUserID id of the user that started the bidding
     * @param receivingUserID id of the user to be notified
     * @param entryID id the entry in context
     * @param message the actual notification message
     */
    void sendEntryNotification(User.UserSelector sendingUserID, User.UserSelector receivingUserID,
                               Entry.EntrySelector entryID, Notification.EntryNotification.EntryMessage message);

    /**
     * Sending a notification to a user in context of a review.
     * @param sendingUserID id of the user that started the bidding
     * @param receivingUserID id of the user to be notified
     * @param reviewID id the review in context
     * @param message the actual notification message
     */
    void sendReviewNotification(User.UserSelector sendingUserID, User.UserSelector receivingUserID,
                                Review.ReviewSelector reviewID, Notification.ReviewNotification.ReviewMessage message);

    /**
     * Re-sending a notification in case the mail-server could not be reached.
     * @param notificationID the id of the notification that has to be resent
     */
    void sendNotification(Notification.NotificationSelector notificationID);
}
