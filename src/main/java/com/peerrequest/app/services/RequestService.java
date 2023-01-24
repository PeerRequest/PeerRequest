package com.peerrequest.app.services;

import com.peerrequest.app.model.DirectRequestProcess;
import com.peerrequest.app.model.Entry;
import com.peerrequest.app.model.Request;
import com.peerrequest.app.model.User;
import java.util.List;

/**
 * Describes the functionality of a RequestService.
 *
 * @param <F> Request Filter
 * @param <U> Request Updater
 */
public interface RequestService<F, U> extends CrudService<Request, Request.RequestSelector, F, U> {

    void createDirectRequestProcess(Entry.EntrySelector entrySelector, List<User.UserSelector> userSelectors,
                                    int openSlots);

    void setOpenSlots(int openSlots);

    void claimOpenSlot(
        DirectRequestProcess.DirectRequestProcessSelector directRequestProcessSelector, User.UserSelector userSelector);

    void acceptDirectRequest(Request.RequestSelector requestSelector);

    void declineDirectRequest(Request.RequestSelector requestSelector);

    void declineBiddingRequest(Request.RequestSelector requestSelector);

    DirectRequestProcess getDirectRequestProcess(Entry.EntrySelector entrySelector);

}
