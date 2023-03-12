package com.peerrequest.app.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.peerrequest.app.PeerRequestBackend;
import com.peerrequest.app.data.Category;
import com.peerrequest.app.services.CategoryService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.json.JSONObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest(properties = {"spring.mail.from=mail", "spring.load=false"})
@AutoConfigureMockMvc
@ContextConfiguration(classes = PeerRequestBackend.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
class CategoriesControllerTest {
    @Autowired
    CategoryService categoryService;

    @MockBean
    JavaMailSender mailSender;
    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private MockMvc mockMvc;
    private static List<Category> categories;
    private static UUID userId = UUID.randomUUID();

    private static MockHttpSession session;

    @BeforeAll
    public static void  setUp(@Autowired CategoryService categoryService, @Autowired MockMvc mockMvc) throws Exception {
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
        categories = new ArrayList<>();
        for (int i = 1; i <= 200; i++) {
            var c = Category.builder()
                .name("Test Category " + i)
                .year(2000 + i)
                .label(Category.CategoryLabel.INTERNAL)
                .minScore(0)
                .maxScore(5)
                .scoreStepSize(1)
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

    @Test
    @Transactional
    void listCategoriesWithLimit() throws Exception {

        Optional<Integer> limitOption = Optional.of(5);

        var action = mockMvc.perform(
                        get("/api/categories")
                                .session(session)
                                .secure(true)
                                .param("limit", String.valueOf(limitOption.get()))
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(limitOption.get()))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(200 / limitOption.get()))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(limitOption.get())));

        List<Category> list = categories.stream().limit(limitOption.get()).toList();
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

    @Test
    @Transactional
    void listCategoriesWithBadLimit() throws Exception {
        Optional<Integer> limitOption = Optional.of(0);

        mockMvc.perform(get("/api/categories")
                        .session(session)
                        .secure(true)
                        .param("limit", String.valueOf(limitOption.get())))
                .andExpect(status().isBadRequest());
    }



    @Test
    @Transactional
    void getCategoryFailure() throws Exception {
        long categoryId = -1L;
        mockMvc.perform(
                        get("/api/categories/" + categoryId)
                                .session(session)
                                .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void getCategory() throws Exception {
        Long categoryId = categories.get(0).getId();
        var action = mockMvc.perform(
                        get("/api/categories/" + categoryId.toString())
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk());


        action.andExpect(jsonPath("$.id").value(categoryId));
        action.andExpect(jsonPath("$.researcher_id").value(userId.toString()));
        action.andExpect(jsonPath("$.name").value("Test Category 1"));
        action.andExpect(jsonPath("$.year").value(2001));
        action.andExpect(jsonPath("$.label").value(Category.CategoryLabel.INTERNAL.toString()));
        action.andExpect(jsonPath("$.deadline").value(nullValue()));
        action.andExpect(jsonPath("$.min_score").value(0));
        action.andExpect(jsonPath("$.max_score").value(5));
        action.andExpect(jsonPath("$.score_step_size").value(1));
    }

    @Test
    @Transactional
    void deleteCategory() throws Exception {
        Long categoryId = categories.get(0).getId();
        var action = mockMvc.perform(
                        delete("/api/categories/" + categoryId.toString())
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk());

        action.andExpect(jsonPath("$.id").value(categoryId));
        action.andExpect(jsonPath("$.researcher_id").value(userId.toString()));
        action.andExpect(jsonPath("$.name").value("Test Category 1"));
        action.andExpect(jsonPath("$.year").value(2001));
        action.andExpect(jsonPath("$.label").value(Category.CategoryLabel.INTERNAL.toString()));
        action.andExpect(jsonPath("$.deadline").value(nullValue()));
        action.andExpect(jsonPath("$.min_score").value(0));
        action.andExpect(jsonPath("$.max_score").value(5));
        action.andExpect(jsonPath("$.score_step_size").value(1));
    }

    @Test
    void createCategory() throws Exception {

        JSONObject toPost = new JSONObject();
        toPost.put("name", "Created Category");
        toPost.put("year", 1337);
        toPost.put("label", "INTERNAL");
        toPost.put("min_score", 1.0);
        toPost.put("max_score", 5.0);
        toPost.put("score_step_size", 1.0);

        var action = mockMvc.perform(
                        post("/api/categories")
                                .session(session)
                                .secure(true)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toPost.toString()))
                .andExpect(status().isOk());

        action.andExpect(jsonPath("$.id").isNotEmpty());
        action.andExpect(jsonPath("$.researcher_id").value(userId.toString()));
        action.andExpect(jsonPath("$.name").value("Created Category"));
        action.andExpect(jsonPath("$.year").value(1337));
        action.andExpect(jsonPath("$.label").value(Category.CategoryLabel.INTERNAL.toString()));
        action.andExpect(jsonPath("$.deadline").value(nullValue()));
        action.andExpect(jsonPath("$.min_score").value(1.0));
        action.andExpect(jsonPath("$.max_score").value(5.0));
        action.andExpect(jsonPath("$.score_step_size").value(1.0));
    }
}