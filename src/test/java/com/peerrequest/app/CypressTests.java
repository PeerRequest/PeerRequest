package com.peerrequest.app;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.peerrequest.app.model.User;
import com.peerrequest.app.services.UserService;
import java.util.List;
import java.util.Optional;
import org.mockito.Mockito;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
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

    @SuppressWarnings("checkstyle:MissingJavadocMethod")
    @Bean
    @Primary
    public UserService userService() {
        var mock = Mockito.mock(UserService.class);

        var mockUsers = List.of(new User("1", "Helma", "Gunter", "helma@gunter.de"));
        when(mock.getUsers()).thenReturn(mockUsers);
        when(mock.list(any())).thenReturn(new PageImpl<User>(mockUsers, Pageable.ofSize(1), mockUsers.size()));

        when(mock.get("1")).thenReturn(Optional.of(mockUsers.get(0)));

        return mock;
    }

    @Bean
    @Primary
    public ClientRegistrationRepository clientRegistrationRepository() {
        return Mockito.mock(ClientRegistrationRepository.class);
    }
}