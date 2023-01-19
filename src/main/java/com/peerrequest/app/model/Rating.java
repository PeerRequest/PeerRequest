package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a bid on one entry of a bidding process.
 *
 * @author User5 Mildt
 * @version 0.0.1
 */
public class Rating {

    /**
     * Constructor for a rating.
     *
     * @param id             id of the rating
     * @param biddingSlotID  id of the bidding slot of the rating
     * @param reviewerID     id of the user who bid
     */
    public Rating(RatingSelector id, BiddingSlot.BiddingSlotSelector biddingSlotID, User.UserSelector reviewerID) {
        this.id = id;
        this.biddingSlotID = biddingSlotID;
        this.reviewerID = reviewerID;
    }

    @Getter
    private final RatingSelector id;

    @Getter
    private final BiddingSlot.BiddingSlotSelector biddingSlotID;

    @Getter
    private final User.UserSelector reviewerID;

    /**
     * Reviewer of this rating referenced by reviewerID.
     * Might be null.
     */
    @Getter
    @Setter
    private User reviewer;

    @Getter
    @Setter
    private int rating;

    public record RatingSelector(long ratingID) { }

    public record RatingUpdater(int rating) { }
}
