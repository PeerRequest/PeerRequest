package com.peerrequest.app.api;


import com.peerrequest.app.model.*;
import org.springframework.web.bind.annotation.*;

/**
 * The RequestsController handles all directRequests interaction with the backend.
 */
@RestController
@RequestMapping
public class RequestsController {

    @GetMapping(value = "/categories/{categoryId}/entries/{entryId}/requests", produces = "application/json")
    public DirectRequestProcess getDirectRequestProcess(@PathVariable final long categoryId,
                                                        @PathVariable final long entryId) {
        return mockDirectRequestProcess();
    }

    @PutMapping(value = "/categories/{categoryId}/entries/{entryId}/requests", produces = "application/json")
    public DirectRequestProcess putDirectRequestProcess(@PathVariable final long categoryId,
                                                        @PathVariable final long entryId,
                                                        @RequestBody final DirectRequestProcess directRequestProcess) {
        this.directRequestProcessMockUp = directRequestProcess;
        return directRequestProcessMockUp;
    }

    @PatchMapping(value = "/categories/{categoryId}/entries/{entryId}/requests", produces = "application/json")
    public DirectRequestProcess patchDirectRequestProcess(@PathVariable final long categoryId,
                                                          @PathVariable final long entryId,
                                                          @RequestBody final DirectRequestProcessUpdater updater) {
        mockDirectRequestProcess().setOpenSlots(updater.openSlots());
        return directRequestProcessMockUp;
    }

    @GetMapping(value = "/categories/{categoryId}/entries/{entryId}/requests/{requestId}",
        produces = "application/json")
    public Request getDirectRequest(@PathVariable final long categoryId,
                              @PathVariable final long entryId,
                              @PathVariable final long requestId) {
        return mockDirectRequest();
    }

    @PutMapping(value = "/categories/{categoryId}/entries/{entryId}/requests/{requestId}",
        produces = "application/json")
    public Request putDirectRequest(@PathVariable final long categoryId,
                              @PathVariable final long entryId,
                              @PathVariable final long requestId,
                              @RequestBody final DirectRequest request) {
        this.directRequestMockUp = request;
        return directRequestMockUp;
    }

    @PatchMapping(value = "/categories/{categoryId}/entries/{entryId}/requests/{requestId}",
        produces = "application/json")
    public Request patchDirectRequest(@PathVariable final long categoryId,
                                @PathVariable final long entryId,
                                @PathVariable final long requestId,
                                @RequestBody final RequestUpdater updater) {
        mockDirectRequest().setRequestState(updater.state());
        return this.directRequestMockUp;
    }

    // MOCK DATA //////////////////////////////////////////////////////////////////////////////////////////////

    private DirectRequestProcess directRequestProcessMockUp;
    private Request directRequestMockUp;

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

    private Request mockDirectRequest() {
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
