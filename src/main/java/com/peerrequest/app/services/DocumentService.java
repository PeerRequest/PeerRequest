package com.peerrequest.app.services;

import com.peerrequest.app.model.Document;

/**
 * Interface responsible for CRUD operations for Document objects.
 * @param <F> EntityFilter
 * @param <U> EntityUpdater
 */
public interface DocumentService<F, U> extends CrudService<Document, Document.DocumentSelector, F, U> {
}
