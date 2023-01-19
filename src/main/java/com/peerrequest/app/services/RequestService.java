package com.peerrequest.app.services;

import com.peerrequest.app.model.DirectRequestProcess;
import com.peerrequest.app.model.Entry;
import com.peerrequest.app.model.Request;
import com.peerrequest.app.model.User;

import java.util.List;

public interface RequestService<F, U> extends CrudService<Request, Request.RequestSelector, F, U> {

    void createDirectRequestProcess(Entry.EntrySelector entryID, List<User.UserSelector> userIDs, int openSlots);

    void setOpenSlots(int openSlots);

    void claimOpenSlot(
            DirectRequestProcess.DirectRequestProcessSelector directRequestProcessID, User.UserSelector userID);

    void acceptDirectRequest(Request.RequestSelector requestID);

    void declineDirectRequest(Request.RequestSelector requestID);

    void declineBiddingRequest(Request.RequestSelector requestID);

    DirectRequestProcess getDirectRequestProcess(Entry.EntrySelector entryID);

}
