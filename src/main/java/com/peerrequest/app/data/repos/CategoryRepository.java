package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Category;
import org.springframework.data.repository.CrudRepository;

public interface CategoryRepository extends CrudRepository<Category, Long> {
}