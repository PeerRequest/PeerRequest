package com.peerrequest.app.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.time.ZonedDateTime;
import lombok.Getter;


/**
 * This class represents a category.
 */
@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonProperty("id")
    @Getter
    private Long id;

    @Getter
    @Column(nullable = false)
    private String researcherId;
    @Getter
    @Column(nullable = false)
    private String title;
    @Getter
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryLabel label;
    @Getter
    @Column(nullable = false)
    private int year;
    @Getter
    private ZonedDateTime deadline;
    /**
     * Minimum score a user can rate an entry of this category in a review.
     */
    @Getter
    @Column(nullable = false)
    private float minScore;
    /**
     * Maximum score a user can rate an entry of this category in a review.
     */
    @Getter
    @Column(nullable = false)
    private float maxScore;
    @Getter
    @Column(nullable = false)
    private float scoreStepSize;

    public Category() {

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
