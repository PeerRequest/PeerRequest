package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Entry;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;


/**
 * Defines an Entry Repository.
 */
public interface EntryRepository extends CrudRepository<Entry, Long>, JpaRepository<Entry, Long> {
    Page<Entry> findByCategoryId(Long categoryId, Pageable pageable);

    List<Entry> findByCategoryId(@Param("category_id") Long categoryId);

    Page<Entry> findByResearcherId(String researcherId, Pageable pageable);
}
