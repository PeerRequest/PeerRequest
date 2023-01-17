package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

public class User {
    @Getter
    @Setter
    UserSelector id;

    @Getter
    @Setter
    String name;

    @Getter
    @Setter
    String email;

    public record UserSelector(String id) {
    }
}
