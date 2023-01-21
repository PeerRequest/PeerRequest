package com.peerrequest.app.model;

import lombok.Getter;
import lombok.Setter;

/**
 * This class represents a direct request process.
 */
public class DirectRequestProcess {

    @Getter
    private final DirectRequestProcessSelector id;
    @Getter
    private final Entry.EntrySelector entrySelector;
    @Getter
    @Setter
    private int openSlots;

    /**
     * Constructor for a direct request process.
     *
     * @param id            id of the direct request process
     * @param entrySelector id of the entry of the direct request process
     */
    public DirectRequestProcess(DirectRequestProcessSelector id,
                                Entry.EntrySelector entrySelector) {
        this.id = id;
        this.entrySelector = entrySelector;
    }

    /**
     * Identifies a DirectRequestProcess.
     *
     * @param id id of the DirectRequestProcess
     */
    public record DirectRequestProcessSelector(long id) {
    }
}