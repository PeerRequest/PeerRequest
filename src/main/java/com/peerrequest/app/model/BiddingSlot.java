package com.peerrequest.app.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a bidding slot.
 *
 * @author Michael Mildt
 * @version 0.0.1
 */
public class BiddingSlot {

    @Getter
    private final BiddingSlotSelector id;
    @Getter
    private final BiddingProcess.BiddingProcessSelector biddingProcessSelector;
    @Getter
    private final Entry.EntrySelector entrySelector;
    /**
     * Ratings of a bidding slot.
     * May be null.
     */
    @Getter
    @Setter
    private List<Rating> ratings;
    @Getter
    @Setter
    private int reviewSlots = 1;

    /**
     * Constructor for a bidding slot.
     *
     * @param id                     id of the bidding slot
     * @param biddingProcessSelector id of the bidding process of the bidding slot
     * @param entrySelector          id of the entry of the bidding slot
     */
    public BiddingSlot(BiddingSlotSelector id,
                       BiddingProcess.BiddingProcessSelector biddingProcessSelector,
                       Entry.EntrySelector entrySelector) {
        this.id = id;
        this.biddingProcessSelector = biddingProcessSelector;
        this.entrySelector = entrySelector;
    }

    public void addRating(Rating rating) {
        ratings.add(rating);
    }

    public void removeRating(Rating user) {
        ratings.remove(user);
    }

    /**
     * Identifies a BiddingSlot.
     *
     * @param id id of the BiddingSlot
     */
    public record BiddingSlotSelector(long id) {
    }
}
