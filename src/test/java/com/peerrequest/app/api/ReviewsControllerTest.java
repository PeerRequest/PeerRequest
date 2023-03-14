package com.peerrequest.app.api;

import com.peerrequest.app.PeerRequestBackend;
import com.peerrequest.app.services.CategoryService;
import com.peerrequest.app.services.DocumentService;
import com.peerrequest.app.services.EntryService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * This test class tests the endpoints of the {@link EntriesController} class.
 */
@SpringBootTest(properties = {"spring.mail.from=mail", "spring.load=false"})
@AutoConfigureMockMvc
@ContextConfiguration(classes = PeerRequestBackend.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ReviewsControllerTest {

    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    JavaMailSender mailSender;

    @Autowired
    private MockMvc mockMvc;
    private static final UUID userId = UUID.randomUUID();
    private static MockHttpSession session;

    @BeforeAll
    static void setUp(@Autowired CategoryService categoryService, @Autowired EntryService entryService,
                      @Autowired DocumentService documentService, @Autowired MockMvc mockMvc) throws Exception {

        mockMvc.perform(
                        get("/test/auth/login")
                                .queryParam("user_id", userId.toString())
                                .queryParam("user_name", "ich")
                                .queryParam("given_name", "kann")
                                .queryParam("family_name", "das")
                                .queryParam("email", "alles@nicht.mehr")
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk());

    }
}
