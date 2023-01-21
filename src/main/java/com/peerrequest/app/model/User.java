package com.peerrequest.app.model;

import lombok.Getter;

/**
 * This class represents a user.
 *
 * @author User1 Halpick
 * @author User5 Mildt
 * @version 0.0.1
 */
public record User(@Getter com.peerrequest.app.model.User.UserSelector id, @Getter String firstName,
                   @Getter String lastName, @Getter String email) {

    /**
     * Constructor for a user.
     *
     * @param id        id of the user
     * @param firstName first name(s) of the user
     * @param lastName  last name of the user
     * @param email     email address of the user
     */
    public User {
    }

    /**
     * Identifies a User.
     *
     * @param id id of a User
     */
    public record UserSelector(String id) {
    }
}
