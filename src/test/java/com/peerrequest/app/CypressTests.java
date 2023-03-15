package com.peerrequest.app;

import com.peerrequest.app.services.UserService;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;

/**
 * Defines an Instance for e2e tests using cypress.
 */

@ActiveProfiles("test")
@SpringBootApplication
public class CypressTests {
    /**
     * Starts the test instance.
     *
     * @param args command line arguments
     */
    public static void main(String[] args) {
        var app = new SpringApplication(CypressTests.class);
        app.setAdditionalProfiles("test");
        app.run(args);
    }

    @Bean
    @Primary
    public JavaMailSender nameService() {
        return Mockito.mock(JavaMailSender.class);
    }

    @Bean
    @Primary
    public UserService userService() {
        return Mockito.mock(UserService.class);
    }

    @Bean
    @Primary
    public ClientRegistrationRepository clientRegistrationRepository() {
        return Mockito.mock(ClientRegistrationRepository.class);
    }
}