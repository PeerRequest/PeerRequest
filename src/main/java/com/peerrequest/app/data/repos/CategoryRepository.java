package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Category;
import java.util.List;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

/**
 * Defines a Category Repository.
 */
public interface CategoryRepository extends CrudRepository<Category, Long> {
    /**
     * Defines the list operation.
     *
     * @param cursor   if not null the last id of the previous page
     * @param pageable describes the pages
     * @param name     if not null is used as a search filter
     * @return a list of all matching categories
     */
    default List<Category> list(Long cursor, Pageable pageable, String name) {
        if (cursor == null && name == null) {
            return list(pageable);
        } else if (cursor == null) {
            return listNameContains(name, pageable);
        } else if (name == null) {
            return list(cursor, pageable);
        } else {
            return listNameContains(cursor, name, pageable);
        }
    }

    @Query("select c from Category c order by c.id DESC")
    List<Category> list(Pageable pageable);

    @Query("select c from Category c where c.id < ?1 order by c.id DESC")
    List<Category> list(Long cursor, Pageable pageable);

    @Query("select c from Category c where c.id < ?1 and c.name like concat('%', ?2, '%') order by c.id DESC")
    List<Category> listNameContains(Long cursor, String name, Pageable pageable);

    @Query("select c from Category c where c.name like concat('%', ?1, '%') order by c.id DESC")
    List<Category> listNameContains(String name, Pageable pageable);
}