package com.peerrequest.app.model;

/**
 * This record encapsulates the amount of review slots for one entry of a bidding process.
 *
 * @param slots    amount of slots
 * @param entryID  the entry id
 */
public record SlotPair(int slots, Entry.EntrySelector entryID) {
}