package com.peerrequest.app.model;

import lombok.Getter;
import net.minidev.json.JSONObject;

/**
 * This class represents a user.
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
     * Transform the given user into JSON.
     *
     * @return a JSON object
     */
    public JSONObject toJson() {
        var json = new JSONObject();
        json.put("id", this.id.id());
        json.put("first_name", this.firstName());
        json.put("last_name", this.lastName());
        json.put("email", this.email());
        return json;
    }

    /**
     * Identifies a User.
     *
     * @param id id of a User
     */
    public record UserSelector(String id) {
    }
}
