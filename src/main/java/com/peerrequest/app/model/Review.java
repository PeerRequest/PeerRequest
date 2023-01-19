package com.peerrequest.app.model;


import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a review.
 *
 * @author Luder Halpick
 * @author Michael Mildt
 * @version 0.0.1
 */
public class Review {

    /**
     * Constructor for a review.
     *
     * @param id          id of the review
     * @param reviewerID  id of the reviewer
     */
    public Review(ReviewSelector id, User.UserSelector reviewerID) {
        this.id = id;
        this.reviewerID = reviewerID;
    }
    @Getter
    private final ReviewSelector id;

    @Getter
    private final User.UserSelector reviewerID;

    @Getter
    private Document.DocumentSelector reviewDocumentID;

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

    public record ReviewSelector(Entry.EntrySelector entrySelector, long reviewID) {
    }


    public record ReviewUpdater(Document reviewDocument, ConfidenceLevel confidenceLevel, String summary,
                                String mainWeakness, String mainStrengths, String questionsForAuthors,
                                String answersFromAuthors, String otherComments, float score) {

    }
}
