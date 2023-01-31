package com.peerrequest.app.services;

import com.peerrequest.app.data.Category;
import com.peerrequest.app.data.repos.CategoryRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository repo;

    @Override
    public Category create(Category.Dto newEntity) {
        return repo.save(Category.fromDto(newEntity));
    }

    @Override
    public List<Category> list(Long cursor, int maxCount, Category.Dto filter) {
        return repo.list(cursor, Pageable.ofSize(maxCount), filter == null ? null : filter.name());
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
            category.setName(newProps.name());
        }
        if (newProps.label() != null) {
            category.setName(newProps.name());
        }
        if (newProps.maxScore() != null) {
            category.setName(newProps.name());
        }
        if (newProps.minScore() != null) {
            category.setName(newProps.name());
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
        return Optional.of(category);
    }
}