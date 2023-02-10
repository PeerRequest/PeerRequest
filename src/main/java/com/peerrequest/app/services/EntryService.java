package com.peerrequest.app.services;

import com.peerrequest.app.data.Entry;
import org.springframework.data.domain.Page;

/**
 * Describes the functionality of an EntryService.
 */
public interface EntryService
    extends CrudService<Entry, Long, Entry.Dto> {
    Page<Entry> listByResearcher(int page, int maxCount, String researcherId);
}
