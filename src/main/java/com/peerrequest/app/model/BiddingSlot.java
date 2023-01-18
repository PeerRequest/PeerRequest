package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * This class represents a bidding slot.
 *
 * @author Michael Mildt
 * @version 0.0.1
 */
public class BiddingSlot {

    /**
     * Constructor for a bidding slot.
     *
     * @param id                id of the bidding slot
     * @param biddingProcessID  id of the bidding process of the bidding slot
     * @param entryID           id of the entry of the bidding slot
     */
    public BiddingSlot(BiddingSlotSelector id,
                       BiddingProcess.BiddingProcessSelector biddingProcessID, Entry.EntrySelector entryID) {
        this.id = id;
        this.biddingProcessID = biddingProcessID;
        this.entryID = entryID;
    }

    @Getter
    private final BiddingSlotSelector id;

    @Getter
    private final BiddingProcess.BiddingProcessSelector biddingProcessID;

    @Getter
    @Setter
    private final Entry.EntrySelector entryID;

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

    public record BiddingSlotSelector (long biddingSlotID) {}
}
