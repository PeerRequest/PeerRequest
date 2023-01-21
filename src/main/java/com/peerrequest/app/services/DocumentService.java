package com.peerrequest.app.services;

import com.peerrequest.app.model.Document;

/**
 * Interface responsible for CRUD operations for objects of the class Document.
 *
 * @param <F> Entity Filter
 * @param <U> Entity Updater
 */
public interface DocumentService<F, U>
    extends CrudService<Document, Document.DocumentSelector, F, U> {
}
