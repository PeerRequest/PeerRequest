package com.peerrequest.app.data.repos;

import com.peerrequest.app.data.Entry;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

/**
 * Defines an Entry Repository.
 */
public interface EntryRepository extends CrudRepository<Entry, Long> {
    /**
     * Defines the list operation.
     *
     * @param cursor   if not null the last id of the previous page
     * @param pageable describes the pages
     * @return a list of all matching entries
     */
    default List<Entry> list(Long cursor, Pageable pageable, String name, Long categoryId) {

        if (categoryId != null) {
            if (cursor == null) {
                return listIsInCategory(categoryId, pageable);
            } else {
                return listIsInCategory(cursor, categoryId, pageable);
            }
        }

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

    @Query("select e from Entry e order by e.id DESC")
    List<Entry> list(Pageable pageable);

    @Query("select e from Entry e where e.id < ?1 order by e.id DESC")
    List<Entry> list(Long cursor, Pageable pageable);

    @Query("select e from Entry e where e.id < ?1 and e.categoryId = ?2 order by e.id DESC")
    List<Entry> listIsInCategory(Long cursor, Long categoryId, Pageable pageable);

    @Query("select e from Entry e where e.categoryId = ?1 order by e.id DESC")
    List<Entry> listIsInCategory(Long categoryId, Pageable pageable);


    @Query("select e from Entry e where e.id < ?1 and e.name like concat('%', ?2, '%') order by e.id DESC")
    List<Entry> listNameContains(Long cursor, String name, Pageable pageable);

    @Query("select e from Entry e where e.name like concat('%', ?1, '%') order by e.id DESC")
    List<Entry> listNameContains(String name, Pageable pageable);

}
