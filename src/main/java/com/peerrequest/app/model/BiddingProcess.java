package com.peerrequest.app.model;


import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a bidding process of a category.
 *
 * @author User5 Mildt
 * @version 0.0.1
 */
public class BiddingProcess {

    /**
     * Minimum number a user can use for a bid.
     */
    public static final int MIN_RATING = 1;
    /**
     * Maximum number a user can use for a bid.
     */
    public static final int MAX_RATING = 5;
    @Getter
    private final BiddingProcessSelector id;
    @Getter
    private final Category.CategorySelector categorySelector;
    /**
     * Bidding slots of this bidding process.
     * Might be null.
     */
    @Getter
    private final List<BiddingSlot> biddingSlots = new ArrayList<>();
    /**
     * Bidding Requests of this bidding process.
     * Might be null.
     */
    @Getter
    @Setter
    private List<BiddingRequest> biddingRequests = new ArrayList<>();
    @Getter
    @Setter
    private ZonedDateTime deadline;

    /**
     * Constructor for a bidding process.
     *
     * @param id               id of the bidding process
     * @param categorySelector category id of the bidding process
     * @param deadline         deadline of the bidding process
     */
    public BiddingProcess(BiddingProcessSelector id,
                          Category.CategorySelector categorySelector, ZonedDateTime deadline) {
        this.id = id;
        this.categorySelector = categorySelector;
        this.deadline = deadline;
    }

    public List<BiddingSlot> evaluateBidding() {
        // TODO: implement evaluateBidding-algorithm
        throw new RuntimeException("not implemented yet");
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

    /**
     * Identification of a BiddingProcess.
     *
     * @param id id of the BiddingProcess
     */
    public record BiddingProcessSelector(long id) {
    }
}
