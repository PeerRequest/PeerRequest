package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

public class Request {
    @Getter
    @Setter
    RequestSelector id;

    @Getter
    @Setter
    RequestState requestState;

    public record RequestSelector(long id) {
    }

    public enum RequestState {
        ACCEPTED, DECLINED, PENDING;
    }
}