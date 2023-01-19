package com.peerrequest.app.services;

import com.peerrequest.app.model.User;

public interface BiddingProcessService<F, U>
        extends CrudService<BiddingProcess, BiddingProcess.BiddingProcessSelector, F, U> {

    int refreshBiddingProcess(BiddingProcess.BiddingProcessSelector biddingProcessID);

    void allocateBiddingProcess(BiddingProcess.BiddingProcessSelector biddingProcessID, List<BiddingSlot> biddingSlots);

    void bidBiddingProcess(User.UserSelector userID, List<BiddingPair> bidding);

    void evaluateBiddingProcess(BiddingProcess.BiddingProcessSelector biddingProcessID);
}
