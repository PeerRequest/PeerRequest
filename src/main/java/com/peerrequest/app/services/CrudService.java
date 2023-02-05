package com.peerrequest.app.services;

import java.util.List;
import java.util.Optional;

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
 * @param <T>> Data Type
 * @param <I>  ID of data type
 * @param <D>  DTO of data type
 */
public interface CrudService<T, I, D> {
    T create(D newEntity);

    List<T> list(I cursor, int maxCount, D filter);

    Optional<T> get(I cursor);

    Optional<T> update(I cursor, D newProps);

    Optional<T> delete(I cursor);
}
