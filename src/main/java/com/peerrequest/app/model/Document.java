package com.peerrequest.app.model;


import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a document.
 *
 * @author Ludger Halpick
 * @version 0.0.1
 */
public class Document {
    @Getter
    @Setter
    private DocumentSelector id;

    public Document(DocumentSelector id) {
        this.id = id;
    }

    /**
     * Identifies a Document.
     *
     * @param id id of the Document
     */
    public record DocumentSelector(String id) {
    }
}
