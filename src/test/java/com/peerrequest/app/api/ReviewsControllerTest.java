package com.peerrequest.app.api;

import com.peerrequest.app.PeerRequestBackend;
import com.peerrequest.app.data.*;
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
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.in;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
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

    private static MockHttpSession session;
    private static final UUID userId = UUID.randomUUID();

    private static Document.Dto reviewDocumentDto;

    private static Entry entryReviewList;
    private static final List<EntityWrapper> listReviews = new ArrayList<>();
    private static final List<Review> patchReviewsAsReviewer = new ArrayList<>();

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

        // creates 120 reviews with user as researcher entries to list, get and patch them
        Entry entryListReview = entryService.create(
                Entry.builder()
                        .researcherId(userId.toString())
                        .name("User Entry Review")
                        .authors("Alan Turing")
                        .documentId((documentService.create(reviewDocumentDto)).getId())
                        .categoryId(category.getId())
                        .build().toDto());

        DirectRequestProcess drpListReview = drpService.create(drpService.create(
                DirectRequestProcess.builder()
                        .entryId(entryListReview.getId())
                        .openSlots(ThreadLocalRandom.current().nextInt(1, 11))
                        .build().toDto()).toDto());

        for (int i = 0; i < 120; i++) {
            EntityWrapper entity = new EntityWrapper(category, entryListReview, drpListReview);
            createReview(false, false, entity, documentService, directRequestService, reviewService);
            listReviews.add(entity);
        }

/*

        //creates 10 reviews with user as reviewer
        for (int i = 0; i < 10; i++) {
            entryReviewList = entryService.create(
                    Entry.builder()
                            .researcherId(userId.toString())
                            .name("User Entry Review Patch " + i)
                            .authors("Alan Turing")
                            .documentId((documentService.create(reviewDocumentDto)).getId())
                            .categoryId(category.getId())
                            .build().toDto());
            patchReviewsAsReviewer.add()

            DirectRequestProcess drpReviewPatchReviewer = drpService.create(drpService.create(
                    DirectRequestProcess.builder()
                            .entryId(entryReviewList.getId())
                            .openSlots(ThreadLocalRandom.current().nextInt(1, 11))
                            .build().toDto()).toDto());
        }
        var test = new entityWrapper(null);
        test.

        for (int i = 0; i < 120; i++) {
            listReviews.add(createReview(false, false, entryReviewList.getId(), drpReviewList.getId(),
                    documentService, directRequestService, reviewService));
        }
        */
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

        List<Review> list = listReviews.stream().map(e -> e.review).limit(100).toList();
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
        String answer = "Hello, this is my answer.";
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
        int index = 0;
        Entry entry = listReviews.get(index).entry;
        Review review = listReviews.get(index).review;

        JSONObject patch = new JSONObject();
        String answer = "Hello, this is my answer.";
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
    @Order(1)
    void getReviewDocument() throws Exception {

    }

    @Test
    @Order(1)
    void postReviewDocument() throws Exception {

    }

    @Test
    @Order(1)
    void deleteReviewDocument() throws Exception {

    }

    @Test
    @Order(1)
    void notifyReview() throws Exception {

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
}
