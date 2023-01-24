package com.peerrequest.app.model;

/**
 * This record encapsulates a bid of a bidding slot of a bidding process.
 *
 * @param bid         the bid between MIN_RATING and MAX_RATING of a bidding process
 * @param biddingSlot the bidding slot id
 */
public record BiddingPair(int bid, BiddingSlot.BiddingSlotSelector biddingSlot) {
}
