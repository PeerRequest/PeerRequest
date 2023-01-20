package com.peerrequest.app.api;

import com.peerrequest.app.model.*;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping
public class BiddingProcessesController {

    @PutMapping(value = "/categories/{categoryId}/biddingProcess", produces = "application/json")
    public BiddingProcess putBiddingProcess(@PathVariable final long categoryId,
                                            @RequestBody final BiddingProcess biddingProcess) {
        this.mockUp = biddingProcess;
        return this.mockUp;
    }

    @PatchMapping(value = "/categories/{categoryId}/biddingProcess", produces = "application/json")
    public BiddingProcess patchBiddingProcess(@PathVariable final long categoryId,
                                              @RequestBody final BiddingProcess.BiddingProcessUpdater updater) {
        mockBiddingProcess().setDeadline((updater.deadline()));
        return mockUp;
    }

    @GetMapping(value = "/categories/{categoryId}/biddingProcess", produces = "application/json")
    public BiddingProcess getBiddingProcess(@PathVariable final long categoryId) {
        return mockBiddingProcess();
    }

    @DeleteMapping(value = "/categories/{categoryId}/biddingProcess", produces = "application/json")
    public void deleteBiddingProcess(@PathVariable final long categoryId) {

    }

    private BiddingProcess mockUp;

    private BiddingProcess mockBiddingProcess() {

        if (mockUp != null) {
            return mockUp;
        }

        ZonedDateTime deadline = ZonedDateTime.now();
        BiddingProcess.BiddingProcessSelector biddingProcessID = new BiddingProcess.BiddingProcessSelector(1);
        Category.CategorySelector categoryID = new Category.CategorySelector(1);

        this.mockUp = new BiddingProcess(biddingProcessID, categoryID, deadline);

        return mockUp;
    }

}
