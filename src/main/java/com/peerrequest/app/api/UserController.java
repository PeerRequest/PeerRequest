package com.peerrequest.app.api;

import jdk.jfr.Category;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;

@RestController
@RequestMapping
public class UserController {


    // returns mock entries
    private Entry mockEntryOne() {
        Entry.EntrySelector entryId = new Entry.EntrySelector(42);
        User.UserSelector userId = new User.UserSelector("123");
        Document.DocumentSelector documentId = new Document.DocumentSelector("356");
        Entry entry = new Entry(entryId, new CategorySelector("010"), userId, documentId, "great Paper #01", "Karol Bender");
        return entry;
    }

    private Entry mockEntryTwo() {
        Entry.EntrySelector entryId = new Entry.EntrySelector(420);
        User.CategorySelector categoryId = new Category.CategorySelector("010");
        Document.DocumentSelector documentId = new Document.DocumentSelector("3560");
        Entry entry = new Entry(entryId, categoryId, mockUser().getId, documentId, "great Paper #02", "Karol Bender");
        return entry;
    }
}
