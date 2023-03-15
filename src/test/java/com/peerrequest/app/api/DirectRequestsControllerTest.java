package com.peerrequest.app.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.util.AssertionErrors.assertFalse;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.peerrequest.app.PeerRequestBackend;
import com.peerrequest.app.data.*;
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
public class DirectRequestsControllerTest {

    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    JavaMailSender mailSender;

    @MockBean
    UserService userService;

    @Autowired
    private MockMvc mockMvc;

    private static Category category;
    //for getSpecificRequests()
    private static final List<Entry> entries = new ArrayList<>();
    private static final List<Entry> entriesClaimOpenSlot = new ArrayList<>();
    private static final List<DirectRequestProcess> drpPatch = new ArrayList<>();
    private static final List<DirectRequest> requestsPatch = new ArrayList<>();
    private static final List<DirectRequest> userRequestedOthers = new ArrayList<>();
    private static final UUID userId = UUID.randomUUID();
    private static MockHttpSession session;
    private static Entry userEntryCreateDrp;
    private static DirectRequestProcess userDrpRequests;
    private static DirectRequestProcess userDrpDelete;

    @BeforeAll
    static void setUp(@Autowired CategoryService categoryService, @Autowired EntryService entryService,
                      @Autowired DocumentService documentService, @Autowired DirectRequestProcessService drpService,
                      @Autowired DirectRequestService directRequestService,
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
        category = categoryService.create(
                Category.builder()
                        .name("Test Category Requests")
                        .year(2000)
                        .label(Category.CategoryLabel.INTERNAL)
                        .minScore(0)
                        .maxScore(5)
                        .scoreStepSize(1)
                        .researcherId(userId.toString())
                        .build().toDto());

        File file = ResourceUtils.getFile("classpath:loremipsum.pdf");
        Document.Dto document = new Document.Dto(Optional.empty(), Optional.of(FileUtils.readFileToByteArray(file)),
                Optional.of("loremipsum"));


        // create entry, drp, and 120 Request (40 each state, random open slots 1-10) for currently signed-in user
        // to list the requests
        var userEntryRequests = entryService.create(
                Entry.builder()
                        .researcherId(userId.toString())
                        .name("User Entry with Requests")
                        .authors("Alan Turing")
                        .documentId((documentService.create(document)).getId())
                        .categoryId(category.getId())
                        .build().toDto());
        entries.add(userEntryRequests);

        userDrpRequests = drpService.create(drpService.create(
                DirectRequestProcess.builder()
                        .entryId(userEntryRequests.getId())
                        .openSlots(ThreadLocalRandom.current().nextInt(1, 11))
                        .build().toDto()).toDto());

        int requestsSize = 120; // should be multiple of three
        for (int i = 0; i < requestsSize; i++) {
            var r = directRequestService.create(
                    DirectRequest.builder()
                            .state(i < requestsSize / 3 ? DirectRequest.RequestState.PENDING
                                    : (i < (requestsSize / 3) * 2 ? DirectRequest.RequestState.ACCEPTED
                                    : DirectRequest.RequestState.DECLINED))
                            .reviewerId(UUID.randomUUID().toString())
                            .directRequestProcessId(userDrpRequests.getId())
                            .build().toDto());
            userRequestedOthers.add(r);
        }

        // create entry, drp of user - to delete a request
        var userEntryDelete = entryService.create(Entry.builder()
                .researcherId(userId.toString())
                .name("User Entry Delete")
                .authors("Alan Turing")
                .documentId(documentService.create(document).getId())
                .categoryId(category.getId())
                .build().toDto());
        entries.add(userEntryDelete);

        userDrpDelete = drpService.create(
                DirectRequestProcess.builder()
                        .entryId(userEntryDelete.getId())
                        .openSlots(ThreadLocalRandom.current().nextInt(0, 11))
                        .build().toDto());


        // create entry of user without drp - to create a drp
        userEntryCreateDrp = entryService.create(
                Entry.builder()
                        .researcherId(userId.toString())
                        .name("User Entry Create Direct Request Process")
                        .authors("Alan Turing")
                        .documentId(documentService.create(document).getId())
                        .categoryId(category.getId())
                        .build().toDto());
        entries.add(userEntryCreateDrp);


        // create nine entries, drps with open slot but no requests for currently signed-in user - to claim open slots
        for (int i = 0; i < 9; i++) {
            var e = entryService.create(
                    Entry.builder()
                            .researcherId(UUID.randomUUID().toString())
                            .name("Not User Entry with Open Slots " + i)
                            .authors("Alan Turing")
                            .documentId(documentService.create(document).getId())
                            .categoryId(category.getId())
                            .build().toDto());
            entries.add(e);
            entriesClaimOpenSlot.add(e);

            var p = drpService.create(
                    DirectRequestProcess.builder()
                            .entryId(e.getId())
                            .openSlots(10)
                            .build().toDto());
        }


        // create nine entries, drps with one requests each (first third drp has pending, second third accepted,
        // last third declined) - to patch direct requests
        int drpsSize = 9; // should be multiple of 3
        for (int i = 0; i < drpsSize; i++) {

            var e = entryService.create(
                    Entry.builder()
                            .researcherId(UUID.randomUUID().toString())
                            .name("User Entry Requested " + i)
                            .authors("Alan Turing")
                            .documentId(documentService.create(document).getId())
                            .categoryId(category.getId())
                            .build().toDto());
            entries.add(e);

            var p = drpService.create(
                    DirectRequestProcess.builder()
                    .entryId(e.getId())
                    .openSlots(0)
                    .build().toDto());
            drpPatch.add(p);

            var r = directRequestService.create(
                    DirectRequest.builder()
                            .state(i < drpsSize / 3 ? DirectRequest.RequestState.PENDING
                                    : (i < (drpsSize / 3) * 2 ? DirectRequest.RequestState.ACCEPTED
                                    : DirectRequest.RequestState.DECLINED))
                            .reviewerId(userId.toString())
                            .directRequestProcessId(p.getId())
                            .build().toDto());
            requestsPatch.add(r);
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @Order(1)
    void getDirectRequestProcess() throws Exception {
        mockMvc.perform(
                get("/api/categories/" + category.getId() + "/entries/" + userDrpRequests.getEntryId()
                        + "/process")
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDrpRequests.getId()))
                .andExpect(jsonPath("$.entry_id").value(userDrpRequests.getEntryId()))
                .andExpect(jsonPath("$.open_slots").value(userDrpRequests.getOpenSlots()));
    }

    @Test
    @Order(2)
    void createDirectRequestProcess() throws Exception {
        int openSlots = 2;
        JSONObject toPost = new JSONObject();
        toPost.put("open_slots", openSlots);

        mockMvc.perform(
                post("/api/categories/" + category.getId() + "/entries/" + userEntryCreateDrp.getId()
                        + "/process")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.entry_id").value(userEntryCreateDrp.getId()))
                .andExpect(jsonPath("$.open_slots").value(openSlots));
    }

    @Test
    @Order(2)
    void patchDirectRequestProcess() throws Exception {
        int openSlots = 3;
        JSONObject patch = new JSONObject();
        patch.put("open_slots", 3);

        mockMvc.perform(
                patch("/api/categories/" + category.getId() + "/entries/" + userDrpRequests.getEntryId()
                        + "/process")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(userDrpRequests.getId()))
                .andExpect(jsonPath("$.entry_id").value(userDrpRequests.getEntryId()))
                .andExpect(jsonPath("$.open_slots").value(openSlots));
    }

    @Test
    @Order(1)
    void getDirectRequest() throws Exception {
        DirectRequest request = userRequestedOthers.get(
                ThreadLocalRandom.current().nextInt(0, userRequestedOthers.size()));
        mockMvc.perform(
                get("/api/categories/" + category.getId() + "/entries/" + userDrpRequests.getEntryId()
                        + "/process/requests/" + request.getId())
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.state").value(request.getState().toString()))
                .andExpect(jsonPath("$.reviewer_id").value(request.getReviewerId()))
                .andExpect(jsonPath("$.direct_request_process_id").value(request.getDirectRequestProcessId()));
    }

    @Test
    @Order(2)
    void deleteDirectRequest(@Autowired DirectRequestService directRequestService) throws Exception {
        var request = directRequestService.create(
                DirectRequest.builder()
                        .state(DirectRequest.RequestState.PENDING)
                        .reviewerId(UUID.randomUUID().toString())
                        .directRequestProcessId(userDrpDelete.getId())
                        .build().toDto());

        mockMvc.perform(
                delete("/api/categories/" + category.getId() + "/entries/" + userDrpDelete.getEntryId()
                        + "/process/requests/" + request.getId().toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk());

        assertTrue("entry was not deleted", directRequestService.get(request.getId()).isEmpty());
    }

    @Test
    @Order(1)
    void listDirectRequestsByEntry() throws Exception {
        var action = mockMvc.perform(
                        get("/api/categories/" + category.getId() + "/entries/" + userDrpRequests.getEntryId()
                                + "/process/requests")
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(100))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(2))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(100)));

        List<DirectRequest> list = userRequestedOthers.stream().limit(100).toList();
        for (int i = 0; i < list.size(); i++) {
            DirectRequest request = list.get(i);

            action.andExpect(jsonPath("$.content[" + i + "].id").value(request.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].state").value(request.getState().toString()));
            action.andExpect(jsonPath("$.content[" + i + "].reviewer_id").value(request.getReviewerId()));
            action.andExpect(jsonPath("$.content[" + i + "].direct_request_process_id")
                    .value(request.getDirectRequestProcessId()));
        }
    }

    @Test
    @Order(2)
    void postDirectRequest() throws Exception {
        String reviewerId = UUID.randomUUID().toString();
        JSONObject toPost = new JSONObject();
        toPost.put("reviewer_id", reviewerId);

        mockMvc.perform(
                post("/api/categories/" + category.getId() + "/entries/" + userDrpRequests.getEntryId()
                        + "/process/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(toPost.toString())
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.state").value(DirectRequest.RequestState.PENDING.toString()))
                .andExpect(jsonPath("$.reviewer_id").value(reviewerId))
                .andExpect(jsonPath("$.direct_request_process_id").value(userDrpRequests.getId()));
    }

    @Test
    @Order(2)
    void patchDirectRequestAccept(@Autowired ReviewService reviewService) throws Exception {
        int index = 0;
        DirectRequestProcess drp = drpPatch.get(index);
        DirectRequest request = requestsPatch.get(index);
        String state = DirectRequest.RequestState.ACCEPTED.toString();

        JSONObject patch = new JSONObject();
        patch.put("request_id", request.getId());
        patch.put("state", state);

        mockMvc.perform(
                patch("/api/categories/" + category.getId() + "/entries/" + drp.getEntryId()
                        + "/process/requests")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.state").value(state))
                .andExpect(jsonPath("$.reviewer_id").value(request.getReviewerId()))
                .andExpect(jsonPath("$.direct_request_process_id").value(request.getDirectRequestProcessId()));

        assertTrue("review was not created", reviewService.getReviewerIdsByEntryId(drp.getEntryId()).stream()
                .anyMatch(string -> string.equals(request.getReviewerId())));
    }

    @Test
    @Order(2)
    void patchDirectRequestDecline(@Autowired ReviewService reviewService) throws Exception {
        int index = 1;
        DirectRequestProcess drp = drpPatch.get(index);
        DirectRequest request = requestsPatch.get(index);
        String state = DirectRequest.RequestState.DECLINED.toString();

        JSONObject patch = new JSONObject();
        patch.put("request_id", request.getId());
        patch.put("state", state);

        mockMvc.perform(
                patch("/api/categories/" + category.getId() + "/entries/" + drp.getEntryId()
                        + "/process/requests")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(request.getId()))
                .andExpect(jsonPath("$.state").value(state))
                .andExpect(jsonPath("$.reviewer_id").value(request.getReviewerId()))
                .andExpect(jsonPath("$.direct_request_process_id").value(request.getDirectRequestProcessId()));

        assertFalse("review was created", reviewService.getReviewerIdsByEntryId(drp.getEntryId()).stream()
                .anyMatch(string -> string.equals(request.getReviewerId())));
    }

    @Test
    @Order(1)
    void listRequestsByResearcher() throws Exception {
        var action = mockMvc.perform(
                get("/api/requests")
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(requestsPatch.size()))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(1))
                .andExpect(jsonPath("$.content").isArray());

        for (int i = 0; i < requestsPatch.size(); i++) {
            DirectRequest r = requestsPatch.get(i);

            action.andExpect(jsonPath("$.content[" + i + "].first.id").value(r.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].first.state").value(r.getState().toString()));
            action.andExpect(jsonPath("$.content[" + i + "].first.reviewer_id").value(userId.toString()));
            action.andExpect(jsonPath("$.content[" + i + "].first.direct_request_process_id")
                    .value(r.getDirectRequestProcessId()));
        }
    }

    @Test
    @Order(1)
    void getSpecificRequests() throws Exception {
        List<Long> entryIds = entries.stream().map(Entry::getId).toList();
        ObjectMapper mapper = new ObjectMapper();
        String content = mapper.writeValueAsString(entryIds);

        var action = mockMvc.perform(
                post("/api/requests")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk());

        for (int i = 0; i < (requestsPatch.size() / 3) * 2; i++) {
            DirectRequest r = requestsPatch.get(i);

            action.andExpect(jsonPath("$.[" + i + "].id").value(r.getId()));
            action.andExpect(jsonPath("$.[" + i + "].state").value(r.getState().toString()));
            action.andExpect(jsonPath("$.[" + i + "].reviewer_id").value(userId.toString()));
            action.andExpect(jsonPath("$.[" + i + "].direct_request_process_id")
                    .value(r.getDirectRequestProcessId()));
        }
    }

    @Test
    @Order(2)
    void claimOpenSlotNoRequest(@Autowired ReviewService reviewService) throws Exception {
        Entry entry = entriesClaimOpenSlot.get(0);

        mockMvc.perform(
                post("/api/categories/" + category.getId() + "/entries/" + entry.getId()
                        + "/process/claim")
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk());

        assertTrue("review was not created", reviewService.getReviewerIdsByEntryId(entry.getId()).stream()
                .anyMatch(string -> string.equals(userId.toString())));
    }

    @Test
    @Order(1)
    void notifyOpenSlots() throws Exception {
        mockMvc.perform(
                post("/api/categories/" + category.getId() + "/entries/" + userDrpRequests.getEntryId()
                        + "/process/notify")
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk());
    }
}