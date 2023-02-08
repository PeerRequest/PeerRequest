package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Entry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;


/**
 * Defines an Entry Repository.
 */
public interface EntryRepository extends CrudRepository<Entry, Long>, JpaRepository<Entry, Long> {
    Page<Entry> findByCategoryId(Long categoryId, Pageable pageable);
}
