package com.peerrequest.app.model;


import lombok.Getter;
import lombok.Setter;

/**
 * This abstract class represents a request.
 *
 * @author User3 Boban
 * @author User5 Mildt
 * @version 0.0.1
 */
public abstract class Request {

    @Getter
    private final RequestSelector id;
    @Getter
    private final User.UserSelector reviewerSelector;
    @Getter
    @Setter
    private RequestState requestState;

    /**
     * Constructor for a request.
     *
     * @param id               id of the request.
     * @param reviewerSelector id of the requested user.
     */
    public Request(RequestSelector id, User.UserSelector reviewerSelector) {
        this.id = id;
        this.reviewerSelector = reviewerSelector;
        this.requestState = RequestState.PENDING;
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
        PENDING
    }

    /**
     * Identifies a Request.
     *
     * @param id id of the Request
     */
    public record RequestSelector(long id) {
    }
}