package com.peerrequest.app.model;


import lombok.Getter;

/**
 * This class represents a direct request.
 *
 * @author Michael Mildt
 * @version 0.0.1
 */
public class DirectRequest extends Request {

    /**
     * Constructor for a direct request
     *
     * @param id                      id of the direct request
     * @param reviewerID              id of the requested user
     * @param directRequestProcessID  id of direct request process of the direct request
     */
    public DirectRequest(RequestSelector id, User.UserSelector reviewerID,
                         DirectRequestProcess.DirectRequestProcessSelector directRequestProcessID) {
        super(id, reviewerID);
        this.directRequestProcessID = directRequestProcessID;
    }
    @Getter
    private final DirectRequestProcess.DirectRequestProcessSelector directRequestProcessID;
}
