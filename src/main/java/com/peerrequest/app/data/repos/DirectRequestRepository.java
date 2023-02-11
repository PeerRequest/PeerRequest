package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.DirectRequest;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

/**
 * Defines a DirectRequest Repository.
 */
public interface DirectRequestRepository
        extends CrudRepository<DirectRequest, Long>, JpaRepository<DirectRequest, Long> {

    Page<DirectRequest> findByReviewerId(String reviewerId, Pageable pageable);

    Page<DirectRequest> findByDirectRequestProcessId(Long directRequestProcessId, Pageable pageable);

    List<DirectRequest> findByDirectRequestProcessId(Long directRequestProcessId);

    List<DirectRequest> findByDirectRequestProcessIdAndState(
            @Param("direct_request_process_id") Long directRequestProcessId,
            @Param("state") DirectRequest.RequestState state);
}
