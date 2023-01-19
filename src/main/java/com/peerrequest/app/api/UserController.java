package com.peerrequest.app.api;

import com.peerrequest.app.model.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping
public class UserController {

    @GetMapping(value = "/users/{userId}/reviews", produces = "application/json")
    public List<Review> getReviews(@PathVariable int userId) {
        List<Entry> entryList = new ArrayList<>();
        entryList.add(mockEntryOne());
        entryList.add(mockEntryTwo());
        return entryList;
    }

    @GetMapping(value = "/users/{userId}/entries", produces = "application/json")
    public List<Review> getReviews(@PathVariable int userId)

    //returns mock user
    private User mockUser() {
        User.UserSelector userId = new User.UserSelector("007");
        User user = new User(userId,"James", "Bond", "mockdata@user.com");
        return user;
    }
    // returns mock entries
    private Entry mockEntryOne() {
        Entry.EntrySelector entryId = new Entry.EntrySelector(42);
        Category.CategorySelector categoryId = new Category.CategorySelector(10);
        Document.DocumentSelector documentId = new Document.DocumentSelector("356");
        Entry entry = new Entry(entryId, categoryId, mockUser().getId(), documentId, "great Paper #01", "Karol Bender");
        return entry;
    }

    private Entry mockEntryTwo() {
        Entry.EntrySelector entryId = new Entry.EntrySelector(420);
        Category.CategorySelector categoryId = new Category.CategorySelector(10);
        Document.DocumentSelector documentId = new Document.DocumentSelector("3560");
        Entry entry = new Entry(entryId, categoryId, mockUser().getId(), documentId, "great Paper #02", "Karol Bender");
        return entry;
    }

    // returns mock review
    private Review mockReview() {
        Review.ReviewSelector reviewId = new Review.ReviewSelector(mockEntryOne().getId(),700);
        Review review = new Review(reviewId, mockUser().getId(), mockEntryOne().getId());
        return review;
    }
}
