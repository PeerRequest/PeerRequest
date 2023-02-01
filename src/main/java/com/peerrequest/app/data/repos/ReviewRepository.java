package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Entry;
import com.peerrequest.app.data.Review;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Defines a Review Repository.
 */
public interface ReviewRepository extends CrudRepository<Review, Long> {
    /**
     * Defines the list operation.
     *
     * @param cursor     if not null the last id of the previous page
     * @param pageable   describes the pages
     * @param entryId    if not null is user to filter by an entry
     * @param reviewerId if not null is used to filter by a reviewer
     * @return a list of all matching entries
     */
    default List<Review> list(Long cursor, Pageable pageable, Long entryId, String reviewerId) {

        if (entryId != null) {
            if (cursor == null) {
                return listIsInEntry(entryId, pageable);
            } else {
                return listIsInEntry(cursor, entryId, pageable);
            }
        }

        if (cursor == null && reviewerId == null) {
            return list(pageable);
        } else if (cursor == null) {
            return listReviewerContains(reviewerId, pageable);
        } else if (reviewerId == null) {
            return list(cursor, pageable);
        } else {
            return listReviewerContains(cursor, reviewerId, pageable);
        }
    }

    @Query("select r from Review r order by r.id DESC")
    List<Review> list(Pageable pageable);

    @Query("select r from Review r where r.id < ?1 order by r.id DESC")
    List<Review> list(Long cursor, Pageable pageable);

    @Query("select r from Review r where r.id < ?1 and r.entryId = ?2 order by r.id DESC")
    List<Review> listIsInEntry(Long cursor, Long entryId, Pageable pageable);

    @Query("select r from Review r where r.entryId = ?1 order by r.id DESC")
    List<Review> listIsInEntry(Long entryId, Pageable pageable);

    @Query("select r from Review r where r.id < ?1 and r.reviewerId like concat('%', ?2, '%') order by r.id DESC")
    List<Review> listReviewerContains(Long cursor, String reviewerId, Pageable pageable);

    @Query("select r from Review r where r.reviewerId like concat('%', ?1, '%') order by r.id DESC")
    List<Review> listReviewerContains(String reviewerId, Pageable pageable);
}