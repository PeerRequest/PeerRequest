package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Entry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Defines an Entry Repository.
 */
public interface EntryRepository extends CrudRepository<Entry, Long> {
    /**
     * Defines the list operation.
     *
     * @param cursor   if not null the last id of the previous page
     * @param pageable describes the pages
     * @param name     if not null is used as a search filter
     * @return a list of all matching entries
     */
    default List<Entry> list(Long cursor, Pageable pageable, String name) {
        // TODO: implement
        throw new RuntimeException("");
    }

}
