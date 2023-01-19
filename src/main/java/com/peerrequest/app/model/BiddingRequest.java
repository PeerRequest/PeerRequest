package com.peerrequest.app.model;

import lombok.Getter;

/**
 * This class represents a bidding request.
 *
 * @author Michael Mildt
 * @version 0.0.1
 */
public class BiddingRequest extends Request {

    /**
     * Constructor for a bidding request.
     *
     * @param id                id of the bidding request
     * @param reviewerID        id of the reviewer
     * @param biddingProcessID  id of the bidding process of the bidding request
     */
    public BiddingRequest(RequestSelector id, User.UserSelector reviewerID,
                          BiddingProcess.BiddingProcessSelector biddingProcessID) {
        super(id, reviewerID);
        this.biddingProcessID = biddingProcessID;
    }

    @Getter
    private final BiddingProcess.BiddingProcessSelector biddingProcessID;
}
