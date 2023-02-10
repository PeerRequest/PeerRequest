package com.peerrequest.app;

import com.peerrequest.app.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main class for the PeerRequest Backend.
 */
@SpringBootApplication
public class PeerRequestBackend {

    @Autowired
    private NotificationService service;

    public static void main(String[] args) {
        SpringApplication.run(PeerRequestBackend.class, args);
    }
}
