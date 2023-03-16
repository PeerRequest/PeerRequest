package com.peerrequest.app.api;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.util.AssertionErrors.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.jayway.jsonpath.JsonPath;
import com.peerrequest.app.PeerRequestBackend;
import com.peerrequest.app.data.*;
import com.peerrequest.app.services.*;
import java.io.File;
import java.time.ZonedDateTime;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.ResourceUtils;

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

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private static MockHttpSession session;
    private static final UUID userId = UUID.randomUUID();

    private static Document.Dto reviewDocumentDto;
    private static final List<EntityWrapper> listReviews = new ArrayList<>();
    private static final List<EntityWrapper> patchReviewsAsReviewer = new ArrayList<>();

    private static final List<EntityWrapper> listReviewsByReviewer = new ArrayList<>();

    private static EntityWrapper reviewWithDocument;

    private static EntityWrapper reviewMessageReviewer;
    private static EntityWrapper reviewMessageResearcher;

    private static EntityWrapper reviewNotResearcherOrReviewer;

    @BeforeAll
    static void setUp(@Autowired CategoryService categoryService, @Autowired EntryService entryService,
                      @Autowired DocumentService documentService, @Autowired DirectRequestProcessService drpService,
                      @Autowired DirectRequestService directRequestService, @Autowired ReviewService reviewService,
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

        // setup data
        Category category = categoryService.create(
                Category.builder()
                        .name("Test Category Review")
                        .year(2000)
                        .label(Category.CategoryLabel.INTERNAL)
                        .minScore(0)
                        .maxScore(5)
                        .scoreStepSize(1)
                        .researcherId(userId.toString())
                        .build().toDto());

        File file = ResourceUtils.getFile("classpath:loremipsum.pdf");
        reviewDocumentDto = new Document.Dto(Optional.empty(), Optional.of(FileUtils.readFileToByteArray(file)),
                Optional.of("loremipsum"));


        // creates 120 reviews with current user as researcher to list, get and patch them as researcher
        EntityWrapper wrapperListReviews = new EntityWrapper(category);
        createEntryAndDrp(true, "User is Researcher + Review", wrapperListReviews, entryService,
                documentService, drpService);

        for (int i = 0; i < 120; i++) {
            EntityWrapper wrapper = new EntityWrapper(wrapperListReviews.category,
                    wrapperListReviews.entry, wrapperListReviews.directRequestProcess);
            createReview(false, false, wrapper, documentService, directRequestService, reviewService);
            listReviews.add(wrapper);
        }


        // creates 10 reviews with user as reviewer to patch them as reviewer
        for (int i = 0; i < 10; i++) {
            EntityWrapper wrapper = new EntityWrapper(category);
            createEntryAndDrp(false, "User is Reviewer + Review Patch " + i, wrapper,
                    entryService, documentService, drpService);
            createReview(false, true, wrapper, documentService, directRequestService, reviewService);
            patchReviewsAsReviewer.add(wrapper);
        }


        // creates a review to get and upload a review document. To notify the researcher
        reviewWithDocument = new EntityWrapper(category);
        createEntryAndDrp(false, "User is Reviewer + Review + Document", reviewWithDocument,
                entryService, documentService, drpService);
        createReview(true, true, reviewWithDocument, documentService, directRequestService, reviewService);


        // creates a review with user as researcher (other as reviewer) to create, get and delete messages
        reviewMessageReviewer = new EntityWrapper(category);
        createEntryAndDrp(false, "User is Reviewer + Review + Messages", reviewMessageReviewer,
                entryService, documentService, drpService);
        createReview(false, true, reviewMessageReviewer, documentService, directRequestService, reviewService);

        // creates a review, user is not researcher or reviewer
        reviewNotResearcherOrReviewer = new EntityWrapper(category);
        createEntryAndDrp(false, "User is not Reviewer or Researcher", reviewNotResearcherOrReviewer,
                entryService, documentService, drpService);
        createReview(false, false, reviewNotResearcherOrReviewer, documentService, directRequestService, reviewService);

        reviewMessageResearcher = new EntityWrapper(category);
        createEntryAndDrp(true, "User is Researcher + Review + Messages", reviewMessageResearcher,
                entryService, documentService, drpService);
        createReview(false, false, reviewMessageResearcher, documentService, directRequestService, reviewService);
        // first half are user messages, second half are messages from the other role
        int size = 120;
        for (int i = 1; i <= size; i++) {
            createEntityMessage(i < (size / 2), "message " + i, reviewMessageReviewer, reviewService);
            createEntityMessage(i < (size / 2), "message " + i, reviewMessageResearcher, reviewService);
        }
    }

    @Test
    @Order(1)
    void listReviews() throws Exception {
        Entry entry = listReviews.get(0).entry;
        var action = mockMvc.perform(
                get("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews")
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(ReviewsController.MAX_PAGE_SIZE))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(
                        Math.ceil(listReviews.size() / (double) ReviewsController.MAX_PAGE_SIZE)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(ReviewsController.MAX_PAGE_SIZE)));

        List<Review> list = listReviews.stream().limit(ReviewsController.MAX_PAGE_SIZE).map(p -> p.review).toList();
        for (int i = 0; i < list.size(); i++) {
            Review review = list.get(i);

            action.andExpect(jsonPath("$.content[" + i + "].id").value(review.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].reviewer_id").value(review.getReviewerId()));
            action.andExpect(jsonPath("$.content[" + i + "].entry_id").value(review.getEntryId()));
            action.andExpect(jsonPath("$.content[" + i + "].review_document_id").value(review.getReviewDocumentId()));
            action.andExpect(jsonPath("$.content[" + i + "].confidence_level")
                    .value(review.getConfidenceLevel().toString()));
            action.andExpect(jsonPath("$.content[" + i + "].summary").value(review.getSummary()));
            action.andExpect(jsonPath("$.content[" + i + "].main_weaknesses").value(review.getMainWeakness()));
            action.andExpect(jsonPath("$.content[" + i + "].main_strengths").value(review.getMainStrengths()));
            action.andExpect(jsonPath("$.content[" + i + "].questions_for_authors")
                    .value(review.getQuestionsForAuthors()));
            action.andExpect(jsonPath("$.content[" + i + "].answers_from_authors")
                    .value(review.getAnswersFromAuthors()));
            action.andExpect(jsonPath("$.content[" + i + "].other_comments").value(review.getOtherComments()));
            action.andExpect(jsonPath("$.content[" + i + "].score").value(review.getScore()));
        }
    }

    @Test
    @Order(1)
    void listReviewsFailBadEntryId() throws Exception {
        Entry entry = listReviews.get(0).entry;
        mockMvc.perform(
                get("/api/categories/" + entry.getCategoryId() + "/entries/" + -1L + "/reviews")
                        .param("limit", String.valueOf(0))
                        .session(session)
                        .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(1)
    void listReviewsFailNotAllowed() throws Exception {
        Entry entry = reviewMessageReviewer.entry;
        mockMvc.perform(
                get("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId() + "/reviews")
                        .param("limit", String.valueOf(0))
                        .session(session)
                        .secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(1)
    void listReviewsFailBadLimit() throws Exception {
        Entry entry = listReviews.get(0).entry;
        mockMvc.perform(
                get("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId() + "/reviews")
                        .param("limit", String.valueOf(0))
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(1)
    void listReviewsWithLimit() throws Exception {
        int limit = 5;
        Entry entry = listReviews.get(0).entry;
        var action = mockMvc.perform(
                        get("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId() + "/reviews")
                                .param("limit", String.valueOf(limit))
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(limit))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(Math.ceil(listReviews.size() / (double) limit)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(limit)));

        List<Review> list = listReviews.stream().limit(limit).map(p -> p.review).toList();
        for (int i = 0; i < list.size(); i++) {
            Review review = list.get(i);

            action.andExpect(jsonPath("$.content[" + i + "].id").value(review.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].reviewer_id").value(review.getReviewerId()));
            action.andExpect(jsonPath("$.content[" + i + "].entry_id").value(review.getEntryId()));
            action.andExpect(jsonPath("$.content[" + i + "].review_document_id").value(review.getReviewDocumentId()));
            action.andExpect(jsonPath("$.content[" + i + "].confidence_level")
                    .value(review.getConfidenceLevel().toString()));
            action.andExpect(jsonPath("$.content[" + i + "].summary").value(review.getSummary()));
            action.andExpect(jsonPath("$.content[" + i + "].main_weaknesses").value(review.getMainWeakness()));
            action.andExpect(jsonPath("$.content[" + i + "].main_strengths").value(review.getMainStrengths()));
            action.andExpect(jsonPath("$.content[" + i + "].questions_for_authors")
                    .value(review.getQuestionsForAuthors()));
            action.andExpect(jsonPath("$.content[" + i + "].answers_from_authors")
                    .value(review.getAnswersFromAuthors()));
            action.andExpect(jsonPath("$.content[" + i + "].other_comments").value(review.getOtherComments()));
            action.andExpect(jsonPath("$.content[" + i + "].score").value(review.getScore()));
        }
    }

    @Test
    @Order(1)
    void getReview() throws Exception {
        int index = 0;
        Entry entry = listReviews.get(index).entry;
        Review review = listReviews.get(index).review;
        mockMvc.perform(
                get("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId())
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(review.getId()))
                .andExpect(jsonPath("$.reviewer_id").value(review.getReviewerId()))
                .andExpect(jsonPath("$.entry_id").value(review.getEntryId()))
                .andExpect(jsonPath("$.review_document_id").value(review.getReviewDocumentId()))
                .andExpect(jsonPath("$.confidence_level").value(review.getConfidenceLevel().toString()))
                .andExpect(jsonPath("$.summary").value(review.getSummary()))
                .andExpect(jsonPath("$.main_weaknesses").value(review.getMainWeakness()))
                .andExpect(jsonPath("$.main_strengths").value(review.getMainStrengths()))
                .andExpect(jsonPath("$.questions_for_authors").value(review.getQuestionsForAuthors()))
                .andExpect(jsonPath("$.answers_from_authors").value(review.getAnswersFromAuthors()))
                .andExpect(jsonPath("$.other_comments").value(review.getOtherComments()))
                .andExpect(jsonPath("$.score").value(review.getScore()));
    }

    @Test
    @Order(2)
    void patchReviewAsResearcher() throws Exception {
        int index = 0;
        Entry entry = listReviews.get(index).entry;
        Review review = listReviews.get(index).review;

        JSONObject patch = new JSONObject();
        String answer = "answer";
        patch.put("id", review.getId());
        patch.put("answers_from_authors", answer);

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId() + "/reviews")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(review.getId()))
                .andExpect(jsonPath("$.reviewer_id").value(review.getReviewerId()))
                .andExpect(jsonPath("$.entry_id").value(review.getEntryId()))
                .andExpect(jsonPath("$.review_document_id").value(review.getReviewDocumentId()))
                .andExpect(jsonPath("$.confidence_level").value(review.getConfidenceLevel().toString()))
                .andExpect(jsonPath("$.summary").value(review.getSummary()))
                .andExpect(jsonPath("$.main_weaknesses").value(review.getMainWeakness()))
                .andExpect(jsonPath("$.main_strengths").value(review.getMainStrengths()))
                .andExpect(jsonPath("$.questions_for_authors").value(review.getQuestionsForAuthors()))
                .andExpect(jsonPath("$.answers_from_authors").value(answer))
                .andExpect(jsonPath("$.other_comments").value(review.getOtherComments()))
                .andExpect(jsonPath("$.score").value(review.getScore()));
    }

    @Test
    @Order(2)
    void patchReviewAsResearcherFailNoId() throws Exception {
        int index = 0;
        Entry entry = listReviews.get(index).entry;

        JSONObject patch = new JSONObject();
        patch.put("answers_from_authors", "answer");

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId() + "/reviews")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchReviewAsResearcherFailNotAllowed() throws Exception {
        int index = 0;
        final Entry entry = listReviews.get(index).entry;
        Review review = listReviews.get(index).review;

        JSONObject patch = new JSONObject();
        patch.put("id", review.getId());
        patch.put("confidence_level", Review.ConfidenceLevel.HIGH.toString());
        patch.put("summary", "summary");
        patch.put("main_weaknesses", "mainWeaknesses");
        patch.put("main_strengths", "mainStrengths");
        patch.put("questions_for_authors", "questions");
        patch.put("other_comments", "otherComments");
        patch.put("score", 2.f);

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId() + "/reviews")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchReviewAsResearcherFailReviewerIdSet() throws Exception {
        int index = 0;
        final Entry entry = listReviews.get(index).entry;
        Review review = listReviews.get(index).review;

        JSONObject patch = new JSONObject();
        patch.put("id", review.getId());
        patch.put("reviewer_id", "reviewerId");
        patch.put("answers_from_authors", "answer");

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId() + "/reviews")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchReviewAsResearcherFailEntryIdSet() throws Exception {
        int index = 0;
        final Entry entry = listReviews.get(index).entry;
        Review review = listReviews.get(index).review;

        JSONObject patch = new JSONObject();
        patch.put("id", review.getId());
        patch.put("entry_id", 1L);
        patch.put("answers_from_authors", "answer");

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId() + "/reviews")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchReviewAsResearcherFailDocumentIdSet() throws Exception {
        int index = 0;
        final Entry entry = listReviews.get(index).entry;
        Review review = listReviews.get(index).review;

        JSONObject patch = new JSONObject();
        patch.put("id", review.getId());
        patch.put("review_document_id", "documentId");
        patch.put("answers_from_authors", "answer");

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId() + "/reviews")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchReviewAsResearcherFailBadReviewId() throws Exception {
        int index = 0;
        Entry entry = listReviews.get(index).entry;

        JSONObject patch = new JSONObject();
        patch.put("id", -1L);
        patch.put("answers_from_authors", "answer");

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId() + "/reviews")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    void patchReviewAsReviewer() throws Exception {
        int index = 1;
        final Entry entry = patchReviewsAsReviewer.get(index).entry;
        Review review = patchReviewsAsReviewer.get(index).review;

        JSONObject patch = new JSONObject();

        String confidenceLevel = Review.ConfidenceLevel.HIGH.toString();
        String summary = "summary";
        String mainWeaknesses = "mainWeaknesses";
        String mainStrengths = "mainStrengths";
        String questionsForAuthors = "questions";
        String otherComments = "otherComments";
        Float score = 2.f;

        patch.put("id", review.getId());
        patch.put("confidence_level", confidenceLevel);
        patch.put("summary", summary);
        patch.put("main_weaknesses", mainWeaknesses);
        patch.put("main_strengths", mainStrengths);
        patch.put("questions_for_authors", questionsForAuthors);
        patch.put("other_comments", otherComments);
        patch.put("score", score);

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId() + "/reviews")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(review.getId()))
                .andExpect(jsonPath("$.reviewer_id").value(review.getReviewerId()))
                .andExpect(jsonPath("$.entry_id").value(review.getEntryId()))
                .andExpect(jsonPath("$.review_document_id").value(review.getReviewDocumentId()))
                .andExpect(jsonPath("$.confidence_level").value(confidenceLevel))
                .andExpect(jsonPath("$.summary").value(summary))
                .andExpect(jsonPath("$.main_weaknesses").value(mainWeaknesses))
                .andExpect(jsonPath("$.main_strengths").value(mainStrengths))
                .andExpect(jsonPath("$.questions_for_authors").value(questionsForAuthors))
                .andExpect(jsonPath("$.answers_from_authors").value(review.getAnswersFromAuthors()))
                .andExpect(jsonPath("$.other_comments").value(otherComments))
                .andExpect(jsonPath("$.score").value(score));
    }

    @Test
    @Order(2)
    void patchReviewAsReviewerFailNotAllowed() throws Exception {
        int index = 1;
        final Entry entry = patchReviewsAsReviewer.get(index).entry;
        Review review = patchReviewsAsReviewer.get(index).review;

        JSONObject patch = new JSONObject();
        patch.put("id", review.getId());
        patch.put("answers_from_authors", "answer");

        mockMvc.perform(
                        patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId() + "/reviews")
                                .content(patch.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session)
                                .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(1)
    void getReviewDocument() throws Exception {
        Entry entry = reviewWithDocument.entry;
        Review review = reviewWithDocument.review;

        mockMvc.perform(
                get("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/document")
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(content().bytes(reviewDocumentDto.file().get()));
    }

    @Test
    @Order(1)
    void getReviewDocumentFailBadEntryId() throws Exception {
        Entry entry = reviewWithDocument.entry;
        Review review = reviewWithDocument.review;

        mockMvc.perform(
                get("/api/categories/" + entry.getCategoryId() + "/entries/" + -1L
                        + "/reviews/" + review.getId() + "/document")
                        .session(session)
                        .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(1)
    void getReviewDocumentFailNotAllowed() throws Exception {
        Entry entry = reviewNotResearcherOrReviewer.entry;
        Review review = reviewNotResearcherOrReviewer.review;

        mockMvc.perform(
                get("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/document")
                        .session(session)
                        .secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(1)
    void getReviewDocumentFailBadReviewId() throws Exception {
        Entry entry = reviewWithDocument.entry;

        mockMvc.perform(
                get("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + -1L + "/document")
                        .session(session)
                        .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(1)
    void getReviewDocumentFailNoDocument() throws Exception {
        Entry entry = patchReviewsAsReviewer.get(0).entry;
        Review review = patchReviewsAsReviewer.get(0).review;

        mockMvc.perform(
                get("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/document")
                        .session(session)
                        .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    void postReviewDocument() throws Exception {
        Entry entry = reviewWithDocument.entry;
        Review review = reviewWithDocument.review;
        String documentId = review.getReviewDocumentId();

        MockMultipartFile document = new MockMultipartFile("file", "loremipsum.pdf",
                "application/pdf", FileUtils.readFileToByteArray(ResourceUtils.getFile("classpath:loremipsum.pdf")));

        var action = mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                                + "/reviews/" + review.getId() + "/document")
                        .file(document)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(review.getId()))
                .andExpect(jsonPath("$.reviewer_id").value(review.getReviewerId()))
                .andExpect(jsonPath("$.entry_id").value(review.getEntryId()))
                .andExpect(jsonPath("$.review_document_id").isNotEmpty())
                .andExpect(jsonPath("$.confidence_level").value(review.getConfidenceLevel().toString()))
                .andExpect(jsonPath("$.summary").value(review.getSummary()))
                .andExpect(jsonPath("$.main_weaknesses").value(review.getMainWeakness()))
                .andExpect(jsonPath("$.main_strengths").value(review.getMainStrengths()))
                .andExpect(jsonPath("$.questions_for_authors").value(review.getQuestionsForAuthors()))
                .andExpect(jsonPath("$.answers_from_authors").value(review.getAnswersFromAuthors()))
                .andExpect(jsonPath("$.other_comments").value(review.getOtherComments()))
                .andExpect(jsonPath("$.score").value(review.getScore()))
                .andReturn();

        String documentNewId = JsonPath.read(action.getResponse().getContentAsString(), "$.review_document_id");
        assertFalse("document has not been updated", documentId.equals(documentNewId));
    }

    @Test
    @Order(3)
    void deleteReviewDocument(@Autowired ReviewService reviewService,
                              @Autowired DocumentService documentService) throws Exception {
        Entry entry = reviewWithDocument.entry;
        Review review = reviewWithDocument.review;

        mockMvc.perform(
                delete("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                                + "/reviews/" + review.getId() + "/document")
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk());

        assertTrue("document was not deleted", documentService.get(review.getReviewDocumentId()).isEmpty());
        assertNull("document id was not deleted", reviewService.get(review.getId()).get().getReviewDocumentId());
    }

    @Test
    @Order(3)
    void deleteReviewDocumentFailBadId() throws Exception {
        Entry entry = reviewWithDocument.entry;

        mockMvc.perform(
                delete("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + -1L + "/document")
                        .session(session)
                        .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(3)
    void deleteReviewDocumentFailNoDocument() throws Exception {
        Entry entry = patchReviewsAsReviewer.get(patchReviewsAsReviewer.size() - 1).entry;
        Review review = patchReviewsAsReviewer.get(patchReviewsAsReviewer.size() - 1).review;

        mockMvc.perform(
                delete("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/document")
                        .session(session)
                        .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(1)
    void notifyReviewAsReviewer() throws Exception {
        Entry entry = reviewWithDocument.entry;
        Review review = reviewWithDocument.review;

        mockMvc.perform(
                post("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/notify")
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk());
    }

    @Test
    @Order(1)
    void notifyReviewAsResearcher() throws Exception {
        Entry entry = reviewMessageResearcher.entry;
        Review review = reviewMessageResearcher.review;

        mockMvc.perform(
                post("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/notify")
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk());
    }

    @Test
    @Order(1)
    void notifyReviewAsResearcherFailBadEntryId() throws Exception {
        int index = 0;
        Entry entry = patchReviewsAsReviewer.get(index).entry;
        Review review = patchReviewsAsReviewer.get(index).review;

        mockMvc.perform(
                        post("/api/categories/" + entry.getCategoryId() + "/entries/" + -1L
                                + "/reviews/" + review.getId() + "/notify")
                                .session(session)
                                .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(1)
    void listMessages() throws Exception {
        EntityWrapper wrapper = reviewMessageReviewer;
        Entry entry = wrapper.entry;
        Review review = wrapper.review;

        var action = mockMvc.perform(
                get("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(ReviewsController.MESSAGES_MAX_PAGE_SIZE))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(
                        Math.ceil(wrapper.messages.size() / (double) ReviewsController.MESSAGES_MAX_PAGE_SIZE)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(ReviewsController.MESSAGES_MAX_PAGE_SIZE)));

        List<Message> list = wrapper.messages.stream().limit(ReviewsController.MESSAGES_MAX_PAGE_SIZE).toList();
        for (int i = 0; i < list.size(); i++) {
            Message message = list.get(i);

            action.andExpect(jsonPath("$.content[" + i + "].id").value(message.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].review_id").value(message.getReviewId()));
            action.andExpect(jsonPath("$.content[" + i + "].creator_id").value(message.getCreatorId()));
            action.andExpect(jsonPath("$.content[" + i + "].content").value(message.getContent()));

            String timeStampString = JsonPath.read(action.andReturn().getResponse().getContentAsString(),
                    "$.content[" + i + "].timestamp");
            assertTrue("timestamp does not match", areDatesEqual(timeStampString, message.getTimeStamp()));
        }
    }

    @Test
    @Order(1)
    void listMessagesFailBadLimit() throws Exception {
        EntityWrapper wrapper = reviewMessageReviewer;
        Entry entry = wrapper.entry;
        Review review = wrapper.review;

        mockMvc.perform(
                get("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .param("limit", String.valueOf(0))
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(1)
    void listMessagesWithLimit() throws Exception {
        int limit = 5;
        EntityWrapper wrapper = reviewMessageReviewer;
        Entry entry = wrapper.entry;
        Review review = wrapper.review;

        var action = mockMvc.perform(
                        get("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                                + "/reviews/" + review.getId() + "/messages")
                                .param("limit", String.valueOf(limit))
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(limit))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(
                        Math.ceil(wrapper.messages.size() / (double) limit)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(limit)));

        List<Message> list = wrapper.messages.stream().limit(limit).toList();
        for (int i = 0; i < list.size(); i++) {
            Message message = list.get(i);

            action.andExpect(jsonPath("$.content[" + i + "].id").value(message.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].review_id").value(message.getReviewId()));
            action.andExpect(jsonPath("$.content[" + i + "].creator_id").value(message.getCreatorId()));
            action.andExpect(jsonPath("$.content[" + i + "].content").value(message.getContent()));

            String timeStampString = JsonPath.read(action.andReturn().getResponse().getContentAsString(),
                    "$.content[" + i + "].timestamp");
            assertTrue("timestamp does not match", areDatesEqual(timeStampString, message.getTimeStamp()));
        }
    }

    @Test
    @Order(2)
    void deleteMessage(@Autowired ReviewService reviewService) throws Exception {
        Entry entry = reviewMessageResearcher.entry;
        Review review = reviewMessageResearcher.review;
        Message message = reviewMessageResearcher.messages.get(0);

        mockMvc.perform(
                delete("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages/" + message.getId())
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk());

        assertTrue("message was not deleted", reviewService.getMessage(message.getId()).isEmpty());
    }

    @Test
    @Order(2)
    void deleteMessageFailNotAllowed() throws Exception {
        Entry entry = reviewMessageReviewer.entry;
        Review review = reviewMessageReviewer.review;
        Message message = reviewMessageReviewer.messages.get(reviewMessageReviewer.messages.size() - 1);

        mockMvc.perform(
                delete("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages/" + message.getId())
                        .session(session)
                        .secure(true))
                .andExpect(status().isForbidden());;
    }

    @Test
    @Order(2)
    void createMessageReviewer() throws Exception {
        Entry entry = reviewMessageReviewer.entry;
        Review review = reviewMessageReviewer.review;

        ZonedDateTime timestamp = ZonedDateTime.now();
        String content = "content";
        JSONObject toPost = new JSONObject();
        toPost.put("timestamp", timestamp);
        toPost.put("content", content);

        mockMvc.perform(
                post("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.review_id").value(review.getId()))
                .andExpect(jsonPath("$.creator_id").value(userId.toString()))
                .andExpect(jsonPath("$.content").value(content))
                .andReturn();
    }

    @Test
    @Order(2)
    void createMessageResearcher() throws Exception {
        Entry entry = reviewMessageResearcher.entry;
        Review review = reviewMessageResearcher.review;

        ZonedDateTime timestamp = ZonedDateTime.now();
        String content = "content";
        JSONObject toPost = new JSONObject();
        toPost.put("timestamp", timestamp);
        toPost.put("content", content);

        mockMvc.perform(
                        post("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                                + "/reviews/" + review.getId() + "/messages")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toPost.toString())
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.review_id").value(review.getId()))
                .andExpect(jsonPath("$.creator_id").value(userId.toString()))
                .andExpect(jsonPath("$.content").value(content))
                .andReturn();
    }

    @Test
    @Order(2)
    void createMessageReviewerFailIdSet() throws Exception {
        final Entry entry = reviewMessageReviewer.entry;
        final Review review = reviewMessageReviewer.review;

        JSONObject toPost = new JSONObject();
        toPost.put("id", -1L);
        toPost.put("timestamp", ZonedDateTime.now());
        toPost.put("content", "content");

        mockMvc.perform(
                post("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void createMessageReviewerFailReviewIdSet() throws Exception {
        final Entry entry = reviewMessageReviewer.entry;
        final Review review = reviewMessageReviewer.review;

        JSONObject toPost = new JSONObject();
        toPost.put("review_id", -1L);
        toPost.put("timestamp", ZonedDateTime.now());
        toPost.put("content", "content");

        mockMvc.perform(
                post("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void createMessageReviewerFailCreatorIdSet() throws Exception {
        final Entry entry = reviewMessageReviewer.entry;
        final Review review = reviewMessageReviewer.review;

        JSONObject toPost = new JSONObject();
        toPost.put("creator_id", "c");
        toPost.put("timestamp", ZonedDateTime.now());
        toPost.put("content", "content");

        mockMvc.perform(
                post("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void createMessageReviewerFailNoTimestamp() throws Exception {
        Entry entry = reviewMessageReviewer.entry;
        Review review = reviewMessageReviewer.review;

        JSONObject toPost = new JSONObject();
        toPost.put("content", "content");

        mockMvc.perform(
                post("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void createMessageReviewerFailNoContent() throws Exception {
        Entry entry = reviewMessageReviewer.entry;
        Review review = reviewMessageReviewer.review;

        JSONObject toPost = new JSONObject();
        toPost.put("timestamp", ZonedDateTime.now());


        mockMvc.perform(
                post("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());

        toPost.put("content", "");
        mockMvc.perform(
                        post("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                                + "/reviews/" + review.getId() + "/messages")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(toPost.toString())
                                .session(session)
                                .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchMessage() throws Exception {
        Entry entry = reviewMessageReviewer.entry;
        Review review = reviewMessageReviewer.review;
        Message message = reviewMessageReviewer.messages.get(1);

        String content = "new content";
        JSONObject toPost = new JSONObject();
        toPost.put("id", message.getId());
        toPost.put("content", content);

        var action = mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.review_id").value(review.getId()))
                .andExpect(jsonPath("$.creator_id").value(userId.toString()))
                .andExpect(jsonPath("$.content").value(content))
                .andReturn();

        String timeStampString = JsonPath.read(action.getResponse().getContentAsString(),
                "$.timestamp");
        assertTrue("timestamp does not match", areDatesEqual(timeStampString, message.getTimeStamp()));
    }

    @Test
    @Order(2)
    void patchMessageFailBadMessageId() throws Exception {
        Entry entry = reviewMessageReviewer.entry;
        Review review = reviewMessageReviewer.review;

        String content = "new content";
        JSONObject toPost = new JSONObject();
        toPost.put("id", -1L);
        toPost.put("content", content);

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    void patchMessageFailNoId() throws Exception {
        Entry entry = reviewMessageReviewer.entry;
        Review review = reviewMessageReviewer.review;

        JSONObject toPost = new JSONObject();
        toPost.put("content", "new content");

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchMessageFailReviewIdSet() throws Exception {
        final Entry entry = reviewMessageReviewer.entry;
        final Review review = reviewMessageReviewer.review;
        Message message = reviewMessageReviewer.messages.get(1);

        JSONObject toPost = new JSONObject();
        toPost.put("id", message.getId());
        toPost.put("review_id", -1L);
        toPost.put("content", "new content");

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchMessageFailCreatorIdSet() throws Exception {
        final Entry entry = reviewMessageReviewer.entry;
        final Review review = reviewMessageReviewer.review;
        Message message = reviewMessageReviewer.messages.get(1);

        JSONObject toPost = new JSONObject();
        toPost.put("id", message.getId());
        toPost.put("creator_id", "c");
        toPost.put("content", "new content");

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchMessageFailTimestampSet() throws Exception {
        final Entry entry = reviewMessageReviewer.entry;
        final Review review = reviewMessageReviewer.review;
        Message message = reviewMessageReviewer.messages.get(1);

        JSONObject toPost = new JSONObject();
        toPost.put("id", message.getId());
        toPost.put("timestamp", ZonedDateTime.now());
        toPost.put("content", "new content");

        mockMvc.perform(
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews/" + review.getId() + "/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(1)
    void listReviewsByReviewer() throws Exception {
        List<EntityWrapper> wrappers = listReviewsByReviewer;
        var action = mockMvc.perform(
                        get("/api/reviews")
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(wrappers.size()))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(
                       Math.ceil(wrappers.size() / (double) ReviewsController.MAX_PAGE_SIZE)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(wrappers.size())));

        for (int i = 0; i < wrappers.stream().limit(ReviewsController.MAX_PAGE_SIZE).toList().size(); i++) {
            Review review = wrappers.get(i).review;

            action.andExpect(jsonPath("$.content[" + i + "].first.id").value(review.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].first.reviewer_id").value(review.getReviewerId()));
            action.andExpect(jsonPath("$.content[" + i + "].first.entry_id").value(review.getEntryId()));
            action.andExpect(jsonPath("$.content[" + i + "].first.review_document_id")
                    .value(review.getReviewDocumentId()));
            action.andExpect(jsonPath("$.content[" + i + "].first.confidence_level")
                    .value(review.getConfidenceLevel().toString()));
            action.andExpect(jsonPath("$.content[" + i + "].first.summary").value(review.getSummary()));
            action.andExpect(jsonPath("$.content[" + i + "].first.main_weaknesses").value(review.getMainWeakness()));
            action.andExpect(jsonPath("$.content[" + i + "].first.main_strengths").value(review.getMainStrengths()));
            action.andExpect(jsonPath("$.content[" + i + "].first.questions_for_authors")
                    .value(review.getQuestionsForAuthors()));
            action.andExpect(jsonPath("$.content[" + i + "].first.answers_from_authors")
                    .value(review.getAnswersFromAuthors()));
            action.andExpect(jsonPath("$.content[" + i + "].first.other_comments").value(review.getOtherComments()));
            action.andExpect(jsonPath("$.content[" + i + "].first.score").value(review.getScore()));
        }
    }

    @Test
    @Order(1)
    void listReviewsByReviewerWithLimit() throws Exception {
        int limit = 5;
        List<EntityWrapper> wrappers = listReviewsByReviewer;
        var action = mockMvc.perform(
                        get("/api/reviews")
                                .param("limit", String.valueOf(limit))
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(limit))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(
                        Math.ceil(wrappers.size() / (double) ReviewsController.MAX_PAGE_SIZE)))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(limit)));

        for (int i = 0; i < wrappers.stream().limit(limit).toList().size(); i++) {
            Review review = wrappers.get(i).review;

            action.andExpect(jsonPath("$.content[" + i + "].first.id").value(review.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].first.reviewer_id").value(review.getReviewerId()));
            action.andExpect(jsonPath("$.content[" + i + "].first.entry_id").value(review.getEntryId()));
            action.andExpect(jsonPath("$.content[" + i + "].first.review_document_id")
                    .value(review.getReviewDocumentId()));
            action.andExpect(jsonPath("$.content[" + i + "].first.confidence_level")
                    .value(review.getConfidenceLevel().toString()));
            action.andExpect(jsonPath("$.content[" + i + "].first.summary").value(review.getSummary()));
            action.andExpect(jsonPath("$.content[" + i + "].first.main_weaknesses").value(review.getMainWeakness()));
            action.andExpect(jsonPath("$.content[" + i + "].first.main_strengths").value(review.getMainStrengths()));
            action.andExpect(jsonPath("$.content[" + i + "].first.questions_for_authors")
                    .value(review.getQuestionsForAuthors()));
            action.andExpect(jsonPath("$.content[" + i + "].first.answers_from_authors")
                    .value(review.getAnswersFromAuthors()));
            action.andExpect(jsonPath("$.content[" + i + "].first.other_comments").value(review.getOtherComments()));
            action.andExpect(jsonPath("$.content[" + i + "].first.score").value(review.getScore()));
        }
    }

    @Test
    @Order(1)
    void listReviewsByReviewerFailBadLimit() throws Exception {
        mockMvc.perform(
                get("/api/reviews")
                        .param("limit", String.valueOf(0))
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    private static void createEntryAndDrp(Boolean isResearcher, String entryName, EntityWrapper wrapper,
                                             EntryService entryService, DocumentService documentService,
                                             DirectRequestProcessService drpService) {
        Entry entry = entryService.create(
                Entry.builder()
                        .researcherId(isResearcher ? userId.toString() : UUID.randomUUID().toString())
                        .name(entryName)
                        .authors("Alan Turing")
                        .documentId((documentService.create(reviewDocumentDto)).getId())
                        .categoryId(wrapper.category.getId())
                        .build().toDto());

        DirectRequestProcess drp = drpService.create(drpService.create(
                DirectRequestProcess.builder()
                        .entryId(entry.getId())
                        .openSlots(ThreadLocalRandom.current().nextInt(1, 11))
                        .build().toDto()).toDto());
        wrapper.entry = entry;
        wrapper.directRequestProcess = drp;
    }

    private static void createReview(Boolean hasReviewDocument, Boolean isUserReviewer, EntityWrapper wrapper,
                                     DocumentService documentService, DirectRequestService directRequestService,
                                     ReviewService reviewService) {

        directRequestService.create(
                DirectRequest.builder()
                        .state(DirectRequest.RequestState.ACCEPTED)
                        .reviewerId(isUserReviewer ? userId.toString() : UUID.randomUUID().toString())
                        .directRequestProcessId(wrapper.directRequestProcess.getId())
                        .build().toDto());

        Review review;
        if (hasReviewDocument) {
            review = reviewService.create(
                    Review.builder()
                            .reviewerId(isUserReviewer ? userId.toString() : UUID.randomUUID().toString())
                            .entryId(wrapper.entry.getId())
                            .reviewDocumentId(documentService.create(reviewDocumentDto).getId())
                            .confidenceLevel(Review.ConfidenceLevel.MEDIUM)
                            .build().toDto());
        } else {
            review = reviewService.create(
                    Review.builder()
                            .reviewerId(isUserReviewer ? userId.toString() : UUID.randomUUID().toString())
                            .entryId(wrapper.entry.getId())
                            .confidenceLevel(Review.ConfidenceLevel.MEDIUM)
                            .build().toDto());
        }
        wrapper.review = review;

        if (isUserReviewer) {
            listReviewsByReviewer.add(wrapper);
        }
    }

    private static void createEntityMessage(Boolean isCreator, String content, EntityWrapper wrapper,
                                      ReviewService reviewService) {
        Message message = reviewService.createMessage(
                Message.builder()
                        .reviewId(wrapper.review.getId())
                        .creatorId(isCreator ? userId.toString() : UUID.randomUUID().toString())
                        .timeStamp(ZonedDateTime.now())
                        .content(content)
                        .build().toDto());
        wrapper.messages.add(message);
    }

    private boolean areDatesEqual(String firstDateString, ZonedDateTime secondDate) {
        ZonedDateTime firstDate = ZonedDateTime.parse(firstDateString);
        return firstDate.getYear() == secondDate.getYear()
                && firstDate.getMonth() == secondDate.getMonth()
                && firstDate.getDayOfMonth() == secondDate.getDayOfMonth()
                && firstDate.getHour() == secondDate.getHour()
                && firstDate.getMinute() == secondDate.getMinute()
                && firstDate.getOffset() == secondDate.getOffset();
    }

    private static class EntityWrapper {

        private final Category category;
        private Entry entry;
        private DirectRequestProcess directRequestProcess;
        private Review review;
        private final List<Message> messages = new ArrayList<>();

        public EntityWrapper(Category category) {
            this.category = category;
        }

        public EntityWrapper(Category category, Entry entry, DirectRequestProcess directRequestProcess) {
            this.category = category;
            this.entry = entry;
            this.directRequestProcess = directRequestProcess;
        }
    }
}
