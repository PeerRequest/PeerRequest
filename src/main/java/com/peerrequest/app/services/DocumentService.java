package com.peerrequest.app.services;

import com.peerrequest.app.data.Document;
import java.util.List;
import java.util.Optional;

/**
 * Defines the functionality of a DocumentService.
 */
public interface DocumentService
    extends CrudService<Document, String, Document.Dto> {

    /**
     * Retrieves the raw bytes of a document.
     *
     * @param id ID of the document
     * @return raw bytes of this document
     */
    Optional<List<Byte>> data(String id);

    /**
     * Stores the data for a specific document.
     *
     * @param id   ID of the document
     * @param data raw bytes of this document
     * @throws Exception in case we are not able to store the data
     */
    void store(String id, List<Byte> data) throws Exception;
}
