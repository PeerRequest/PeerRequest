package com.peerrequest.app.services;

import com.peerrequest.app.data.DirectRequest;
import com.peerrequest.app.data.repos.DirectRequestRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of a Direct Request Service.
 */
@Service
public class DirectRequestServiceImpl implements DirectRequestService {

    @Autowired
    private DirectRequestRepository repo;

    @Override
    public DirectRequest create(DirectRequest.Dto newEntity) {
        return repo.save(DirectRequest.fromDto(newEntity));
    }

    @Override
    public Page<DirectRequest> list(int page, int maxCount, DirectRequest.Dto filter) {
        if (filter.directRequestProcessId().isPresent()) {
            return repo.findByDirectRequestProcessId(filter.directRequestProcessId().get(),
                    PageRequest.of(page, maxCount));
        } else if (filter.reviewerId().isPresent()) {
            return repo.findByReviewerId(filter.reviewerId().get(), PageRequest.of(page, maxCount));
        } else {
            return repo.findAll(Pageable.ofSize(maxCount).withPage(page));
        }
    }

    @Override
    public Optional<DirectRequest> get(Long cursor) {
        return repo.findById(cursor);
    }

    @Override
    public Optional<DirectRequest> update(Long cursor, DirectRequest.Dto newProps) {
        var optional = repo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var request = optional.get();
        if (newProps.state().isPresent()) {
            request.setState(newProps.state().get());
        }
        return Optional.of(repo.save(request));
    }

    @Override
    public Optional<DirectRequest> delete(Long cursor) {
        var optional = repo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var request = optional.get();
        repo.delete(request);
        return Optional.of(request);
    }
}