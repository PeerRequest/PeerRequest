package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

public class Request {
    @Getter
    @Setter
    RequestSelector id;

    public record RequestSelector(long id) {
    }

}