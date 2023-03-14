package com.peerrequest.app.api;

import com.jayway.jsonpath.JsonPath;
import com.peerrequest.app.PeerRequestBackend;
import com.peerrequest.app.data.*;
import com.peerrequest.app.data.repos.CategoryRepository;
import com.peerrequest.app.model.User;
import com.peerrequest.app.services.*;
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

import static org.springframework.test.util.AssertionErrors.*;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.in;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

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

    private static EntityWrapper reviewWithDocument;

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
        createEntryAndDrp(true, "User Entry Review", wrapperListReviews, entryService, documentService, drpService);

        for (int i = 0; i < 120; i++) {
            EntityWrapper wrapper = new EntityWrapper(wrapperListReviews.category,
                    wrapperListReviews.entry, wrapperListReviews.directRequestProcess);
            createReview(false, false, wrapper, documentService, directRequestService, reviewService);
            listReviews.add(wrapper);
        }

        // creates 10 reviews with user as reviewer to patch them as reviewer
        for (int i = 0; i < 10; i++) {
            EntityWrapper wrapper = new EntityWrapper(category);
            createEntryAndDrp(false, "Not User Entry + Review Patch " + i, wrapper,
                    entryService, documentService, drpService);
            createReview(true, false, wrapper, documentService, directRequestService, reviewService);
            patchReviewsAsReviewer.add(wrapper);
        }

        // creates a review to get and upload a review document. To notify the researcher
        reviewWithDocument = new EntityWrapper(category);
        createEntryAndDrp(false, "Not User Entry + Review + Document", reviewWithDocument,
                entryService, documentService, drpService);
        createReview(true, true, reviewWithDocument, documentService, directRequestService, reviewService);
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
                .andExpect(jsonPath("$.page_size").value(100))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(2))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(100)));
        var test = listReviews;
        List<Review> list = listReviews.stream().limit(100).map(p -> p.review).toList();
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
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews")
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
    void patchReviewAsReviewer() throws Exception {
        int index = 1;
        Entry entry = patchReviewsAsReviewer.get(index).entry;
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
                patch("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                        + "/reviews")
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
    @Order(1)
    void getReviewDocument(@Autowired DocumentService documentService) throws Exception {
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
        int index = 0;
        Entry entry = patchReviewsAsReviewer.get(index).entry;
        Review review = patchReviewsAsReviewer.get(index).review;

        mockMvc.perform(
                        post("/api/categories/" + entry.getCategoryId() + "/entries/" + entry.getId()
                                + "/reviews/" + review.getId() + "/notify")
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk());
    }

    @Test
    @Order(1)
    void listMessages() throws Exception {

    }

    @Test
    @Order(1)
    void deleteMessage() throws Exception {

    }

    @Test
    @Order(1)
    void createMessage() throws Exception {

    }

    @Test
    @Order(1)
    void patchMessage() throws Exception {

    }

    @Test
    @Order(1)
    void listEntriesByReviewer() throws Exception {

    }

    private static void createReview(Boolean isReviewer, Boolean hasReviewDocument, EntityWrapper wrapper,
                                     DocumentService documentService, DirectRequestService directRequestService,
                                     ReviewService reviewService) {

        directRequestService.create(
                DirectRequest.builder()
                        .state(DirectRequest.RequestState.ACCEPTED)
                        .reviewerId(isReviewer ? userId.toString() : UUID.randomUUID().toString())
                        .directRequestProcessId(wrapper.directRequestProcess.getId())
                        .build().toDto());

        Review review;
        if (hasReviewDocument) {
            review = reviewService.create(
                    Review.builder()
                            .reviewerId(isReviewer ? userId.toString() : UUID.randomUUID().toString())
                            .entryId(wrapper.entry.getId())
                            .reviewDocumentId(documentService.create(reviewDocumentDto).getId())
                            .confidenceLevel(Review.ConfidenceLevel.MEDIUM)
                            .build().toDto());
        } else {
            review = reviewService.create(
                    Review.builder()
                            .reviewerId(isReviewer ? userId.toString() : UUID.randomUUID().toString())
                            .entryId(wrapper.entry.getId())
                            .confidenceLevel(Review.ConfidenceLevel.MEDIUM)
                            .build().toDto());
        }
        wrapper.review = review;
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

    private static class EntityWrapper {

        private final Category category;
        private Entry entry;
        private DirectRequestProcess directRequestProcess;
        private Review review;
        private final List<Message> messages = new ArrayList<>();

        public EntityWrapper(Category category) {
            this.category = category;
        }

        public EntityWrapper(Category category, Entry entry) {
            this.category = category;
            this.entry = entry;
        }

        public EntityWrapper(Category category, Entry entry, DirectRequestProcess directRequestProcess) {
            this.category = category;
            this.entry = entry;
            this.directRequestProcess = directRequestProcess;
        }

        public EntityWrapper(Category category, Entry entry, DirectRequestProcess directRequestProcess,
                             Review review) {
            this.category = category;
            this.entry = entry;
            this.directRequestProcess = directRequestProcess;
            this.review = review;
        }
    }
}
