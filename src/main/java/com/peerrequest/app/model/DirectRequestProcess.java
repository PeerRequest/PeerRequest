package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

public class DirectRequestProcess {
    @Getter
    @Setter
    DirectRequestProcessSelector id;

    @Getter
    @Setter
    int openSlots;

    public record DirectRequestProcessSelector(long id) {
    }
}