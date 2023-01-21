package com.peerrequest.app.services;

import com.peerrequest.app.model.BiddingPair;
import com.peerrequest.app.model.BiddingProcess;
import com.peerrequest.app.model.BiddingSlot;
import com.peerrequest.app.model.User;
import java.util.List;

/**
 * Specifies functionality of BiddingProcessService.
 *
 * @param <F> BiddingProcess Filter
 * @param <U> BiddingProcess Updater
 */
public interface BiddingProcessService<F, U>
    extends CrudService<BiddingProcess, BiddingProcess.BiddingProcessSelector, F, U> {

    int refreshBiddingProcess(BiddingProcess.BiddingProcessSelector selector);

    void allocateBiddingProcess(BiddingProcess.BiddingProcessSelector selector,
                                List<BiddingSlot> biddingSlots);

    void bidBiddingProcess(User.UserSelector user, List<BiddingPair> bidding);

    void evaluateBiddingProcess(BiddingProcess.BiddingProcessSelector selector);
}
