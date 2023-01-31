package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Entry;
import org.springframework.data.repository.CrudRepository;

/**
 * Defines an Entry Repository.
 */
public interface EntryRepository extends CrudRepository<Entry, Long> {

}
