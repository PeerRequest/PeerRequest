package com.peerrequest.app.services;

import com.peerrequest.app.model.Document;

/**
 * Interface for the document attached to objects.
 * @param <P> the object the document is attached to
 */
public interface DocumentAttachment<P> {

    /**
     * returns the document attached to the selected object.
     * @param selector the selected object
     * @return the document attached to the selected object
     */
    Document.DocumentSelector document(P selector);
}
