package com.peerrequest.app.services;

import com.peerrequest.app.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Category JPA Repository, using Long as ID and Category as relation.
 */
@Repository("CategoryService")
public interface CategoryService extends JpaRepository<Category, Long> {
}
