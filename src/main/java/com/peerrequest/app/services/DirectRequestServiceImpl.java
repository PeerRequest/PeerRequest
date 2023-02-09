package com.peerrequest.app.services;

import com.peerrequest.app.data.DirectRequest;
import com.peerrequest.app.data.repos.DirectRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

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
        return null;
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