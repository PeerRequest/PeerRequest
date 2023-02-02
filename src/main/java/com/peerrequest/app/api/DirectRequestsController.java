package com.peerrequest.app.api;

import com.peerrequest.app.data.DirectRequest;
import com.peerrequest.app.data.DirectRequestProcess;
import org.springframework.web.bind.annotation.*;

/**
 * The RequestsController handles all directRequests interaction with the backend.
 */
@RestController
@ApiControllerPrefix
public class DirectRequestsController extends ServiceBasedController {

    // DirectRequestProcess

    /**
     * Gets a directRequestProcess.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the directRequestProcess
     *
     * @return directRequestProcess of entry
     */
    @GetMapping(value = "/categories/{categoryId}/entries/{entryId}/process", produces = "application/json")
    public DirectRequestProcess getDirectRequestProcess(@PathVariable() final long categoryId,
                                                        @PathVariable final long entryId) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    /**
     * Creates a directRequestProcess.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the directRequestProcess
     * @param directRequestProcess directRequestProcess to add
     *
     * @return the added directRequestProcess
     */
    @PutMapping(value = "/categories/{categoryId}/entries/{entryId}/process", produces = "application/json")
    public DirectRequestProcess putDirectRequestProcess(@PathVariable final long categoryId,
                                                        @PathVariable final long entryId,
                                                        @RequestBody final DirectRequestProcess directRequestProcess) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    /**
     * Patches a directRequestProcess.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the directRequestProcess
     * @param updater updater containing the amount of openSlots (int)
     *
     * @return updated directRequestProcess
     */
    @PatchMapping(value = "/categories/{categoryId}/entries/{entryId}/process", produces = "application/json")
    public DirectRequestProcess patchDirectRequestProcess(@PathVariable final long categoryId,
                                                          @PathVariable final long entryId,
                                                          @RequestBody final DirectRequest.Dto updater) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    // DirectRequest

    /**
     * Gets a directRequests.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the directRequestProcess
     * @param requestId request to get
     *
     * @return directRequest with requestId
     */
    @GetMapping(value = "/categories/{categoryId}/entries/{entryId}/process/requests/{requestId}",
            produces = "application/json")
    public DirectRequest getDirectRequest(@PathVariable final long categoryId,
                                          @PathVariable final long entryId,
                                          @PathVariable final long requestId) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    /**
     * Patches a directRequest.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the DirectRequestProcess
     * @param requestId DirectRequest to patch
     * @param updater updater containing a DirectRequest.State
     *
     * @return the updated request
     */
    @PatchMapping(value = "/categories/{categoryId}/entries/{entryId}/process/requests/{requestId}",
            produces = "application/json")
    public DirectRequest patchDirectRequest(@PathVariable final long categoryId,
                                            @PathVariable final long entryId,
                                            @PathVariable final long requestId,
                                            @RequestBody final DirectRequest.Dto updater) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    /**
     * Creates a new directRequest.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the directRequestProcess
     * @param request request to add
     *
     * @return the added request
     */
    @PutMapping(value = "/categories/{categoryId}/entries/{entryId}/process/requests",
            produces = "application/json")
    public DirectRequest putDirectRequest(@PathVariable final long categoryId,
                                          @PathVariable final long entryId,
                                          @RequestBody final DirectRequest request) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }


    /**
     * Claims an open slot.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the directRequestProcess
     * @param userId user who claims open slot
     *
     * @return directRequest with state=ACCEPTED
     */
    @PostMapping(value = "/categories/{categoryId}/entries/{entryId}/process/claim")
    public DirectRequest claimOpenSlot(@PathVariable final long categoryId,
                                       @PathVariable final long entryId,
                                       @RequestBody final String userId) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

}