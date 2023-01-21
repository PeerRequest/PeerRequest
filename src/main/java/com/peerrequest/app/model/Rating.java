package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a bid on one entry of a bidding process.
 *
 * @author Michael Mildt
 * @version 0.0.1
 */
public class Rating {
    @Getter
    private final RatingSelector id;
    @Getter
    private final BiddingSlot.BiddingSlotSelector biddingSlotSelector;
    @Getter
    private final User.UserSelector reviewerSelector;
    /**
     * Reviewer of this rating referenced by reviewerSelector.
     * Might be null.
     */
    @Getter
    @Setter
    private User reviewer;
    @Getter
    @Setter
    private int rating;

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
    }

    /**
     * Identifies a Rating.
     *
     * @param id id of a Rating
     */
    public record RatingSelector(long id) {
    }
}
