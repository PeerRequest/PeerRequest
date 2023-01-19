package com.peerrequest.app.model;


import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.HashMap;

/**
 * This abstract class represents a generic notification.
 *
 * @author Ludger Halpick
 * @author Michael Mildt
 * @version 0.0.1
 */
public abstract class Notification {

    /**
     * Constructor of a notification
     *
     * @param id          id of the notification
     * @param receiverID  id of the user who receives the notification
     * @param emitterID   id of the user who emitted the notification
     * @param subject     subject of the notification
     * @param message     message of the notification
     * @param timeStamp   timestamp of the creation of the notification
     */
    public Notification(NotificationSelector id, User.UserSelector receiverID,
                        User.UserSelector emitterID, String subject, String message, ZonedDateTime timeStamp) {
        this.id = id;
        this.receiverID = receiverID;
        this.emitterID = emitterID;
        this.subject = subject;
        this.message = message;
        this.timeStamp = timeStamp;
    }
    @Getter
    private final NotificationSelector id;

    @Getter
    private final User.UserSelector receiverID;

    @Getter
    private final User.UserSelector emitterID;

    @Getter
    private final String subject;

    /**
     * Complete notification message with every wildcard filled.
     */
    @Getter
    private final String message;

    @Getter
    private final ZonedDateTime timeStamp;

    public record NotificationSelector(long notificationID) {
    }

    public record NotificationUpdater() { }

    /**
     * This class represents a review notification.
     *
     * @author Ludger Halpick
     * @author Michael Mildt
     * @version 0.0.1
     */
    public static class ReviewNotification extends Notification {

        /**
         * Constructor of a review notification.
         *
         * @param reviewID id of the review which issued the notification
         */
        public ReviewNotification(NotificationSelector id, User.UserSelector receiverID,
                                  User.UserSelector emitterID, String subject, String message,
                                  ZonedDateTime timeStamp, Review.ReviewSelector reviewID) {
            super(id, receiverID, emitterID, subject, message, timeStamp);
            this.reviewID = reviewID;
        }
        @Getter
        private final Review.ReviewSelector reviewID;

        /**
         * The Review referenced by `reviewID`.
         * Might be null.
         */
        @Getter
        @Setter
        private Review review;


        /**
         * Message templates for a review message.
         */
        public enum ReviewMessage {
            // TODO: add messages
            /**
             * Message template when a researcher presses the 'notify reviewer' button.
             * Informs the reviewer about changes of the answers-from-the-authors-box of a review.
             */
            EDIT_RESEARCHER(""),
            /**
             * Message template when a reviewer presses the 'notify researcher' button.
             * Informs the researcher about changes of the comment boxes except
             * answers-from-the-authors-box of a review.
             */
            EDIT_REVIEWER(""),

            /**
             * Message template when a message is posted to a message board of a review.
             * Informs the other user about the post.
             */
            ADD_MESSAGE("");

            @Getter
            private final String message;

            ReviewMessage(String message) {
                this.message = message;
            }
        }
    }

    /**
     * This class represents an entry notification.
     *
     * @author Ludger Halpick
     * @author Michael Mildt
     * @version 0.0.1
     */
    public static class EntryNotification extends Notification {

        /**
         * Constructor of a review notification.
         *
         * @param entryID id of the entry which issued the notification
         */
        public EntryNotification(NotificationSelector id, User.UserSelector receiverID,
                                 User.UserSelector emitterID, String subject, String message,
                                 ZonedDateTime timeStamp, Entry.EntrySelector entryID) {
            super(id, receiverID, emitterID, subject, message, timeStamp);
            this.entryID = entryID;
        }
        @Getter
        private final Entry.EntrySelector entryID;

        /**
         * The Entry referenced by `entryID`.
         * Might be null.
         */
        @Getter
        @Setter
        private Entry entry;

        /**
         * Message templates for a review message.
         */
        public enum EntryMessage {
            // TODO: add messages
            /**
             * Message template when an entry got deleted.
             * Notifies all reviewers of the entry about the deletion.
             */
            ENTRY_DELETED(""),
            /**
             * Message template when a researcher of an entry requests a user to review their entry.
             * Notifies requested user about the direct request.
             */
            DIRECT_REQUEST(""),
            /**
             * Message template when a user claims an open slot of a direct request process.
             * Notifies the researcher about the claim.
             */
            OPEN_SLOT_CLAIMED(""),
            /**
             * Message template when a requested user accepts a direct request for a review of the entry.
             * Notifies the researcher that the requested user accepted.
             */
            REQUEST_ACCEPTED(""),
            /**
             * Message template when a requested user declines a direct request for a review of the entry.
             * Notifies the researcher that the requested user declined.
             */
            REQUEST_DECLINED(""),
            /**
             * Message template when an entry got deleted and requests for the entry were ongoing.
             * Notifies a requested user with the request state PENDING about the deletion.
             */
            REQUEST_WITHDRAWN("");

            @Getter
            private final String message;

            EntryMessage(String message) {
                this.message = message;
            }
        }
    }

    /**
     * This class represents an bidding process notification.
     *
     * @author Ludger Halpick
     * @author Michael Mildt
     * @version 0.0.1
     */
    public static class BiddingProcessNotification extends Notification {

        /**
         * Constructor of a bidding process notification except for the message BIDDING_PROCESS_ALLOCATION.
         *
         * @param categoryID  id of the category of the bidding process which issued the notification
         */
        public BiddingProcessNotification(NotificationSelector id, User.UserSelector receiverID,
                                          User.UserSelector emitterID, String subject, String message,
                                          ZonedDateTime timeStamp, Category.CategorySelector categoryID) {
            super(id, receiverID, emitterID, subject, message, timeStamp);
            this.categoryID = categoryID;
        }

        /**
         * Constructor of the BIDDING_PROCESS_ALLOCATION bidding process notification.
         *
         * @param categoryID  id of the category of the bidding process which issued the notification
         * @param entries     id
         */
        public BiddingProcessNotification(NotificationSelector id, User.UserSelector receiverID,
                                          User.UserSelector emitterID, String subject, String message,
                                          ZonedDateTime timeStamp, Category.CategorySelector categoryID,
                                          HashMap<Entry.EntrySelector, Optional<Entry>> entries) {
            super(id, receiverID, emitterID, subject, message, timeStamp);
            this.categoryID = categoryID;
            this.entries = entries;
        }
        @Getter
        private HashMap<Entry.EntrySelector, Optional<Entry>> entries;

        @Getter
        private final Category.CategorySelector categoryID;

        /**
         * The Category referenced by `categoryID`.
         * Might be null.
         */
        @Getter
        @Setter
        private Category.CategorySelector category;

        public enum BiddingProcessMessage {
            // TODO: add messages
            /**
             * Message template when a researcher of an external category
             * requests a user to take part of their bidding process.
             * Notifies the requested user about the bidding request.
             */
            BIDDING_PROCESS_REQUEST(""),
            /**
             * Message template when a bidding request for a bidding process was declined.
             * Notifies the researcher of the bidding process that the requested user declined.
             */
            BIDDING_PROCESS_DECLINED(""),
            /**
             * Message template when a bidding process was refreshed.
             * Notifies a participating user about the refresh and that they have to bid again.
             */
            BIDDING_PROCESS_RESTARTED(""),
            /**
             * Message template when a bidding process is finished.
             * Informs a participating user about the entries they have to review.
             */
            BIDDING_PROCESS_ALLOCATION("");

            @Getter
            private final String message;

            BiddingProcessMessage(String message) {
                this.message = message;
            }
        }
    }
}
