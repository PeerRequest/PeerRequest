package com.peerrequest.app.services;

import com.peerrequest.app.model.User;

/**
 * Interface class responsible for user management.
 */
public interface UserService {
    /**
     * The methode to return a User class.
     * @param selector the user to be returned
     * @return the actual User object
     */
    User getUser(User.UserSelector selector);
}
