package com.peerrequest.app.api;

import com.peerrequest.app.model.*;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/**
 * The BiddingProcessesController handles all bidding process and bidding requests interactions with the backend.
 */
@RestController
@RequestMapping
public class BiddingProcessesController {

    // BiddingProcess

    @PutMapping(value = "/categories/{categoryId}/biddingProcess", produces = "application/json")
    public BiddingProcess putBiddingProcess(@PathVariable final long categoryId,
                                            @RequestBody final BiddingProcess biddingProcess) {
        this.mockUpBiddingProcess = biddingProcess;
        return this.mockUpBiddingProcess;
    }

    @PatchMapping(value = "/categories/{categoryId}/biddingProcess", produces = "application/json")
    public BiddingProcess patchBiddingProcess(@PathVariable final long categoryId,
                                              @RequestBody final BiddingProcessUpdater updater) {
        mockBiddingProcess().setDeadline((updater.deadline()));
        return mockUpBiddingProcess;
    }

    @GetMapping(value = "/categories/{categoryId}/biddingProcess", produces = "application/json")
    public BiddingProcess getBiddingProcess(@PathVariable final long categoryId) {
        return mockBiddingProcess();
    }

    @DeleteMapping(value = "/categories/{categoryId}/biddingProcess", produces = "application/json")
    public void deleteBiddingProcess(@PathVariable final long categoryId) {
        mockUpBiddingProcess = null;
    }

    // BiddingRequest

    @GetMapping(value = "/categories/{categoryId}/biddingProcess/requests/{requestId}", produces = "application/json")
    public BiddingRequest getBiddingRequest(@PathVariable final long categoryId,
                                            @PathVariable final long requestId) {
        return mockRequest();
    }

    @PutMapping(value = "/categories/{categoryId}/biddingProcess/requests", produces = "application/json")
    public BiddingRequest putBiddingRequest(@PathVariable final long categoryId,
                                            @RequestBody final BiddingRequest request) {
        this.mockUpRequest = request;
        return mockUpRequest;
    }

    @PatchMapping(value = "/categories/{categoryId}/biddingProcess/requests/{requestId}", produces = "application/json")
    public BiddingRequest patchBiddingRequest(@PathVariable final long categoryId,
                                              @PathVariable final long requestId,
                                              @RequestBody final RequestUpdater updater) {
        mockRequest().setRequestState(updater.state());
        return mockUpRequest;
    }

    // BiddingProcessService methods

    @PostMapping(value = "/categories/{categoryId}/biddingProcess/refresh", produces = "application/json")
    public BiddingProcess refreshBiddingProcess(@PathVariable final long categoryId) {
        //todo: add BiddingProcessService.refreshBiddingProcess(...)
        throw new RuntimeException("not implemented");
    }

    @PostMapping(value = "/categories/{categoryId}/biddingProcess/allocate", produces = "application/json")
    public BiddingProcess allocateBiddingProcess(@PathVariable final long categoryId,
                                                 @RequestBody final List<BiddingSlot> allocation) {
        //todo: add BiddingProcessService.allocateBiddingProcess(...)
        throw new RuntimeException("not implemented");
    }

    @PostMapping(value = "/categories/{categoryId}/biddingProcess/bid", produces = "application/json")
    public BiddingProcess bidBiddingProcess(@PathVariable final long categoryId,
                                            @RequestBody final String userId,
                                            @RequestBody final List<BiddingPair> bids) {
        //todo: add BiddingProcessService.bidBiddingProcess(...)
        throw new RuntimeException("not implemented");
    }

    @PostMapping(value = "/categories/{categoryId}/biddingProcess/evaluate", produces = "application/json")
    public BiddingProcess evaluateBiddingProcess(@PathVariable final long categoryId) {
        //todo: add BiddingProcessService.evaluateBiddingProcess(...)
        throw new RuntimeException("not implemented");
    }



    // MOCKUPS ///////////////////////////////////////////////////////////////////////////////////////////////////

    private BiddingProcess mockUpBiddingProcess;
    private BiddingRequest mockUpRequest;

    private BiddingProcess mockBiddingProcess() {

        if (mockUpBiddingProcess != null) {
            return mockUpBiddingProcess;
        }

        ZonedDateTime deadline = ZonedDateTime.now();
        BiddingProcess.BiddingProcessSelector biddingProcessId = new BiddingProcess.BiddingProcessSelector(1);
        Category.CategorySelector categoryId = new Category.CategorySelector(1);

        this.mockUpBiddingProcess = new BiddingProcess(biddingProcessId, categoryId, deadline);

        return mockUpBiddingProcess;
    }

    private BiddingRequest mockRequest() {

        if (mockUpRequest != null) {
            return mockUpRequest;
        }

        Request.RequestSelector requestId = new Request.RequestSelector(1);
        User.UserSelector reviewerId =  new User.UserSelector("1");
        BiddingProcess.BiddingProcessSelector biddingProcessId = new BiddingProcess.BiddingProcessSelector(1);

        this.mockUpRequest = new BiddingRequest(requestId, reviewerId, biddingProcessId);

        return mockUpRequest;
    }

    private record BiddingProcessUpdater(ZonedDateTime deadline) {}

    private record RequestUpdater(Request.RequestState state) {}

}
