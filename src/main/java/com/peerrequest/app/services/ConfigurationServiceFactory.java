package com.peerrequest.app.services;

import com.peerrequest.app.model.*;

import java.time.ZonedDateTime;
import java.util.List;

public class ConfigurationServiceFactory implements ServiceFactory{
    @Override
    public ReviewService newReviewService() {
        return new ReviewService() {
            @Override
            public void addMessage(long reviewID, long userID, ZonedDateTime timeStamp, String content) {

            }

            @Override
            public void deleteMessage(long commentID) {

            }

            @Override
            public void notifyResearcher(long reviewID) {

            }

            @Override
            public void notifyReviewerOfEdit(long reviewID) {

            }

            @Override
            public Object create(Object newEntity) {
                return null;
            }

            @Override
            public List list(Object cursor, int maxCount, Object filter) {
                return null;
            }

            @Override
            public Object get(Object cursor) {
                return null;
            }

            @Override
            public Object update(Object cursor, Object updater) {
                return null;
            }

            @Override
            public Object delete(Object cursor) {
                return null;
            }
        };
    }

    @Override
    public EntryService newEntryService() {
        return new EntryService() {
            @Override
            public Object create(Object newEntity) {
                return null;
            }

            @Override
            public List list(Object cursor, int maxCount, Object filter) {
                return null;
            }

            @Override
            public Object get(Object cursor) {
                return null;
            }

            @Override
            public Object update(Object cursor, Object updater) {
                return null;
            }

            @Override
            public Object delete(Object cursor) {
                return null;
            }
        };
    }

    @Override
    public RequestService newRequestService() {
        return new RequestService() {
            @Override
            public void createDirectRequestProcess(long entryID, List userIDs, int openSlots) {

            }

            @Override
            public void setOpenSlots(int openSlots) {

            }

            @Override
            public void claimOpenSlot(long directRequestProcessID, long userID) {

            }

            @Override
            public void acceptDirectRequest(long requestID) {

            }

            @Override
            public void declineDirectRequest(long requestID) {

            }

            @Override
            public void declineBiddingRequest(long requestID) {

            }

            @Override
            public DirectRequestProcess getDirectRequestProcess(long entryID) {
                return null;
            }

            @Override
            public Object create(Object newEntity) {
                return null;
            }

            @Override
            public List list(Object cursor, int maxCount, Object filter) {
                return null;
            }

            @Override
            public Object get(Object cursor) {
                return null;
            }

            @Override
            public Object update(Object cursor, Object updater) {
                return null;
            }

            @Override
            public Object delete(Object cursor) {
                return null;
            }
        };
    }

    @Override
    public NotificationService newNotificationService() {
        return new NotificationService() {
            @Override
            public void sendBiddingNotification(User.UserSelector sendingUserID, User.UserSelector receivingUserID, Category.CategorySelector categoryID, Notification.BiddingProcessNotification.BiddingProcessMessage message) {

            }

            @Override
            public void sendBiddingAllocationNotification(User.UserSelector sendingUserID, User.UserSelector receivingUserID, Category.CategorySelector categoryID, List entries) {

            }

            @Override
            public void sendEntryNotification(User.UserSelector sendingUserID, User.UserSelector receivingUserID, Entry.EntrySelector entryID, Notification.EntryNotification.EntryMessage message) {

            }

            @Override
            public void sendReviewNotification(User.UserSelector sendingUserID, User.UserSelector receivingUserID, Review.ReviewSelector reviewID, Notification.ReviewNotification.ReviewMessage message) {

            }

            @Override
            public void sendNotification(Notification.NotificationSelector notificationID) {

            }

            @Override
            public Object create(Object newEntity) {
                return null;
            }

            @Override
            public List list(Object cursor, int maxCount, Object filter) {
                return null;
            }

            @Override
            public Object get(Object cursor) {
                return null;
            }

            @Override
            public Object update(Object cursor, Object updater) {
                return null;
            }

            @Override
            public Object delete(Object cursor) {
                return null;
            }
        };
    }

    @Override
    public DocumentService newDocumentService() {
        return new DocumentService() {
            @Override
            public Object create(Object newEntity) {
                return null;
            }

            @Override
            public List list(Object cursor, int maxCount, Object filter) {
                return null;
            }

            @Override
            public Object get(Object cursor) {
                return null;
            }

            @Override
            public Object update(Object cursor, Object updater) {
                return null;
            }

            @Override
            public Object delete(Object cursor) {
                return null;
            }
        };
    }

    @Override
    public CategoryService newCategoryService() {
        return new CategoryService() {
            @Override
            public Object create(Object newEntity) {
                return null;
            }

            @Override
            public List list(Object cursor, int maxCount, Object filter) {
                return null;
            }

            @Override
            public Object get(Object cursor) {
                return null;
            }

            @Override
            public Object update(Object cursor, Object updater) {
                return null;
            }

            @Override
            public Object delete(Object cursor) {
                return null;
            }
        };
    }

    @Override
    public UserService newUserService() {
        return new UserService() {
            @Override
            public User getUser(User.UserSelector selector) {
                return null;
            }
        };
    }
}
