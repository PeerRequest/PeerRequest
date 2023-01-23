package com.peerrequest.app.api;

import com.peerrequest.app.model.Document;
import com.peerrequest.app.model.Entry;
import com.peerrequest.app.model.Review;
import com.peerrequest.app.model.User;
import java.util.List;
import org.springframework.web.bind.annotation.*;


/**
 * Defines the controller for Reviews.
 */
public class ReviewsController {

    private Review reviewMockup;

    @GetMapping(value = "/categories/{categoryId}/entries/{entryId}/reviews",
            produces = "application/json")
    public List<Review> getReviews(@PathVariable final int categoryId, @PathVariable final int entryId) {
        return List.of(mockReview());
    }

    @GetMapping(value = "/categories/{categoryId}/entries/{entryId}/reviews/{reviewId}",
            produces = "application/json")
    public Review getReview(@PathVariable final int categoryId,
                            @PathVariable final int entryId,
                            @PathVariable final int reviewId) {
        return mockReview();
    }

    @GetMapping(value = "/categories/{categoryId}/entries/{entryId}/reviews/{reviewId}/document",
            produces = "application/json")
    public Document getReviewDocument(@PathVariable final int categoryId,
                                      @PathVariable final int entryId,
                                      @PathVariable final int reviewId) {
        return mockDocument();
    }

    @PostMapping(value = "/categories/{categoryId}/entries/{entryId}/reviews",
            consumes = "application/json",
            produces = "application/json")
    public Review postReview(@PathVariable final int categoryId,
                             @PathVariable final int entryId,
                             @RequestBody final Review review) {
        this.reviewMockup = review;
        return this.reviewMockup;
    }

    @PutMapping(value = "/categories/{categoryId}/entries/{entryId}/reviews/{reviewId}/document",
            consumes = "application/json",
            produces = "application/json")
    public Document putReviewDocument(@PathVariable final int categoryId,
                                      @PathVariable final int entryId,
                                      @PathVariable final int reviewId,
                                      @RequestBody final Document document) {
        return mockDocument();
    }

    @PatchMapping(value = "categories/{categoryId}/entries/{entryId}/reviews/{reviewId}",
            consumes = "application/json",
            produces = "application/json")
    public Review patchReview(@PathVariable final int categoryId,
                              @PathVariable final int entryId,
                              @PathVariable final int reviewId,
                              @RequestBody final Review.ReviewUpdater reviewUpdater) {
        return mockReview();
    }


    private Review mockReview() {
        if (this.reviewMockup != null) {
            return reviewMockup;
        }

        Review.ReviewSelector reviewId = new Review.ReviewSelector(new Entry.EntrySelector(42), 13);
        return new Review(reviewId, new User.UserSelector("89624"), new Entry.EntrySelector(42));
    }

    private Document mockDocument() {
        Document.DocumentSelector documentId = new Document.DocumentSelector("356");
        return new Document(documentId);
    }

}
