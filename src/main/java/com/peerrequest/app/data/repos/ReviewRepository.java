package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Entry;
import com.peerrequest.app.data.Review;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Defines a Review Repository.
 */
public interface ReviewRepository extends CrudRepository<Review, Long>, JpaRepository<Review, Long> {
    Page<Review> findByEntryId(Long entryId, Pageable pageable);
}