package com.peerrequest.app.api;

import com.peerrequest.app.model.Category;
import com.peerrequest.app.model.Document;
import com.peerrequest.app.model.Entry;
import com.peerrequest.app.model.Review;
import com.peerrequest.app.model.User;
import java.util.ArrayList;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * The controller class for users to encapsulate the functionalities of the services
 *  * and provide them as HTTP-endpoints.
 */
@RestController
@ApiControllerPrefix
public class UserController {

    /**
     * Method used to return all entries of a specific user.
     *
     * @param userId the id of the user
     * @return a list with all the entries of the user
     */
    @GetMapping(value = "/users/{userId}/entries", produces = "application/json")
    public List<Entry> getEntries(@PathVariable int userId) {
        List<Entry> entryList = new ArrayList<>();
        entryList.add(this.mockUpEntryOne);
        entryList.add(this.mockUpEntryTwo);
        return entryList;
    }

    /**
     * Method used to return a specific user.
     *
     * @return a list with all the entries of the user
     */
    @GetMapping(value = "/users/{userId}", produces = "application/json")
    public User getUser() {
        return mockUpUser;
    }

    /**
     * Method used to return all entries of the logged-in user.
     *
     * @return a list with all the entries of the user
     */
    @GetMapping(value = "/users/self/entries", produces = "application/json")
    public List<Entry> getSelfEntries() {
        List<Entry> entryList = new ArrayList<>();
        entryList.add(this.mockUpEntryOne);
        entryList.add(this.mockUpEntryTwo);
        return entryList;
    }

    /**
     * Method used to return the logged-in user.
     *
     * @param userId the id of the user
     * @return a list with all the entries of the user
     */
    @GetMapping(value = "/users/self", produces = "application/json")
    public User getSelfUser(@PathVariable int userId) {
        return mockUpUser;
    }

    private User mockUpUser;
    private Entry mockUpEntryOne;
    private Entry mockUpEntryTwo;
    private Review mockUpReview;

    /**
     * Constructor for the class userController in order to initialize mock data.
     */
    public UserController() {
        mockUser();
        mockEntryOne();
        mockEntryTwo();
        mockReview();
    }

    //returns mock user
    private User mockUser() {
        User.UserSelector userId = new User.UserSelector("007");
        this.mockUpUser = new User(userId, "James", "Bond", "mockdata@user.com");
        return mockUpUser;
    }

    // returns mock entries
    private Entry mockEntryOne() {
        Entry.EntrySelector entryId = new Entry.EntrySelector(42);
        Category.CategorySelector categoryId = new Category.CategorySelector(10);
        Document.DocumentSelector documentId = new Document.DocumentSelector("356");
        this.mockUpEntryOne = new Entry(entryId, categoryId, this.mockUpUser.id(), documentId,
            "great Paper #01", "Karol Bender");
        return mockUpEntryOne;
    }

    private Entry mockEntryTwo() {
        Entry.EntrySelector entryId = new Entry.EntrySelector(420);
        Category.CategorySelector categoryId = new Category.CategorySelector(10);
        Document.DocumentSelector documentId = new Document.DocumentSelector("3560");
        this.mockUpEntryTwo = new Entry(entryId, categoryId, this.mockUpUser.id(), documentId,
            "great Paper #02", "Karol Bender");
        return mockUpEntryTwo;
    }

    // returns mock review
    private Review mockReview() {
        Review.ReviewSelector reviewId = new Review.ReviewSelector(mockEntryOne().getId(), 700);
        this.mockUpReview = new Review(reviewId, this.mockUpUser.id(), mockEntryOne().getId());
        return mockUpReview;
    }
}