package com.peerrequest.app.api;


import com.peerrequest.app.model.*;
import org.springframework.web.bind.annotation.*;

/**
 * The RequestsController handles all directRequests interaction with the backend.
 */
@RestController
@ApiControllerPrefix
public class DirectRequestsController {

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
    public DirectRequestProcess getDirectRequestProcess(@PathVariable final long categoryId,
                                                        @PathVariable final long entryId) {
        return mockDirectRequestProcess();
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
        this.directRequestProcessMockUp = directRequestProcess;
        return directRequestProcessMockUp;
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
                                                          @RequestBody final DirectRequestProcessUpdater updater) {
        mockDirectRequestProcess().setOpenSlots(updater.openSlots());
        return directRequestProcessMockUp;
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
        return mockDirectRequest();
    }

    /**
     * Patches a directRequest.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the DirectRequestProcess
     * @param requestId Request to patch
     * @param updater updater containing a Request.State
     *
     * @return the updated request
     */
    @PatchMapping(value = "/categories/{categoryId}/entries/{entryId}/process/requests/{requestId}",
        produces = "application/json")
    public DirectRequest patchDirectRequest(@PathVariable final long categoryId,
                                            @PathVariable final long entryId,
                                            @PathVariable final long requestId,
                                            @RequestBody final RequestUpdater updater) {
        // differentiate between Request.States.ACCEPTED and Request.States.DECLINED and call the corresponding
        // method of the RequestService: declineDirectRequest() or acceptDirectRequest
        mockDirectRequest().setRequestState(updater.state());
        return this.directRequestMockUp;
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
        this.directRequestMockUp = request;
        return directRequestMockUp;
    }

    // RequestServiceMethods

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
        //todo: add RequestService.claimOpenSlot(...)
        throw new RuntimeException("not implemented");
    }

    // MOCK DATA //////////////////////////////////////////////////////////////////////////////////////////////

    private DirectRequestProcess directRequestProcessMockUp;
    private DirectRequest directRequestMockUp;

    private DirectRequestProcess mockDirectRequestProcess() {

        if (this.directRequestProcessMockUp != null) {
            return directRequestProcessMockUp;
        }

        DirectRequestProcess.DirectRequestProcessSelector directRequestProcessId
                = new DirectRequestProcess.DirectRequestProcessSelector(1);
        Entry.EntrySelector entryId =  new Entry.EntrySelector(1);

        this.directRequestProcessMockUp = new DirectRequestProcess(directRequestProcessId, entryId);
        return directRequestProcessMockUp;
    }

    private DirectRequest mockDirectRequest() {
        if (this.directRequestMockUp != null) {
            return this.directRequestMockUp;
        }

        Request.RequestSelector requestId = new Request.RequestSelector(1);
        User.UserSelector reviewerId = new User.UserSelector("1");

        this.directRequestMockUp = new DirectRequest(requestId, reviewerId, mockDirectRequestProcess().getId());
        return this.directRequestMockUp;
    }

    private record DirectRequestProcessUpdater(int openSlots) {}

    private record RequestUpdater(Request.RequestState state) {}
}
