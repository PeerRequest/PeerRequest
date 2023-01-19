package com.peerrequest.app.api;

import com.peerrequest.app.model.*;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

public class ReviewsController {

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
        return mockReview().getReviewDocument();
    }

    @PostMapping(value = "/categories/{categoryId}/entries/{entryId}/reviews",
            consumes = "application/json",
            produces = "application/json")
    public Review postReview(@PathVariable final int categoryId,
                               @PathVariable final int entryId,
                               @RequestBody final Review review) {
        return mockReview();
    }

    @PutMapping(value = "/categories/{categoryId}/entries/{entryId}/reviews/{reviewId}/document",
            consumes = "application/json",
            produces = "application/json")
    public Document putReviewDocument(@PathVariable final int categoryId,
                                    @PathVariable final int entryId,
                                    @PathVariable final int reviewId,
                                    @RequestBody final Document document) {
        return mockReview().getReviewDocument();
    }


    @PatchMapping(value = "categories/{categoryId}/entries/{entryId}/reviews/{reviewId}",
            consumes = "application/json",
            produces = "application/json")
    public Review patchReview(@PathVariable final int categoryId,
                                      @PathVariable final int entryId,
                                      @PathVariable final int reviewId,
                                      @RequestBody final Review.ReviewUpdater reviewUpdater) {
        Review updatedReview =  mockReview();
        updatedReview.setScore(5);
        updatedReview.setSummary("This is a brand new summary.");
        return updatedReview;
    }

    private Category mockCategory() {
        Category.CategorySelector categoryId = new Category.CategorySelector(12);
        Category category = new Category(categoryId,
                new User.UserSelector("140314"),
                "Best International Conference",
                Category.CategoryLabel.INTERNAL,
                2020, ZonedDateTime.now(),
                0,
                5,
                1);
        return category;
    }

    private Entry mockEntry() {
        Entry.EntrySelector entryId = new Entry.EntrySelector(42);
        User.UserSelector userId = new User.UserSelector("123");
        Document.DocumentSelector documentId = new Document.DocumentSelector("356");
        Entry entry = new Entry(entryId, mockCategory().getId(), userId, documentId, "great Paper #01",
                "Karol Bender");
        return entry;
    }

    private Review mockReview() {
        Review.ReviewSelector reviewId = new Review.ReviewSelector(mockEntry().getId(), 13);
        Review review = new Review(reviewId, new User.UserSelector("89624"));
        Document.DocumentSelector documentId = new Document.DocumentSelector("19126");
        Document document = new Document(documentId);
        review.setReviewDocument(document);
        return review;
    }
}
