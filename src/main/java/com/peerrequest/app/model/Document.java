package com.peerrequest.app.model;


import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a document.
 *
 * @author User1 Halpick
 * @version 0.0.1
 */
public class Document {

    public Document(DocumentSelector id) {
        this.id = id;
    }

    @Getter
    @Setter
    private DocumentSelector id;

    public record DocumentSelector(String documentID) {
    }

    public record DocumentUpdater() { }
}
