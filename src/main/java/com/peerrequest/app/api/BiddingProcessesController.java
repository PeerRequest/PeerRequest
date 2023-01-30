package com.peerrequest.app.api;

import com.peerrequest.app.model.*;
import java.time.ZonedDateTime;
import java.util.List;
import org.springframework.web.bind.annotation.*;

/**
 * The BiddingProcessesController handles all bidding process and bidding requests interactions with the backend.
 */
@RestController
@ApiControllerPrefix
public class BiddingProcessesController {

    // BiddingProcess

    /**
     * Creates a BiddingProcess.
     *
     * @param categoryId category of the biddingProcess
     * @param biddingProcess biddingProcess to add
     *
     * @return added biddingProcess
     */
    @PutMapping(value = "/categories/{categoryId}/biddingProcess", produces = "application/json")
    public BiddingProcess putBiddingProcess(@PathVariable final long categoryId,
                                            @RequestBody final BiddingProcess biddingProcess) {
        this.mockUpBiddingProcess = biddingProcess;
        return this.mockUpBiddingProcess;
    }

    /**
     * Patches a biddingProcess.
     *
     * @param categoryId category of the biddingProcess
     * @param updater updater containing a new ZonedDateTime 'deadline'
     *
     * @return returns updated biddingProcess
     */
    @PatchMapping(value = "/categories/{categoryId}/biddingProcess", produces = "application/json")
    public BiddingProcess patchBiddingProcess(@PathVariable final long categoryId,
                                              @RequestBody final BiddingProcessUpdater updater) {
        mockBiddingProcess().setDeadline((updater.deadline()));
        return mockUpBiddingProcess;
    }

    /**
     * Gets a biddingProcess.
     *
     * @param categoryId category of the biddingProcess
     *
     * @return biddingProcess of the category
     */
    @GetMapping(value = "/categories/{categoryId}/biddingProcess", produces = "application/json")
    public BiddingProcess getBiddingProcess(@PathVariable final long categoryId) {
        return mockBiddingProcess();
    }

    /**
     * Deletes a BiddingProcess.
     *
     * @param categoryId category of the biddingRequest
     */
    @DeleteMapping(value = "/categories/{categoryId}/biddingProcess", produces = "application/json")
    public void deleteBiddingProcess(@PathVariable final long categoryId) {
        mockUpBiddingProcess = null;
    }

    // BiddingRequest

    /**
     * Gets a biddingRequest.
     *
     * @param categoryId category of the biddingProcess
     * @param requestId biddingRequest for the biddingProcess
     *
     * @return the biddingRequest with requestId
     */
    @GetMapping(value = "/categories/{categoryId}/biddingProcess/requests/{requestId}", produces = "application/json")
    public BiddingRequest getBiddingRequest(@PathVariable final long categoryId,
                                            @PathVariable final long requestId) {
        return mockRequest();
    }

    /**
     * Creates a biddingRequest.
     *
     * @param categoryId category of the biddingProcess
     * @param request biddingRequest to add
     *
     * @return added biddingRequest
     */
    @PutMapping(value = "/categories/{categoryId}/biddingProcess/requests", produces = "application/json")
    public BiddingRequest putBiddingRequest(@PathVariable final long categoryId,
                                            @RequestBody final BiddingRequest request) {
        this.mockUpRequest = request;
        return mockUpRequest;
    }

    /**
     * Patches a biddingRequest.
     *
     * @param categoryId category of the biddingProcess
     * @param requestId request for the bidding Process
     * @param updater updater containing Request.State ACCEPTED or DECLINED
     *
     * @return the updated biddingRequest
     */
    @PatchMapping(value = "/categories/{categoryId}/biddingProcess/requests/{requestId}", produces = "application/json")
    public BiddingRequest patchBiddingRequest(@PathVariable final long categoryId,
                                              @PathVariable final long requestId,
                                              @RequestBody final RequestUpdater updater) {
        mockRequest().setRequestState(updater.state());
        return mockUpRequest;
    }

    // BiddingProcessService methods

    /**
     * Refreshes a biddingProcess.
     *
     * @param categoryId category of the bidding Process
     *
     * @return the refreshed biddingProcess
     */
    @PostMapping(value = "/categories/{categoryId}/biddingProcess/refresh", produces = "application/json")
    public BiddingProcess refreshBiddingProcess(@PathVariable final long categoryId) {
        //todo: add BiddingProcessService.refreshBiddingProcess(...)
        throw new RuntimeException("not implemented");
    }

    /**
     * Assigns an allocation of a biddingProcess and deletes the biddingProcess afterwards.
     *
     * @param categoryId category of the biddingProcess
     * @param allocation allocation of reviewers to entries represented in biddingSlots
     */
    @DeleteMapping(value = "/categories/{categoryId}/biddingProcess/allocate", produces = "application/json")
    public void allocateBiddingProcess(@PathVariable final long categoryId,
                                                 @RequestBody final List<BiddingSlot> allocation) {
        //todo: add BiddingProcessService.allocateBiddingProcess(...)
        throw new RuntimeException("not implemented");
    }

    /**
     * Adds the bids of a reviewer to a biddingProcess.
     *
     * @param categoryId category of the biddingProcess.
     * @param userId user who bid
     * @param bids bids of the user
     *
     * @return BiddingProcess
     */
    @PostMapping(value = "/categories/{categoryId}/biddingProcess/bid", produces = "application/json")
    public BiddingProcess bidBiddingProcess(@PathVariable final long categoryId,
                                            @RequestBody final String userId,
                                            @RequestBody final List<BiddingPair> bids) {
        //todo: add BiddingProcessService.bidBiddingProcess(...)
        throw new RuntimeException("not implemented");
    }

    /**
     * Evaluates a biddingProcess after the researcher ended the biddingProcess before the deadline.
     *
     * @param categoryId category of the biddingProcess
     *
     * @return the evaluated biddingProcess
     */
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
