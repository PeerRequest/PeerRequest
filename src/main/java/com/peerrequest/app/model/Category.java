package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;

public class Category {
    @Getter
    @Setter
    CategorySelector id;

    @Getter
    @Setter
    String name;

    @Getter
    @Setter
    CategoryLabel label;

    @Getter
    @Setter
    int year;

    @Getter
    @Setter
    ZonedDateTime deadline;

    @Getter
    @Setter
    float minScore;

    @Getter
    @Setter
    float maxScore;

    @Getter
    @Setter
    float scoreStepSize;

    public enum CategoryLabel {
        INTERNAL, EXTERNAL
    }

    public record CategorySelector(long id) {
    }
}
