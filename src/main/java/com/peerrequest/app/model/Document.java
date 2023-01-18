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

    public Document(DocumentSelector id) {
        this.id = id;
    }

    @Getter
    @Setter
    private DocumentSelector id;

    public record DocumentSelector(String documentID) {
    }
}
