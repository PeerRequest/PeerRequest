package com.peerrequest.app.model;


import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.Optional;
import lombok.Getter;
import lombok.Setter;

/**
 * This abstract class represents a generic notification.
 *
 * @author User1 Halpick
 * @author User5 Mildt
 * @version 0.0.1
 */
public abstract class Notification {

    @Getter
    private final NotificationSelector id;
    @Getter
    private final User.UserSelector receiverSelector;
    @Getter
    private final User.UserSelector emitterSelector;
    @Getter
    private final String subject;
    /**
     * Complete notification message with every wildcard filled.
     */
    @Getter
    private final String message;
    @Getter
    private final ZonedDateTime timeStamp;

    /**
     * Constructor of a notification.
     *
     * @param id               id of the notification
     * @param receiverSelector id of the user who receives the notification
     * @param emitterSelector  id of the user who emitted the notification
     * @param subject          subject of the notification
     * @param message          message of the notification
     * @param timeStamp        timestamp of the creation of the notification
     */
    public Notification(NotificationSelector id, User.UserSelector receiverSelector,
                        User.UserSelector emitterSelector, String subject, String message, ZonedDateTime timeStamp) {
        this.id = id;
        this.receiverSelector = receiverSelector;
        this.emitterSelector = emitterSelector;
        this.subject = subject;
        this.message = message;
        this.timeStamp = timeStamp;
    }

    /**
     * Identifies a Notification.
     *
     * @param id id of the Notification
     */
    public record NotificationSelector(long id) {
    }

    /**
     * This class represents a review notification.
     *
     * @author User1 Halpick
     * @author User5 Mildt
     * @version 0.0.1
     */
    public static class ReviewNotification extends Notification {

        @Getter
        private final Review.ReviewSelector reviewSelector;
        /**
         * The Review referenced by `reviewSelector`.
         * Might be null.
         */
        @Getter
        @Setter
        private Review review;

        /**
         * Constructor of a review notification.
         *
         * @param reviewSelector id of the review which issued the notification
         */
        public ReviewNotification(NotificationSelector id, User.UserSelector receiverSelector,
                                  User.UserSelector emitterSelector, String subject, String message,
                                  ZonedDateTime timeStamp, Review.ReviewSelector reviewSelector) {
            super(id, receiverSelector, emitterSelector, subject, message, timeStamp);
            this.reviewSelector = reviewSelector;
        }


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
     * @author User1 Halpick
     * @author User5 Mildt
     * @version 0.0.1
     */
    public static class EntryNotification extends Notification {

        @Getter
        private final Entry.EntrySelector entrySelector;
        /**
         * The Entry referenced by `id`.
         * Might be null.
         */
        @Getter
        @Setter
        private Entry entry;

        /**
         * Constructor of a review notification.
         *
         * @param entrySelector id of the entry which issued the notification
         */
        public EntryNotification(NotificationSelector id, User.UserSelector receiverSelector,
                                 User.UserSelector emitterSelector, String subject, String message,
                                 ZonedDateTime timeStamp, Entry.EntrySelector entrySelector) {
            super(id, receiverSelector, emitterSelector, subject, message, timeStamp);
            this.entrySelector = entrySelector;
        }

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
     * This class represents a bidding process notification.
     *
     * @author User1 Halpick
     * @author User5 Mildt
     * @version 0.0.1
     */
    public static class BiddingProcessNotification extends Notification {

        @Getter
        private final Category.CategorySelector categorySelector;
        @Getter
        private HashMap<Entry.EntrySelector, Optional<Entry>> entries;
        /**
         * The Category referenced by `id`.
         * Might be null.
         */
        @Getter
        @Setter
        private Category.CategorySelector category;

        /**
         * Constructor of a bidding process notification except for the message BIDDING_PROCESS_ALLOCATION.
         *
         * @param categorySelector id of the category of the bidding process which issued the notification
         */
        public BiddingProcessNotification(NotificationSelector id, User.UserSelector receiverSelector,
                                          User.UserSelector emitterSelector, String subject, String message,
                                          ZonedDateTime timeStamp, Category.CategorySelector categorySelector) {
            super(id, receiverSelector, emitterSelector, subject, message, timeStamp);
            this.categorySelector = categorySelector;
        }

        /**
         * Constructor of the BIDDING_PROCESS_ALLOCATION bidding process notification.
         *
         * @param categorySelector id of the category of the bidding process which issued the notification
         * @param entries          id
         */
        public BiddingProcessNotification(NotificationSelector id, User.UserSelector receiverSelector,
                                          User.UserSelector emitterSelector, String subject, String message,
                                          ZonedDateTime timeStamp, Category.CategorySelector categorySelector,
                                          HashMap<Entry.EntrySelector, Optional<Entry>> entries) {
            super(id, receiverSelector, emitterSelector, subject, message, timeStamp);
            this.categorySelector = categorySelector;
            this.entries = entries;
        }


        /**
         * Message templates for a bidding process message.
         */
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
