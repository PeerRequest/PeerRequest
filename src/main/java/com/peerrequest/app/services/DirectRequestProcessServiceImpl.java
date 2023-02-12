package com.peerrequest.app.services;

import com.peerrequest.app.data.DirectRequestProcess;
import com.peerrequest.app.data.repos.DirectRequestProcessRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Implementation of a DirectRequestProcessService.
 */
@Service
public class DirectRequestProcessServiceImpl implements DirectRequestProcessService {

    @Autowired
    private DirectRequestProcessRepository repo;

    @Autowired
    private DirectRequestService directRequestService;

    @Override
    public DirectRequestProcess create(DirectRequestProcess.Dto newEntity) {
        return repo.save(DirectRequestProcess.fromDto(newEntity));
    }

    @Override
    public Page<DirectRequestProcess> list(int page, int maxCount, DirectRequestProcess.Dto filter) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public Optional<DirectRequestProcess> get(Long cursor) {
        return repo.findById(cursor);
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
        var optional = repo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var directRequestProcess = optional.get();
        repo.delete(directRequestProcess);

        for (var directRequests : this.directRequestService.listByDirectRequestProcessId(cursor)) {
            this.directRequestService.delete(directRequests.getId());
        }

        return Optional.of(directRequestProcess);
    }

    @Override
    public Optional<DirectRequestProcess> getByEntry(Long entryId) {
        return repo.getByEntryId(entryId);
    }
}