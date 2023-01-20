package com.peerrequest.app.api;

import com.peerrequest.app.model.*;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping
public class CategoriesController {

    @GetMapping(value = "/categories", produces = "application/json")
    public List<Category> getCategories() {
        return List.of(mockUpCategory);
    }

    @GetMapping(value = "/categories/{categoryId}", produces = "application/json")
    public Category getCategory(@PathVariable final int categoryId) {
        return mockUpCategory;
    }

    @PostMapping(value = "/categories", produces = "application/json")
    public Category createCategory() {
        return mockUpCategory;
    }

    @PatchMapping(value = "/categories/{categoryId}", consumes = "application/json", produces = "application/json")
    public Category patchCategory(@RequestBody final Category.CategoryUpdater categoryUpdater) {
        mockCategory().setName(categoryUpdater.name());
        mockCategory().setYear(categoryUpdater.year());
        return mockUpCategory;
    }

    @DeleteMapping(value = "categories/{categoryId}/", consumes = "application/json")
    public void deleteCategory(@PathVariable final int categoryId) {
    }

    private Category mockUpCategory;


    // returns mock category
    private Category mockCategory() {
        Category.CategorySelector categoryId = new Category.CategorySelector(12);
        this.mockUpCategory = new Category(categoryId,
                new User.UserSelector("140314"),
                "Best International Conference",
                Category.CategoryLabel.INTERNAL,
                2020, ZonedDateTime.now(),
                0,
                5,
                1);
        return mockUpCategory;
    }

    //returns mock user
    private User mockUser() {
        User.UserSelector userId = new User.UserSelector("007");
        User user = new User(userId, "James", "Bond", "mockdata@user.com");
        return user;
    }
    // returns mock entries
    private Entry mockEntryOne() {
        Entry.EntrySelector entryId = new Entry.EntrySelector(42);
        Document.DocumentSelector documentId = new Document.DocumentSelector("356");
        Entry entry = new Entry(entryId, mockCategory().getId(), mockUser().getId(), documentId,
                "great Paper #01", "Karol Bender");
        return entry;
    }

    private Entry mockEntryTwo() {
        Entry.EntrySelector entryId = new Entry.EntrySelector(420);
        Document.DocumentSelector documentId = new Document.DocumentSelector("3560");
        Entry entry = new Entry(entryId, mockCategory().getId(), mockUser().getId(), documentId,
                "great Paper #02", "Karol Bender");
        return entry;
    }

    // returns mock review
    private Review mockReview() {
        Review.ReviewSelector reviewId = new Review.ReviewSelector(mockEntryOne().getId(), 700);
        Review review = new Review(reviewId, mockUser().getId(), mockEntryOne().getId());
        return review;
    }
}
