package com.peerrequest.app.services;

/**
 * A Factory class for all mayor services.
 */
public interface ServiceFactory {

    /**
     * Creates a new ReviewService instance.
     * @return a new ReviewService instance
     */
    ReviewService newReviewService();

    /**
     * Creates a new EntryService instance.
     * @return a new EntryService instance
     */
    EntryService newEntryService();

    /**
     * Creates a new RequestService instance.
     * @return the new RequestService instance
     */
    RequestService newRequestService();

    /**
     * Creates a new NotificationService instance.
     * @return the new NotificationService instance
     */
    NotificationService newNotificationService();

    /**
     * Creates a new DocumentService instance.
     * @return the new DocumentService instance
     */
    DocumentService newDocumentService();

    /**
     * Creates a new CategoryService instance.
     * @return the new CategoryService instance
     */
    CategoryService newCategoryService();

    /**
     * Creates a new UserService instance.
     * @return the new UserService instance
     */
    UserService newUserService();
}
