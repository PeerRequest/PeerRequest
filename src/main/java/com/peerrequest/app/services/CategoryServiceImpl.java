package com.peerrequest.app.services;

import com.peerrequest.app.data.Category;
import com.peerrequest.app.data.repos.CategoryRepository;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository repo;

    @Autowired
    private EntryService entryService;

    @Override
    public Category create(Category.Dto newEntity) {
        return repo.save(Category.fromDto(newEntity));
    }

    @Override
    public Page<Category> list(int page, int maxCount, Category.Dto filter) {
        return repo.findAll(Pageable.ofSize(maxCount).withPage(page));
    }

    @Override
    public Optional<Category> get(Long cursor) {
        return repo.findById(cursor);
    }

    @Override
    public Optional<Category> update(Long cursor, Category.Dto newProps) {
        var optional = repo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var category = optional.get();

        if (newProps.name() != null) {
            category.setName(newProps.name());
        }
        if (newProps.year() != null) {
            category.setYear(newProps.year());
        }
        if (newProps.deadline() != null) {
            category.setDeadline(newProps.deadline());
        }
        if (newProps.label() != null) {
            category.setLabel(newProps.label());
        }
        if (newProps.maxScore() != null) {
            category.setMaxScore(newProps.maxScore());
        }
        if (newProps.minScore() != null) {
            category.setMinScore(newProps.minScore());
        }
        if (newProps.scoreStepSize() != null) {
            category.setScoreStepSize(newProps.scoreStepSize());
        }

        return Optional.of(repo.save(category));
    }

    @Override
    public Optional<Category> delete(Long cursor) {
        var optional = repo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var category = optional.get();
        repo.delete(category);

        for (var entry : entryService.listByCategoryId(cursor)) {
            entryService.delete(entry.getId());
        }

        return Optional.of(category);
    }
}