package com.peerrequest.app.api;


import com.peerrequest.app.model.*;
import org.springframework.web.bind.annotation.*;

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
                                                          @RequestBody final DirectRequestProcess
                                                                  .DirectRequestProcessUpdater updater) {
        mockDirectRequestProcess().setOpenSlots(updater.openSlots());
        return directRequestProcessMockUp;
    }

    @GetMapping(value = "/categories/{categoryId}/entries/{entryId}/requests/{requestId}", produces = "application/json")
    public Request getRequest(@PathVariable final long categoryId,
                              @PathVariable final long entryId,
                              @PathVariable final long requestId) {
        return mockDirectRequest();
    }

    @PutMapping(value = "/categories/{categoryId}/entries/{entryId}/requests/{requestId}", produces = "application/json")
    public Request putRequest(@PathVariable final long categoryId,
                              @PathVariable final long entryId,
                              @PathVariable final long requestId,
                              @RequestBody final Request request) {
        this.directRequestMockUp = request;
        return directRequestMockUp;
    }

    @PatchMapping(value = "/categories/{categoryId}/entries/{entryId}/requests/{requestId}", produces = "application/json")
    public Request patchRequest(@PathVariable final long categoryId,
                                @PathVariable final long entryId,
                                @PathVariable final long requestId,
                                @RequestBody final Request.RequestUpdater updater) {
        mockDirectRequest().setRequestState(updater.state());
        return this.directRequestMockUp;
    }

    private DirectRequestProcess directRequestProcessMockUp;
    private Request directRequestMockUp;

    private DirectRequestProcess mockDirectRequestProcess() {

        if (this.directRequestProcessMockUp != null) {
            return directRequestProcessMockUp;
        }

        DirectRequestProcess.DirectRequestProcessSelector directRequestProcessID
                = new DirectRequestProcess.DirectRequestProcessSelector(1);
        Entry.EntrySelector entryID =  new Entry.EntrySelector(1);

        this.directRequestProcessMockUp = new DirectRequestProcess(directRequestProcessID, entryID);
        return directRequestProcessMockUp;
    }

    private Request mockDirectRequest() {
        if (this.directRequestMockUp != null) {
            return this.directRequestMockUp;
        }

        Request.RequestSelector requestID = new Request.RequestSelector(1);
        User.UserSelector reviewerID = new User.UserSelector("1");

        this.directRequestMockUp = new DirectRequest(requestID, reviewerID, mockDirectRequestProcess().getId());
        return this.directRequestMockUp;
    }
}
