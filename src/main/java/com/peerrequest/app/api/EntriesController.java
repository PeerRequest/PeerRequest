package com.peerrequest.app.api;

import com.peerrequest.app.model.Category;
import com.peerrequest.app.model.Document;
import com.peerrequest.app.model.Entry;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping
public class EntriesController {

    @GetMapping(value = "categories/{categoryId}/entries", produces = "application/json")
    public List<Entry> getEntries(@PathVariable int categoryId) {
        return List.of(mockEntry());
    }

    @GetMapping(value = "categories/{categoryId}/entries/{entryId}", produces = "application/json")
    public Entry getEntry(@PathVariable int categoryId, @PathVariable int entryId) {
        return mockEntry();
    }

    @GetMapping(value = "categories/{categoryId}/entries/{entryId}/paper", produces = "application/json")
    public Document getResearchPaper(@PathVariable int categoryId, @PathVariable int entryId) {
        return mockEntry().getDocument();
    }

    // returns mock category
    private Category mockCategory() {
        Category category = new Category();
        category.setId(new Category.CategorySelector(5));
        return category;
    }

    // returns mock entry
    private Entry mockEntry() {
        Entry entry = new Entry();
        entry.setId(new Entry.EntrySelector(mockCategory().getId(), 42));
        entry.setTitle("Great Paper #01");
        entry.setAuthors("Karol Bender");
        Document document = new Document();
        document.setId(new Document.DocumentSelector("123"));
        return entry;
    }
}
