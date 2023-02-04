package com.peerrequest.app.services;

import com.peerrequest.app.data.DirectRequest;
import com.peerrequest.app.data.repos.DirectRequestRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DirectRequestServiceImpl implements DirectRequestService {

    @Autowired
    private DirectRequestRepository repo;

    @Override
    public DirectRequest create(DirectRequest.Dto newEntity) {
        // TODO: implement
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<DirectRequest> list(Long cursor, int maxCount, DirectRequest.Dto filter) {
        // TODO: implement
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Optional<DirectRequest> get(Long cursor) {
        return repo.findById(cursor);
    }

    @Override
    public Optional<DirectRequest> update(Long cursor, DirectRequest.Dto newProps) {
        // TODO: implement
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Optional<DirectRequest> delete(Long cursor) {
        // TODO: implement
        throw new RuntimeException("Not implemented yet");
    }
}
