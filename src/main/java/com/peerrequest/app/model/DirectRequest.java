package com.peerrequest.app.model;


import lombok.Getter;

/**
 * This class represents a direct request.
 *
 * @author Michael Mildt
 * @version 0.0.1
 */
public class DirectRequest extends Request {

    @Getter
    private final DirectRequestProcess.DirectRequestProcessSelector directRequestProcessSelector;

    /**
     * Constructor for a direct request.
     *
     * @param id                           id of the direct request
     * @param reviewerSelector             id of the requested user
     * @param directRequestProcessSelector id of direct request process of the direct request
     */
    public DirectRequest(RequestSelector id, User.UserSelector reviewerSelector,
                         DirectRequestProcess.DirectRequestProcessSelector directRequestProcessSelector) {
        super(id, reviewerSelector);
        this.directRequestProcessSelector = directRequestProcessSelector;
    }
}
