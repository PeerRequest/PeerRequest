package com.peerrequest.app.model;


import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;


/**
 * This class represents a category.
 *
 * @author User1 Halpick
 * @author User5 Mildt
 * @version 0.0.1
 */
public class Category {

    /**
     * Constructor of a category.
     *
     * @param id             id of the category
     * @param researcherID   id of the researcher of the category
     * @param name           name of the category
     * @param label          label of the category
     * @param year           year of the category
     * @param deadline       deadline of the category
     * @param minScore       minimum score a user can rate an entry of this category in a review
     * @param maxScore       maximum score a user can rate an entry of this category in a review
     * @param scoreStepSize  step size of the score scala of in a review of an entry from this category
     */
    public Category(CategorySelector id, User.UserSelector researcherID, String name, CategoryLabel label,
                    int year, ZonedDateTime deadline, float minScore, float maxScore, float scoreStepSize) {
        this.id = id;
        this.researcherID = researcherID;
        this.name = name;
        this.label = label;
        this.year = year;
        this.deadline = deadline;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.scoreStepSize = scoreStepSize;

    }
    @Getter
    private final CategorySelector id;

    @Getter
    private final User.UserSelector researcherID;

    @Getter
    @Setter
    private final String name;

    @Getter
    private final CategoryLabel label;

    @Getter
    @Setter
    private final int year;

    @Getter
    private final ZonedDateTime deadline;

    /**
     * Minimum score a user can rate an entry of this category in a review.
     */
    @Getter
    private final float minScore;

    /**
     * Maximum score a user can rate an entry of this category in a review
     */
    @Getter
    private final float maxScore;

    @Getter
    @Setter
    private float scoreStepSize;

    /**
     * Label of a category.
     */
    public enum CategoryLabel {
        /**
         * If a category is INTERNAL, no bidding process can be started. Every user can add entries to this category.
         */
        INTERNAL,

        /**
         * If a category is EXTERNAL, only the researcher who created the process can add entries to this category.
         */
        EXTERNAL
    }

    public record CategorySelector(long categoryID) { }

    public record CategoryUpdater(String name, CategoryLabel label, int year,
                                  ZonedDateTime deadline, float minScore, float maxScore, float scoreStepSize) { }

}