package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.DirectRequest;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;



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
                return listByUser(userId, pageable);
            } else {
                return listByUser(cursor, userId, pageable);
            }
        }

        if (directRequestProcessId != null) {
            if (cursor == null) {
                return listByEntry(directRequestProcessId, pageable);
            } else {
                return listByEntry(cursor, directRequestProcessId, pageable);
            }
        }

        if (cursor == null) {
            return list(pageable);
        } else {
            return list(cursor, pageable);
        }
    }

    @Query("select e from DirectRequest e where e.reviewerId = ?1 order by e.id DESC")
    List<DirectRequest> listByUser(String userId, Pageable pageable);

    @Query("select e from DirectRequest e where e.id < ?1 and e.reviewerId = ?2 order by e.id DESC")
    List<DirectRequest> listByUser(Long cursor, String userId, Pageable pageable);

    @Query("select e from DirectRequest e where e.directRequestProcessId = ?1 order by e.id DESC")
    List<DirectRequest> listByEntry(Long directRequestProcessId, Pageable pageable);

    @Query("select e from DirectRequest e where e.id < ?1 and e.directRequestProcessId = ?2 order by e.id DESC")
    List<DirectRequest> listByEntry(Long cursor, Long directRequestProcessId, Pageable pageable);

    @Query("select e from DirectRequest e order by e.id DESC")
    List<DirectRequest> list(Pageable pageable);

    @Query("select e from DirectRequest e where e.id < ?1 order by e.id DESC")
    List<DirectRequest> list(Long cursor, Pageable pageable);

}
