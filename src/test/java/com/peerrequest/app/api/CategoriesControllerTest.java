package com.peerrequest.app.api;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.peerrequest.app.PeerRequestBackend;
import com.peerrequest.app.data.*;
import com.peerrequest.app.data.repos.CategoryRepository;
import com.peerrequest.app.services.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;
import org.apache.commons.io.FileUtils;
import org.json.JSONObject;
import org.junit.jupiter.api.*;
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
import org.springframework.util.ResourceUtils;

@SpringBootTest(properties = {"spring.mail.from=mail", "spring.load=false"})
@AutoConfigureMockMvc
@ContextConfiguration(classes = PeerRequestBackend.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class CategoriesControllerTest {
    @Autowired
    CategoryService categoryService;

    @MockBean
    JavaMailSender mailSender;

    @MockBean
    UserService userService;
    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;

    @Autowired
    private MockMvc mockMvc;

    private static MockHttpSession session;
    private static Document.Dto document;
    private static UUID userId = UUID.randomUUID();
    private static final List<Category> totalCategories = new ArrayList<>();
    private static final List<Category> externalCategories = new ArrayList<>();
    @Autowired
    private CategoryRepository categoryRepository;

    @BeforeAll
    public static void  setUp(@Autowired CategoryService categoryService, @Autowired EntryService entryService,
                              @Autowired DocumentService documentService, @Autowired ReviewService reviewService,
                              @Autowired DirectRequestService directRequestService,
                              @Autowired DirectRequestProcessService drpService,
                              @Autowired MockMvc mockMvc) throws Exception {
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

        File file = ResourceUtils.getFile("classpath:loremipsum.pdf");
        document = new Document.Dto(Optional.empty(), Optional.of(FileUtils.readFileToByteArray(file)),
                Optional.of("loremipsum"));

        // setup data
        // first 10 categories will be created by the current test user
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
            totalCategories.add(categoryService.create(c.toDto()));
        }

        // first half categories will be created by the current user
        int externalSize = 10;
        for (int i = 1; i <= externalSize; i++) {
            var c = Category.builder()
                    .name("Test Category External" + i)
                    .year(2000 + i)
                    .label(Category.CategoryLabel.EXTERNAL)
                    .minScore(0)
                    .maxScore(5)
                    .scoreStepSize(1)
                    .researcherId((i <= externalSize / 2 ? userId : UUID.randomUUID()).toString())
                    .build();
            var cat = categoryService.create(c.toDto());
            totalCategories.add(cat);
            externalCategories.add(cat);
            if (i <= externalSize / 2) {
                setUpDeletionData(cat.getId(), entryService, documentService, drpService, directRequestService,
                        reviewService);
            }
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Order(1)
    void listCategories() throws Exception {
        var action = mockMvc.perform(
                get("/api/categories")
                    .session(session)
                    .secure(true))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.page_size").value(CategoriesController.MAX_PAGE_SIZE))
            .andExpect(jsonPath("$.current_page").value(1))
            .andExpect(jsonPath("$.last_page").value(
                    Math.ceil(totalCategories.size() / (double) CategoriesController.MAX_PAGE_SIZE)))
            .andExpect(jsonPath("$.content").isArray())
            .andExpect(jsonPath("$.content", hasSize(CategoriesController.MAX_PAGE_SIZE)));

        List<Category> list = totalCategories.stream().limit(CategoriesController.MAX_PAGE_SIZE).toList();
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
    @Order(1)
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
                .andExpect(jsonPath("$.last_page").value(
                        Math.ceil(totalCategories.size() / (double) limitOption.get())))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(limitOption.get())));

        List<Category> list = totalCategories.stream().limit(limitOption.get()).toList();
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
    @Order(1)
    void listCategoriesFailWithLimit() throws Exception {
        mockMvc.perform(get("/api/categories")
                        .session(session)
                        .secure(true)
                        .param("limit", String.valueOf(0)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Transactional
    @Order(1)
    void getCategoryFail() throws Exception {
        long categoryId = -1L;
        mockMvc.perform(
                        get("/api/categories/" + categoryId)
                                .session(session)
                                .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @Order(1)
    void getCategory() throws Exception {
        Category category = totalCategories.get(0);
        var action = mockMvc.perform(
                get("/api/categories/" + category.getId())
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk());

        action.andExpect(jsonPath("$.id").value(category.getId()));
        action.andExpect(jsonPath("$.researcher_id").value(category.getResearcherId()));
        action.andExpect(jsonPath("$.name").value(category.getName()));
        action.andExpect(jsonPath("$.year").value(category.getYear()));
        action.andExpect(jsonPath("$.label").value(category.getLabel().toString()));
        action.andExpect(jsonPath("$.deadline").value(nullValue()));
        action.andExpect(jsonPath("$.min_score").value(category.getMinScore()));
        action.andExpect(jsonPath("$.max_score").value(category.getMaxScore()));
        action.andExpect(jsonPath("$.score_step_size").value(category.getScoreStepSize()));
    }

    @Test
    @Transactional
    @Order(3)
    void deleteCategory(@Autowired CategoryService categoryService) throws Exception {
        Category category = totalCategories.get(0);
        var action = mockMvc.perform(
                delete("/api/categories/" + category.getId())
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk());

        action.andExpect(jsonPath("$.id").value(category.getId()));
        action.andExpect(jsonPath("$.researcher_id").value(category.getResearcherId()));
        action.andExpect(jsonPath("$.name").value(category.getName()));
        action.andExpect(jsonPath("$.year").value(category.getYear()));
        action.andExpect(jsonPath("$.label").value(category.getLabel().toString()));
        action.andExpect(jsonPath("$.deadline").value(nullValue()));
        action.andExpect(jsonPath("$.min_score").value(category.getMinScore()));
        action.andExpect(jsonPath("$.max_score").value(category.getMaxScore()));
        action.andExpect(jsonPath("$.score_step_size").value(category.getScoreStepSize()));

        var categoryDeleted = categoryService.get(category.getId());
        assertTrue("category was not deleted", categoryDeleted.isEmpty());
    }

    @Test
    @Transactional
    @Order(3)
    void deleteCategoryWithEntities(@Autowired CategoryService categoryService) throws Exception {
        Category category = externalCategories.get(0);
        var action = mockMvc.perform(
                delete("/api/categories/" + category.getId())
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk());

        action.andExpect(jsonPath("$.id").value(category.getId()));
        action.andExpect(jsonPath("$.researcher_id").value(category.getResearcherId()));
        action.andExpect(jsonPath("$.name").value(category.getName()));
        action.andExpect(jsonPath("$.year").value(category.getYear()));
        action.andExpect(jsonPath("$.label").value(category.getLabel().toString()));
        action.andExpect(jsonPath("$.deadline").value(nullValue()));
        action.andExpect(jsonPath("$.min_score").value(category.getMinScore()));
        action.andExpect(jsonPath("$.max_score").value(category.getMaxScore()));
        action.andExpect(jsonPath("$.score_step_size").value(category.getScoreStepSize()));

        var categoryDeleted = categoryService.get(category.getId());
        assertTrue("category was not deleted", categoryDeleted.isEmpty());
    }

    @Test
    @Transactional
    @Order(3)
    void deleteCategoryFailNotFound() throws Exception {
        long categoryId = -1L;
        mockMvc.perform(
                        delete("/api/categories/" + categoryId)
                                .session(session)
                                .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    @Order(3)
    void deleteCategoryFailExternalForbidden() throws Exception {
        long categoryId = externalCategories.get((externalCategories.size() / 2) + 1).getId();
        mockMvc.perform(
                delete("/api/categories/" + categoryId)
                        .session(session)
                        .secure(true))
                .andExpect(status().isForbidden());
    }


    @Test
    @Order(2)
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

    @Test
    @Order(2)
    void createCategoryFailId() throws Exception {

        JSONObject toPost = new JSONObject();
        toPost.put("id", "1");
        toPost.put("name", "Created Category");
        toPost.put("year", 1337);
        toPost.put("label", "INTERNAL");
        toPost.put("min_score", 1.0);
        toPost.put("max_score", 5.0);
        toPost.put("score_step_size", 1.0);

        mockMvc.perform(
                post("/api/categories")
                        .session(session)
                        .secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void createCategoryFailResearcherId() throws Exception {

        JSONObject toPost = new JSONObject();
        toPost.put("researcher_id", "1");
        toPost.put("name", "Created Category");
        toPost.put("year", 1337);
        toPost.put("label", "INTERNAL");
        toPost.put("min_score", 1.0);
        toPost.put("max_score", 5.0);
        toPost.put("score_step_size", 1.0);

        mockMvc.perform(
                post("/api/categories")
                        .session(session)
                        .secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test()
    @Order(2)
    void patchCategory() throws Exception {
        long categoryId = totalCategories.get(1).getId();

        JSONObject toPost = new JSONObject();
        toPost.put("id", categoryId);
        toPost.put("name", "Patched Category");
        toPost.put("year", 1338);
        toPost.put("label", "INTERNAL");
        toPost.put("min_score", 2.0);
        toPost.put("max_score", 6.0);
        toPost.put("score_step_size", 2.0);

        var action = mockMvc.perform(
                    patch("/api/categories")
                        .session(session)
                        .secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString()))
                .andExpect(status().isOk());

        action.andExpect(jsonPath("$.id").isNotEmpty());
        action.andExpect(jsonPath("$.name").value("Patched Category"));
        action.andExpect(jsonPath("$.year").value(1338));
        action.andExpect(jsonPath("$.label").value(Category.CategoryLabel.INTERNAL.toString()));
        action.andExpect(jsonPath("$.min_score").value(2.0));
        action.andExpect(jsonPath("$.max_score").value(6.0));
        action.andExpect(jsonPath("$.score_step_size").value(2.0));
    }

    @Test()
    @Order(2)
    void patchCategoryFailId() throws Exception {
        JSONObject toPost = new JSONObject();
        toPost.put("name", "Patched Category");

        mockMvc.perform(
                patch("/api/categories")
                        .session(session)
                        .secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString()))
                .andExpect(status().isBadRequest());

    }

    @Test()
    @Order(2)
    void patchCategoryFailResearcherId() throws Exception {
        JSONObject toPost = new JSONObject();
        toPost.put("id", 1L);
        toPost.put("researcher_id", "id");
        toPost.put("name", "Patched Category");

        mockMvc.perform(
                patch("/api/categories")
                        .session(session)
                        .secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString()))
                .andExpect(status().isBadRequest());
    }

    @Test()
    @Order(2)
    void patchCategoryFailNotExist() throws Exception {
        JSONObject toPost = new JSONObject();
        toPost.put("id", -1L);
        toPost.put("name", "Patched Category");

        mockMvc.perform(
                    patch("/api/categories")
                            .session(session)
                            .secure(true)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(toPost.toString()))
                .andExpect(status().isNotFound());
    }

    @Test()
    @Order(2)
    void patchCategoryFailExternal() throws Exception {
        long categoryId = externalCategories.get((externalCategories.size() / 2) + 2).getId();
        JSONObject toPost = new JSONObject();
        toPost.put("id", categoryId);
        toPost.put("name", "Patched Category");

        mockMvc.perform(
                patch("/api/categories")
                        .session(session)
                        .secure(true)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString()))
                .andExpect(status().isForbidden());
    }

    private static void setUpDeletionData(Long categoryId, EntryService entryService, DocumentService documentService,
                                          DirectRequestProcessService drpService,
                                          DirectRequestService directRequestService, ReviewService reviewService) {
        int entriesSize = 5;
        for (int i = 0; i < entriesSize; i++) {
            Entry entry = entryService.create(
                    Entry.builder()
                            .researcherId(userId.toString())
                            .name("Delete Entry " + i)
                            .authors("Alan Turing")
                            .documentId((documentService.create(document)).getId())
                            .categoryId(categoryId)
                            .build().toDto());
            if (i == entriesSize - 1) {
                return;
            }
            DirectRequestProcess drp = drpService.create(drpService.create(
                    DirectRequestProcess.builder()
                            .entryId(entry.getId())
                            .openSlots(ThreadLocalRandom.current().nextInt(1, 11))
                            .build().toDto()).toDto());

            // should be multiple of 3; first third accepted + reviews, second pending, third declined
            int requestsSize = 9;
            for (int j = 0; j < requestsSize; j++) {
                var re = directRequestService.create(
                        DirectRequest.builder()
                                .state(j < requestsSize / 3 ? DirectRequest.RequestState.ACCEPTED
                                        : j < (requestsSize / 3) * 2
                                        ? DirectRequest.RequestState.PENDING : DirectRequest.RequestState.DECLINED)
                                .reviewerId(UUID.randomUUID().toString())
                                .directRequestProcessId(drp.getId())
                                .build().toDto());
                if (j >=  requestsSize / 3) {
                    continue;
                }
                var r = reviewService.create(
                        Review.builder()
                                .reviewerId(UUID.randomUUID().toString())
                                .entryId(entry.getId())
                                .confidenceLevel(Review.ConfidenceLevel.MEDIUM)
                                .build().toDto());
            }
        }
    }
}