package com.peerrequest.app.services;

import com.peerrequest.app.data.DirectRequestProcess;
import com.peerrequest.app.data.repos.DirectRequestProcessRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DirectRequestProcessServiceImpl implements DirectRequestProcessService {

    @Autowired
    private DirectRequestProcessRepository repo;


    @Override
    public DirectRequestProcess create(DirectRequestProcess.Dto newEntity) {
        return repo.save(DirectRequestProcess.fromDto(newEntity));
    }

    @Override
    public List<DirectRequestProcess> list(Long cursor, int maxCount, DirectRequestProcess.Dto filter) {
        // TODO: implement
        throw new RuntimeException("Not implemented yet");
    }

    @Override
    public Optional<DirectRequestProcess> get(Long cursor) {
        return repo.getByEntryId(cursor);
    }

    @Override
    public Optional<DirectRequestProcess> update(Long cursor, DirectRequestProcess.Dto newProps) {
        var optional = repo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var directRequestProcess = optional.get();

        if (newProps.openSlots().isEmpty()) {
            return Optional.empty();
        }

        directRequestProcess.setOpenSlots(newProps.openSlots().get());

        return Optional.of(repo.save(directRequestProcess));
    }

    @Override
    public Optional<DirectRequestProcess> delete(Long cursor) {
        // TODO: implement
        throw new RuntimeException("Not implemented yet");
    }
}
