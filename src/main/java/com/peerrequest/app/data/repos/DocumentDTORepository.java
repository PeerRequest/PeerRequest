package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.DocumentDTO;
import org.springframework.data.repository.CrudRepository;

/**
 * Defines a DocumentDTO Repository.
 */
public interface DocumentDTORepository
    extends CrudRepository<DocumentDTO, String> {
}
