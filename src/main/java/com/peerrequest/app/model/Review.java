package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

public class Review {
    @Getter
    @Setter
    ReviewSelector id;

    @Getter
    @Setter
    Document reviewFile;

    @Getter
    @Setter
    float score;

    @Getter
    @Setter
    ConfidenceLevel confidenceLevel;

    @Getter
    @Setter
    String summary;

    @Getter
    @Setter
    String mainWeakness;

    @Getter
    @Setter
    String mainStrengths;

    @Getter
    @Setter
    String questionsForAuthors;

    @Getter
    @Setter
    String answersFromAuthors;

    @Getter
    @Setter
    String otherComments;

    public enum ConfidenceLevel {
        LOW,
        MEDIUM,
        HIGH
    }

    public record ReviewSelector(Entry.EntrySelector entrySelector, long id) {
    }
}
