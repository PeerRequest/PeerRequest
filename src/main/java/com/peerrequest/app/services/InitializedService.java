package com.peerrequest.app.services;

/**
 * Describes a service that needs to be initialized.
 */
public interface InitializedService {
    void initialize();

    void dispose();
}
