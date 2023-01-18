package com.peerrequest.app.model;


import lombok.Getter;
import lombok.Setter;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * This class represents a bidding process of a category.
 *
 * @author Michael Mildt
 * @version 0.0.1
 */
public class BiddingProcess {

    /**
     * Constructor for a bidding process.
     *
     * @param id          id of the bidding process
     * @param categoryID  category id of the bidding process
     * @param deadline    deadline of the bidding process
     */
    public BiddingProcess(BiddingProcessSelector id,
                          Category.CategorySelector categoryID, ZonedDateTime deadline) {
        this.id = id;
        this.categoryID = categoryID;
        this.deadline = deadline;
    }


    @Getter
    private final BiddingProcessSelector id;

    @Getter
    private final Category.CategorySelector categoryID;

    /**
     * Bidding Requests of this bidding process.
     * Might be null.
     */
    @Getter
    @Setter
    private List<BiddingRequest> biddingRequests = new ArrayList<>();

    /**
     * Bidding slots of this bidding process.
     * Might be null.
     */
    @Getter
    private final List<BiddingSlot> biddingSlots = new ArrayList<>();

    @Getter
    private final ZonedDateTime deadline;

    /**
     * Minimum number a user can use for a bid.
     */
    public static final int MIN_RATING = 1;

    /**
     * Maximum number a user can use for a bid.
     */
    public static final int MAX_RATING = 5;

    public record BiddingProcessSelector(long biddingProcessID) {
    }


    public List<BiddingSlot> evaluateBidding() {
        // TODO: implement evaluateBidding-algorithm
        return new ArrayList<BiddingSlot>();
    }

    /**
     * Adds a list of bidding slots to the bidding slots of this bidding process.
     *
     * @param slots slots to be added
     */
    public void addBiddingSlots(final List<BiddingSlot> slots) {
        this.biddingSlots.addAll(slots);
    }

    /**
     * Removes all bidding slots of this bidding process.
     */
    public void removeAllBiddingSlots() {
        this.biddingSlots.clear();
    }

    /**
     * State of a bidding process.
     */
    public enum BiddingRequestStates {
        /**
         * If a bidding process is OPEN, selected reviewers
         * can bid on the entries of the category of the bidding process.
         */
        OPEN,
        /**
         * If a bidding process is CLOSED, reviewers can not bid anymore.
         * The researcher has to confirm their/the systems allocation of reviewers to entries.
         */
        CLOSED
    }

}
