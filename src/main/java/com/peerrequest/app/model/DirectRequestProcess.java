package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a direct request process.
 *
 * @author User3 Boban
 * @author User5 Mildt
 * @version 0.0.1
 */
public class DirectRequestProcess {

    /**
     * Constructor for a direct request process.
     *
     * @param id       id of the direct request process
     * @param entryID  id of the entry of the direct request process
     */
    public DirectRequestProcess(DirectRequestProcessSelector id, Entry.EntrySelector entryID) {
        this.id = id;
        this.entryID = entryID;
    }

    @Getter
    @Setter
    private final DirectRequestProcessSelector id;

    @Getter
    @Setter
    private final Entry.EntrySelector entryID;

    @Getter
    @Setter
    private int openSlots;

    public record DirectRequestProcessSelector(long directRequestProcessID) {
    }
}