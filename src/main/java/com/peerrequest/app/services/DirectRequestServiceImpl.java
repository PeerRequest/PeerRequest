package com.peerrequest.app.services;

import com.peerrequest.app.data.DirectRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DirectRequestServiceImpl implements DirectRequestService {
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
        // TODO: implement
        throw new RuntimeException("Not implemented yet");
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
