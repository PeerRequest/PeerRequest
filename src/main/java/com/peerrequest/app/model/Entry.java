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

    /**
     * Constructor of an entry.
     *
     * @param id            id of the entry
     * @param researcherID  id of the user who created the entry
     * @param documentID    id of the document attached to the entry
     * @param title         title of the entry
     * @param authors       authors of the entry
     */
    public Entry(EntrySelector id, Category.CategorySelector categoryID, User.UserSelector researcherID,
                 Document.DocumentSelector documentID, String title, String authors) {
        this.id = id;
        this.categoryId = categoryID;
        this.researcherId = researcherID;
        this.documentId = documentID;
        this.title = title;
        this.authors = authors;
    }
    @Getter
    private final EntrySelector id;

    @Getter
    private final Category.CategorySelector categoryId;

    @Getter
    private final User.UserSelector researcherId;

    @Getter
    private final Document.DocumentSelector documentId;

    @Getter
    @Setter
    private Document document;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String authors;

    /**
     * Identifies an Entry.
     *
     * @param entryId   id of the Review
     */
    public record EntrySelector(long entryId) {
    }

    /**
     * Identifies the updater for an Entry.
     *
     * @param title     title of the entry to be updated
     * @param authors   authors of the entry to be updated
     */
    public record EntryUpdater(String title, String authors) {

    }
}
