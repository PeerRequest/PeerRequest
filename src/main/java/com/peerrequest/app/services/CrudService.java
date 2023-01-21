package com.peerrequest.app.services;

import java.util.List;

/**
 * Describes the basic functionality of a CRUD service.
 *
 * <p>
 * C - Create
 * R - Read
 * U - Update
 * D - Delete
 * </p>
 *
 * @param <T> Data Type
 * @param <S> Data Selector
 * @param <F> Data Filter
 * @param <U> Data Updater
 */
public interface CrudService<T, S, F, U> {
    S create(T newEntity);

    List<T> list(S cursor, int maxCount, F filter);

    T get(S cursor);

    T update(S cursor, U updater);

    T delete(S cursor);
}
