package com.peerrequest.app.services;

import com.peerrequest.app.data.DirectRequest;
import java.util.List;


/**
 * Describes the functionality of a DirectRequestService.
 */
public interface DirectRequestService
        extends CrudService<DirectRequest, Long, DirectRequest.Dto> {

    List<DirectRequest> listByDirectRequestProcessIdAndState(Long directRequestProcessId,
                                                             DirectRequest.RequestState state);

    List<DirectRequest> listByDirectRequestProcessId(Long directRequestProcessId);

}
