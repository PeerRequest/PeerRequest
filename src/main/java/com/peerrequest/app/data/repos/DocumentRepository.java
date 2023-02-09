package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Document;
import org.springframework.data.repository.CrudRepository;

/**
 * Defines a Document Repository.
 */
public interface DocumentRepository
        extends CrudRepository<Document, String> {
}