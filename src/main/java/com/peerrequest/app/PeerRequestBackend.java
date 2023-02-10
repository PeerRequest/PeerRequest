package com.peerrequest.app;

import com.peerrequest.app.services.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

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

    @EventListener(ApplicationReadyEvent.class)
    public void sendMail() {
        service.sendReviewNotification("", "", 0L, NotificationService.ReviewMessageTemplates.EDIT_REVIEWER);
        System.out.println("Successful");
    }
}
