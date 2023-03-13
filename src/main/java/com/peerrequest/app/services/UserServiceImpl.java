package com.peerrequest.app.services;

import com.peerrequest.app.model.User;

import java.util.List;
import java.util.Optional;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

/**
 * Default implementation of the UserService using KeyCloak.
 */
@Service
@Component
public class UserServiceImpl implements UserService {
    @Autowired
    private Environment env;

    private String realm;
    private Keycloak instance;

    private void init() {
        realm = env.getProperty("keycloak.admin.realm");
        var adminRealm = env.getProperty("keycloak.admin.admin-realm");
        var serverUrl = env.getProperty("keycloak.admin.serverUrl");
        var username = env.getProperty("keycloak.admin.username");
        var password = env.getProperty("keycloak.admin.password");
        var clientId = env.getProperty("keycloak.admin.clientId");

        this.instance = KeycloakBuilder.builder()
            .serverUrl(serverUrl)
            .realm(adminRealm)
            .clientId(clientId)
            .grantType("password")
            .username(username)
            .password(password)
            .build();
    }

    private Keycloak keycloak() {
        if (this.instance == null) {
            init();
        }

        return this.instance;
    }

    @Override
    public Page<User> list(Pageable pageable) {
        var usersResource = this.keycloak().realm(realm)
            .users();

        var total = usersResource.count();
        var users = usersResource
            .list((int) pageable.getOffset(), pageable.getPageSize())
            .stream().map(u -> new User(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail()))
            .toList();

        return new PageImpl<>(users, pageable, total);
    }

    @Override
    public Optional<User> get(String id) {
        var u = this.keycloak().realm(this.realm)
            .users()
            .get(id)
            .toRepresentation();

        if (u == null) {
            return Optional.empty();
        }

        return Optional.of(new User(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail()));
    }

    @Override
    public List<User> getUsers() {
        return this.keycloak().realm(realm).users().list()
                .stream().map(u -> new User(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail()))
                .toList();
    }
}
