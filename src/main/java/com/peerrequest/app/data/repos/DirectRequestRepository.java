package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.DirectRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DirectRequestRepository extends CrudRepository<DirectRequest, Long> {
    /**
     * Defines the list operation.
     *
     * @param cursor   if not null the last id of the previous page
     * @param pageable describes the pages
     * @return a list of all matching entries
     */
    default List<DirectRequest> list(Long cursor, Pageable pageable, Long directRequestProcessId, String userId) {

        if (userId != null) {
            if (cursor == null) {
                return listByUser(pageable, userId);
            } else {
                return listByUser(cursor, pageable, userId);
            }
        }

        if (directRequestProcessId != null) {
            if (cursor == null) {
                return listByEntry(pageable, directRequestProcessId);
            } else {
                return listByEntry(cursor, pageable, directRequestProcessId);
            }
        }

        if (cursor == null) {
            return list(pageable);
        } else {
            return list(cursor, pageable);
        }
    }

    @Query("select e from DirectRequest e where e.reviewerId = ?2 order by e.id DESC")
    List<DirectRequest> listByUser(Pageable pageable, String userId);

    @Query("select e from DirectRequest e where e.id < ?1 and e.reviewerId = ?3 order by e.id DESC")
    List<DirectRequest> listByUser(Long cursor, Pageable pageable, String userId);

    @Query("select e from DirectRequest e where e.directRequestProcessId = ?2 order by e.id DESC")
    List<DirectRequest> listByEntry(Pageable pageable, Long directRequestProcessId);

    @Query("select e from DirectRequest e where e.id < ?1 and e.directRequestProcessId = ?3 order by e.id DESC")
    List<DirectRequest> listByEntry(Long cursor, Pageable pageable, Long directRequestProcessId);

    @Query("select e from DirectRequest e order by e.id DESC")
    List<DirectRequest> list(Pageable pageable);

    @Query("select e from DirectRequest e where e.id < ?1 order by e.id DESC")
    List<DirectRequest> list(Long cursor, Pageable pageable);

}
