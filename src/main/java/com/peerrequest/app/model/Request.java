package com.peerrequest.app.model;


import lombok.Getter;

/**
 * This abstract class represents a request.
 *
 * @author User3 Boban
 * @author User5 Mildt
 * @version 0.0.1
 */
public abstract class Request {

    /**
     * Constructor for a request.
     *
     * @param id          id of the request.
     * @param reviewerID  id of the requested user.
     */
    public Request(RequestSelector id, User.UserSelector reviewerID) {
        this.id = id;
        this.reviewerID = reviewerID;
    }

    @Getter
    private final RequestSelector id;

    @Getter
    private final User.UserSelector reviewerID;

    public record RequestSelector(long requestID) {
    }

    /**
     * Represents the states of a request.
     */
    public enum RequestState {
        /**
         * Request state when a user accepted a request.
         */
        ACCEPTED,
        /**
         * Request state when a user declined a request.
         */
        DECLINED,
        /**
         * Request state when a user neither accepted nor declined a request.
         */
        PENDING;
    }
}