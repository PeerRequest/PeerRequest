package com.peerrequest.app.services;

import java.util.List;

public interface CrudService<T, S, F, U> {
    S create(T newEntity);

    List<T> list(S cursor, int maxCount, F filter);

    T get(S cursor);

    T update(S cursor, U updater);

    T delete(S cursor);
}
