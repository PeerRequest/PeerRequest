package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a bid on one entry of a bidding process.
 */
public class Rating implements Comparable<Rating> {
    @Getter
    private final RatingSelector id;
    @Getter
    private final BiddingSlot.BiddingSlotSelector biddingSlotSelector;
    @Getter
    private final User.UserSelector reviewerSelector;

    @Getter
    @Setter
    private int rating;

    @Getter
    @Setter
    private RatingState ratingState;

    /**
     * Constructor for a rating.
     *
     * @param id                  id of the rating
     * @param biddingSlotSelector id of the bidding slot of the rating
     * @param reviewerSelector    id of the user who bid
     */
    public Rating(RatingSelector id, BiddingSlot.BiddingSlotSelector biddingSlotSelector,
                  User.UserSelector reviewerSelector) {
        this.id = id;
        this.biddingSlotSelector = biddingSlotSelector;
        this.reviewerSelector = reviewerSelector;
        this.ratingState = RatingState.NOT_EVALUATED;
    }

    @Override
    public int compareTo(Rating r) {
        return Integer.compare(this.rating, r.getRating());
    }

    /**
     * Represents the state of a Rating.
     */
    public enum RatingState {
        /**
         * Rating state when the reviewer of the rating was allocated to the bidding slot.
         */
        ALLOCATED,
        /**
         * Rating state when the reviewer of the rating was not allocated to the bidding slot.
         */
        NOT_ALLOCATED,
        /**
         * Rating state when the rating was not evaluated by the bidding process yet.
         */
        NOT_EVALUATED
    }

    /**
     * Identifies a Rating.
     *
     * @param id id of a Rating
     */
    public record RatingSelector(long id) {
    }
}
