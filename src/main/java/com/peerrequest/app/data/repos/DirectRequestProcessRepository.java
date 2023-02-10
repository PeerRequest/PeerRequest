package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.DirectRequestProcess;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;


/**
 * Defines a DirectRequestProcess Repository.
 */
public interface DirectRequestProcessRepository extends JpaRepository<DirectRequestProcess, Long> {

    @Query("select d from DirectRequestProcess d where d.entryId = ?1")
    Optional<DirectRequestProcess> getByEntryId(Long entryId);
}