package com.peerrequest.app.model;


import java.time.ZonedDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;


/**
 * This class represents a category.
 */
public class Category {

    @Getter
    private final CategorySelector id;
    @Getter
    private final User.UserSelector researcherSelector;
    @Getter
    private final String title;
    @Getter
    private final CategoryLabel label;
    @Getter
    private final int year;
    @Getter
    private final ZonedDateTime deadline;
    /**
     * Minimum score a user can rate an entry of this category in a review.
     */
    @Getter
    private final float minScore;
    /**
     * Maximum score a user can rate an entry of this category in a review.
     */
    @Getter
    private final float maxScore;
    @Getter
    @Setter
    private float scoreStepSize;

    /**
     * Constructor of a category.
     *
     * @param id                 id of the category
     * @param researcherSelector id of the researcher of the category
     * @param title               name of the category
     * @param label              label of the category
     * @param year               year of the category
     * @param deadline           deadline of the category
     * @param minScore           minimum score a user can rate an entry of this category in a review
     * @param maxScore           maximum score a user can rate an entry of this category in a review
     * @param scoreStepSize      step size of the score scala of in a review of an entry from this category
     */
    public Category(@JsonProperty("id") CategorySelector id,
                    @JsonProperty("researcher_id") User.UserSelector researcherSelector,
                    @JsonProperty("title") String title,
                    @JsonProperty("category_type") CategoryLabel label,
                    @JsonProperty("year") int year,
                    @JsonProperty("deadline") ZonedDateTime deadline,
                    @JsonProperty("minScore") float minScore,
                    @JsonProperty("maxScore") float maxScore,
                    @JsonProperty("scoreStepSize") float scoreStepSize) {
        this.id = id;
        this.researcherSelector = researcherSelector;
        this.title = title;
        this.label = label;
        this.year = year;
        this.deadline = deadline;
        this.minScore = minScore;
        this.maxScore = maxScore;
        this.scoreStepSize = scoreStepSize;
    }

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

    /**
     * Identifies a Category.
     *
     * @param id id of the category
     */
    public record CategorySelector(long id) {
    }
}