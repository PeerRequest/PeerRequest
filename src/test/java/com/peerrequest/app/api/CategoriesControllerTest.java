package com.peerrequest.app.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.peerrequest.app.PeerRequestBackend;
import com.peerrequest.app.data.Category;
import com.peerrequest.app.services.CategoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest()
@AutoConfigureMockMvc
@ContextConfiguration(classes = PeerRequestBackend.class)
@ActiveProfiles("test")
class CategoriesControllerTest {
    @Autowired
    CategoryService categoryService;

    @MockBean
    JavaMailSender mailSender;
    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private MockMvc mockMvc;
    private List<Category> categories;
    private UUID userId = UUID.randomUUID();

    private MockHttpSession session;

    @BeforeEach
    void setUp() throws Exception {
        // login and set current user
        session = new MockHttpSession();
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

        // setup data
        // first 10 categories will be created by the current test user
        categories = new ArrayList<Category>();
        for (int i = 1; i <= 200; i++) {
            var c = Category.builder()
                .name("Test Category " + i)
                .year(2000 + i)
                .label(Category.CategoryLabel.INTERNAL)
                .minScore(0)
                .maxScore(5)
                .researcherId((i <= 10 ? userId : UUID.randomUUID()).toString())
                .build();
            categories.add(categoryService.create(c.toDto()));
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void listCategories() throws Exception {
        var action = mockMvc.perform(
                get("/api/categories")
                    .session(session)
                    .secure(true))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page_size").value(100))
            .andExpect(jsonPath("$.current_page").value(1))
            .andExpect(jsonPath("$.last_page").value(2))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(100)));

        List<Category> list = categories.stream().limit(100).toList();
        for (int i = 0; i < list.size(); i++) {
            Category c = list.get(i);

            action.andExpect(jsonPath("$.content[" + i + "].id").value(c.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].researcher_id").value(c.getResearcherId()));
            action.andExpect(jsonPath("$.content[" + i + "].name").value(c.getName()));
            action.andExpect(jsonPath("$.content[" + i + "].year").value(c.getYear()));
            action.andExpect(jsonPath("$.content[" + i + "].label").value(c.getLabel().toString()));
            action.andExpect(jsonPath("$.content[" + i + "].deadline").value(c.getDeadline()));
            action.andExpect(jsonPath("$.content[" + i + "].min_score").value(c.getMinScore()));
            action.andExpect(jsonPath("$.content[" + i + "].max_score").value(c.getMaxScore()));
            action.andExpect(jsonPath("$.content[" + i + "].score_step_size").value(c.getScoreStepSize()));
        }
    }
}