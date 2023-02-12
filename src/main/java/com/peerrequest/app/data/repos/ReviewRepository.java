package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Review;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Defines a Review Repository.
 */
public interface ReviewRepository extends CrudRepository<Review, Long>, JpaRepository<Review, Long> {
    List<Review> findByEntryId(@Param("entry_id") Long entryId);

    Page<Review> findByEntryId(Long entryId, Pageable pageable);

    Page<Review> findByReviewerId(String reviewerId, Pageable pageable);

    @Query("select r.reviewerId from Review r where r.entryId = ?1")
    List<String> getAllReviewerIdsByEntryId(Long entryId);
}