package com.peerrequest.app.api;

import com.peerrequest.app.model.Category;
import com.peerrequest.app.model.Document;
import com.peerrequest.app.model.Entry;
import com.peerrequest.app.model.User;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.List;

@RestController
@RequestMapping
public class EntriesController {

    @GetMapping(value = "categories/{categoryId}/entries",
            produces = "application/json")
    public List<Entry> getEntries(@PathVariable final int categoryId) {
        return List.of(mockEntry());
    }

    @GetMapping(value = "categories/{categoryId}/entries/{entryId}",
            produces = "application/json")
    public Entry getEntry(@PathVariable final int categoryId,
                          @PathVariable final int entryId) {
        return mockEntry();
    }

    @GetMapping(value = "categories/{categoryId}/entries/{entryId}/paper",
            produces = "application/json")
    public Document getResearchPaper(@PathVariable final int categoryId,
                                     @PathVariable final int entryId) {
        return mockEntry().getDocument();
    }

    @PostMapping(value = "categories/{categoryId}/entries",
            consumes = "application/json",
            produces = "application/json")
    public Entry postEntry(@PathVariable final int categoryId,
                             @RequestBody final Entry entry) {
        return mockEntry();
    }

    @PatchMapping(value = "categories/{categoryId}/entries/{entryId}",
            consumes = "application/json",
            produces = "application/json")
    public Entry patchEntry(@PathVariable final int categoryId,
                                    @PathVariable final int entryId,
                                    @RequestBody final Entry.EntryUpdater entryUpdater) {
        return mockEntry();
    }

    @DeleteMapping(value ="categories/{categoryId}/entries/{entryId}", consumes = "application/json")
    public void deleteEntry(@PathVariable final int categoryId, @PathVariable final int entryId) {
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
}
