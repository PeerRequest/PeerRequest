package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

public class Entry {
    @Getter
    @Setter
    EntrySelector id;

    @Getter
    @Setter
    Document document;

    @Getter
    @Setter
    String title;

    @Getter
    @Setter
    String authors;

    public record EntrySelector(Category.CategorySelector categorySelector, long id) {
    }
}
