package com.peerrequest.app.model;

import lombok.Getter;

/**
 * This class represents a bidding request.
 */
public class BiddingRequest extends Request {

    @Getter
    private final BiddingProcess.BiddingProcessSelector biddingProcess;

    /**
     * Constructor for a bidding request.
     *
     * @param id             id of the bidding request
     * @param reviewer       id of the reviewer
     * @param biddingProcess id of the bidding process of the bidding request
     */
    public BiddingRequest(RequestSelector id, User.UserSelector reviewer,
                          BiddingProcess.BiddingProcessSelector biddingProcess) {
        super(id, reviewer);
        this.biddingProcess = biddingProcess;
    }
}
