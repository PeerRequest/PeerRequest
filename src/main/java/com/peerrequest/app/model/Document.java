package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

public class Document {
    @Getter
    @Setter
    DocumentSelector id;

    public record DocumentSelector(String id) {
    }
}
