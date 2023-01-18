package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

public class DirectRequestProcess {
    @Getter
    @Setter
    DirectRequestProcessSelector id;

    public record DirectRequestProcessSelector(long id) {
    }
}