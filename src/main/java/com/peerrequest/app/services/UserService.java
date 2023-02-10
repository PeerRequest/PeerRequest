package com.peerrequest.app.services;

import com.peerrequest.app.model.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Defines the functionality of a UserService.
 */
public interface UserService {
    Page<User> list(Pageable pageable);

    Optional<User> get(String id);
}
