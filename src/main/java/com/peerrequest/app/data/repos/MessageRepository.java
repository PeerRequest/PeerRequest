package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Message;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Defines a Message Repository.
 */
public interface MessageRepository
    extends CrudRepository<Message, Long> {

    /**
     * Defines the list operation.
     *
     * @param cursor   if not null the last id of the previous page
     * @param pageable describes the pages
     * @param reviewId if not null is used to filter by a review
     * @return a list of all matching categories
     */
    default List<Message> list(Long cursor, Pageable pageable, Long reviewId) {
        if (cursor == null && reviewId == null) {
            return list(pageable);
        } else if (cursor == null) {
            return listIsInReview(reviewId, pageable);
        } else if (reviewId == null) {
            return list(cursor, pageable);
        } else {
            return listIsInReview(cursor, reviewId, pageable);
        }
    }

    @Query("select m from Message m order by m.id DESC")
    List<Message> list(Pageable pageable);

    @Query("select m from Message m where m.id < ?1 order by m.id DESC")
    List<Message> list(Long cursor, Pageable pageable);

    @Query("select m from Message m where m.id < ?1 and m.reviewId = ?2 order by m.id DESC")
    List<Message> listIsInReview(Long cursor, Long reviewId, Pageable pageable);

    @Query("select m from Message m where m.reviewId = ?1 order by m.id DESC")
    List<Message> listIsInReview(Long reviewId, Pageable pageable);
}
