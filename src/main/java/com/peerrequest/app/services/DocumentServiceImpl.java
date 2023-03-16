package com.peerrequest.app.services;

import com.peerrequest.app.data.Document;
import com.peerrequest.app.data.repos.DocumentRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

/**
 * Default implementation of a DocumentService.
 */
@Service
public class DocumentServiceImpl implements DocumentService {
    @Autowired
    private DocumentRepository repo;

    @Override
    public Document create(Document.Dto newEntity) {
        return repo.save(Document.fromDto(newEntity));
    }

    @Override
    public Page<Document> list(int page, int maxCount, Document.Dto filter) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public Optional<Document> get(String cursor) {
        return repo.findById(cursor);
    }

    @Override
    public Optional<Document> update(String cursor, Document.Dto newProps) {
        throw new RuntimeException("Operation not supported");
    }

    @Override
    public Optional<Document> delete(String cursor) {
        var optional = repo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var document = optional.get();
        repo.delete(document);
        return Optional.of(document);
    }
}