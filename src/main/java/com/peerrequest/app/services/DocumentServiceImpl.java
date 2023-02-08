package com.peerrequest.app.services;

import com.peerrequest.app.data.DocumentDTO;
import com.peerrequest.app.data.repos.DocumentDTORepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DocumentServiceImpl implements DocumentService {

    @Autowired
    private DocumentDTORepository repo;

    @Override
    public DocumentDTO create(DocumentDTO.Dto newEntity) {
        return repo.save(DocumentDTO.fromDto(newEntity));
    }

    @Override
    public Page<DocumentDTO> list(int page, int maxCount, DocumentDTO.Dto filter) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public Optional<DocumentDTO> get(String cursor) {
        return repo.findById(cursor);
    }

    @Override
    public Optional<DocumentDTO> update(String cursor, DocumentDTO.Dto newProps) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public Optional<DocumentDTO> delete(String cursor) {
        var optional = repo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var document = optional.get();
        repo.delete(document);
        return Optional.of(document);
    }
}
