package com.peerrequest.app.model;

import lombok.Getter;

/**
 * This class represents a user.
 *
 * @author Ludger Halpick
 * @author Michael Mildt
 * @version 0.0.1
 */
public class User {

    /**
     * Constructor for a user.
     *
     * @param id         id of the user
     * @param firstName  first name(s) of the user
     * @param lastName   last name of the user
     * @param email      email address of the user
     */
    public User(UserSelector id, String firstName, String lastName, String email) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
    }

    @Getter
    private final UserSelector id;

    @Getter
    private final String firstName;

    @Getter
    private final String lastName;

    @Getter
    private final String email;

    public record UserSelector(String userID) {
    }
}
