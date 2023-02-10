package com.peerrequest.app.services;

import com.peerrequest.app.data.Entry;
import com.peerrequest.app.data.repos.EntryRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of EntryService.
 */
@Service
public class EntryServiceImpl implements EntryService {

    @Autowired
    private EntryRepository repo;

    @Override
    public Entry create(Entry.Dto newEntity) {
        return repo.save(Entry.fromDto(newEntity));
    }

    @Override
    public Page<Entry> list(int page, int maxCount, Entry.Dto filter) {
        if (filter.categoryId().isPresent()) {
            return repo.findByCategoryId(filter.categoryId().get(), PageRequest.of(page, maxCount));
        } else {
            return repo.findAll(Pageable.ofSize(maxCount).withPage(page));
        }
    }

    @Override
    public Optional<Entry> get(Long cursor) {
        return repo.findById(cursor);
    }

    @Override
    public Optional<Entry> update(Long cursor, Entry.Dto newProps) {
        var optional = repo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var entry = optional.get();

        if (newProps.name() != null) {
            entry.setName(newProps.name());
        }

        if (newProps.authors().isPresent()) {
            entry.setAuthors(newProps.authors().get());
        }

        return Optional.of(repo.save(entry));
    }

    @Override
    public Optional<Entry> delete(Long cursor) {
        var optional = repo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var entry = optional.get();
        repo.delete(entry);
        return Optional.of(entry);
    }
}
