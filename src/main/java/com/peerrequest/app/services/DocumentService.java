package com.peerrequest.app.services;

import com.peerrequest.app.data.Document;
import java.util.List;
import java.util.Optional;

/**
 * Defines the functionality of a DocumentService.
 */
public interface DocumentService
        extends CrudService<Document, String, Document.Dto> {
}