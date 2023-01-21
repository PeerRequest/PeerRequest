package com.peerrequest.app.services;

import com.peerrequest.app.model.Entry;

/**
 * Describes the functionality of an EntryService.
 *
 * @param <F> Entry Filter
 * @param <U> Entry Updater
 */
public interface EntryService<F, U> extends CrudService<Entry, Entry.EntrySelector, F, U> {

}