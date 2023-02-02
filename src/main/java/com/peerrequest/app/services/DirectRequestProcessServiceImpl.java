package com.peerrequest.app.services;

import com.peerrequest.app.data.DirectRequestProcess;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DirectRequestProcessServiceImpl implements DirectRequestProcessService {
    @Override
    public DirectRequestProcess create(DirectRequestProcess.Dto newEntity) {
        // TODO: implement
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public List<DirectRequestProcess> list(Long cursor, int maxCount, DirectRequestProcess.Dto filter) {
        // TODO: implement
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Optional<DirectRequestProcess> get(Long cursor) {
        // TODO: implement
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Optional<DirectRequestProcess> update(Long cursor, DirectRequestProcess.Dto newProps) {
        // TODO: implement
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Optional<DirectRequestProcess> delete(Long cursor) {
        // TODO: implement
        throw new RuntimeException("Not implemented yet");
    }
}
