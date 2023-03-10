package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Message;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

/**
 * Defines a Message Repository.
 */
public interface MessageRepository
    extends CrudRepository<Message, Long>, JpaRepository<Message, Long> {

    Page<Message> findByReviewId(Long reviewId, Pageable pageable);

    List<Message> findByReviewId(Long reviewId);
}
