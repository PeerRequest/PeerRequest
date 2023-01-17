package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

public abstract class Notification {
    @Getter
    @Setter
    NotificationSelector id;

    @Getter
    @Setter
    String subject;

    @Getter
    @Setter
    String message;

    @Getter
    @Setter
    List<User.UserSelector> receiver;

    @Getter
    @Setter
    User.UserSelector emitter;

    @Getter
    @Setter
    ZonedDateTime timeStamp;

    public record NotificationSelector(long id) {
    }

    public static class ReviewNotification extends Notification {
        @Getter
        @Setter
        Review.ReviewSelector reviewSelector;

        /**
         * The Review referenced by `reviewSelector`.
         * Might be null.
         */
        @Getter
        @Setter
        Review review;

        public enum ReviewMessage {
            // TODO: add messages
            EDIT_RESEARCHER(""), EDIT_REVIEWER(""), ADD_MESSAGE("");

            @Getter
            final String message;

            ReviewMessage(String message) {
                this.message = message;
            }
        }
    }

    public static class EntryNotification extends Notification {
        @Getter
        @Setter
        Entry.EntrySelector entrySelector;

        /**
         * The Entry referenced by `entrySelector`.
         * Might be null.
         */
        @Getter
        @Setter
        Entry entry;

        @Getter
        @Setter
        Review review;

        public enum EntryMessage {
            // TODO: add messages
            ENTRY_DELETED(""), DIRECT_REQUEST(""), OPEN_SLOT_CLAIMED(""), REQUEST_ACCEPTED(""), REQUEST_DECLINED(""), REQUEST_WITHDRAWN("");

            @Getter
            final String message;

            EntryMessage(String message) {
                this.message = message;
            }
        }
    }

    public static class BiddingProcessNotification extends Notification {
        @Getter
        @Setter
        HashMap<Entry.EntrySelector, Optional<Entry>> entries;

        @Getter
        @Setter
        Category.CategorySelector categorySelector;

        /**
         * The Category referenced by `categorySelector`.
         * Might be null.
         */
        @Getter
        @Setter
        Category.CategorySelector category;

        @Getter
        @Setter
        Review review;

        public enum BiddingProcessMessage {
            // TODO: add messages
            ENTRY_DELETED(""), DIRECT_REQUEST(""), OPEN_SLOT_CLAIMED(""), REQUEST_ACCEPTED(""), REQUEST_DECLINED(""), REQUEST_WITHDRAWN("");

            @Getter
            final String message;

            BiddingProcessMessage(String message) {
                this.message = message;
            }
        }
    }
}
