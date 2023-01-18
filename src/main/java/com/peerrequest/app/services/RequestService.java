package com.peerrequest.app.services;

import java.util.List;

public interface RequestService<F, U> extends CrudService<Request, Request.RequestSelector, F, U> {

    void createDirectRequestProcess(long entryID, List<Long> userIDs, int openSlots);

    void setOpenSlots(int openSlots);

    void claimOpenSlot(long directRequestProcessID, long userID);

    void acceptDirectRequest(long requestID);

    void declineDirectRequest(long requestID);

    void declineBiddingRequest(long requestID);

    DirectRequestProcess getDirectRequestProcess(long entryID);

}
