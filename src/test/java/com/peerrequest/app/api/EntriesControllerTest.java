package com.peerrequest.app.api;

import com.peerrequest.app.model.Category;
import com.peerrequest.app.model.Document;
import com.peerrequest.app.model.Entry;
import com.peerrequest.app.model.User;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class EntriesControllerTest {

    private Entry testEntry;
    private EntriesController testEntryController;

    @BeforeEach
    void initEntryTest() {
        this.testEntry = new Entry(new Entry.EntrySelector(1), new Category.CategorySelector(1),
                new User.UserSelector("1"), new Document.DocumentSelector("1"),
                "Great Title #01", "");
        this.testEntryController = new EntriesController();
        this.testEntryController.postEntry(1, testEntry);
    }

    @Test
    void getEntrySuccess() {
        Assert.assertEquals(testEntryController.getEntry(1, 1), testEntry);
    }

    @Test
    void getEntryFailure() {
        Assert.assertNotEquals(testEntryController.getEntry(1, -1), testEntry);
    }
}