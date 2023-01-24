package com.peerrequest.app.model;

import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a review.
 */
public class Review {

    @Getter
    private final ReviewSelector id;
    @Getter
    private final User.UserSelector reviewerSelector;
    @Getter
    private final Entry.EntrySelector entrySelector;
    @Getter
    @Setter
    private Document.DocumentSelector reviewDocumentSelector;
    @Getter
    @Setter
    private Document reviewDocument;
    @Getter
    @Setter
    private ConfidenceLevel confidenceLevel;
    @Getter
    @Setter
    private String summary;
    @Getter
    @Setter
    private String mainWeakness;
    @Getter
    @Setter
    private String mainStrengths;
    @Getter
    @Setter
    private String questionsForAuthors;
    @Getter
    @Setter
    private String answersFromAuthors;
    @Getter
    @Setter
    private String otherComments;
    @Getter
    @Setter
    private float score;
    @Getter
    @Setter
    private List<Message> messageBoard = new ArrayList<>();

    /**
     * Constructor for a review.
     *
     * @param id               id of the review
     * @param reviewerSelector id of the reviewer
     */
    public Review(ReviewSelector id, User.UserSelector reviewerSelector, Entry.EntrySelector entrySelector) {
        this.id = id;
        this.reviewerSelector = reviewerSelector;
        this.entrySelector = entrySelector;
    }


    /**
     * Represents the confidence level of a reviewer on the subject of the entry.
     */
    public enum ConfidenceLevel {
        /**
         * Confidence level when the reviewer is not confident in the subject.
         */
        LOW,
        /**
         * Confidence level when the reviewer is semi confident in the subject.
         */
        MEDIUM,
        /**
         * Confidence level when the reviewer is confident in the subject.
         */
        HIGH
    }

    /**
     * Identifies a Review.
     *
     * @param entrySelector specifies the entry this review belongs to. (Might be null)
     * @param id            id of the Review
     */
    public record ReviewSelector(Entry.EntrySelector entrySelector, long id) {
    }
}
