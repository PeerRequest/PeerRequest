package com.peerrequest.app.model;


import lombok.Getter;
import lombok.Setter;

/**
 * This class represents an entry.
 *
 * @author User1 Halpick
 * @author User5 Mildt
 * @version 0.0.1
 */
public class Entry {

    @Getter
    private final EntrySelector id;
    @Getter
    private final Category.CategorySelector categorySelector;
    @Getter
    private final User.UserSelector researcherSelector;
    @Getter
    private final Document.DocumentSelector documentSelector;
    @Getter
    private final String title;
    @Getter
    private final String authors;
    @Getter
    @Setter
    private Document document;

    /**
     * Constructor of an entry.
     *
     * @param id                 id of the entry
     * @param researcherSelector id of the user who created the entry
     * @param documentSelector   id of the document attached to the entry
     * @param title              title of the entry
     * @param authors            authors of the entry
     */
    public Entry(EntrySelector id, Category.CategorySelector categorySelector, User.UserSelector researcherSelector,
                 Document.DocumentSelector documentSelector, String title, String authors) {
        this.id = id;
        this.categorySelector = categorySelector;
        this.researcherSelector = researcherSelector;
        this.documentSelector = documentSelector;
        this.title = title;
        this.authors = authors;
    }

    /**
     * Identifies an Entry.
     *
     * @param id id of the entry
     */
    public record EntrySelector(long id) {
    }
}
