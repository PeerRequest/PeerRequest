package com.peerrequest.app.model;


import lombok.Getter;
import lombok.Setter;

/**
 * This class represents an entry.
 *
 * @author Ludger Halpick
 * @author Michael Mildt
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
        this.categoryID = categoryID;
        this.researcherID = researcherID;
        this.documentID = documentID;
        this.title = title;
        this.authors = authors;
    }
    @Getter
    private final EntrySelector id;

    @Getter
    private final Category.CategorySelector categoryID;

    @Getter
    private final User.UserSelector researcherID;

    @Getter
    private final Document.DocumentSelector documentID;

    @Getter
    @Setter
    private Document document;

    @Getter
    @Setter
    private String title;

    @Getter
    @Setter
    private String authors;

    public record EntrySelector(long entryID) {
    }

    public record EntryUpdater(String title, String authors) {

    }
}
