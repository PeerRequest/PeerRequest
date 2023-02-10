package com.peerrequest.app.services;

import com.peerrequest.app.data.DirectRequestProcess;
import java.util.Optional;

/**
 * Describes the functionality of a DirectRequestProcessService.
 */
public interface DirectRequestProcessService
        extends CrudService<DirectRequestProcess, Long, DirectRequestProcess.Dto> {
    Optional<DirectRequestProcess> getByEntry(Long entryId);
}