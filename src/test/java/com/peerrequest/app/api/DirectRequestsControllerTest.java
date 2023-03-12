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
public class DirectRequestsControllerTest {

    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    JavaMailSender mailSender;

    @Autowired
    private MockMvc mockMvc;
    private static Category category;
    //for getSpecificRequests()
    private static List<Entry> entries = new ArrayList<>();
    private static List<DirectRequestProcess> drps;
    private static List<DirectRequest> userRequestsOthers = new ArrayList<>();
    private static UUID userId = UUID.randomUUID();

    private static MockHttpSession session;

    // should be multiple of four
    private static int currentUserEntrySize = 20;

    private static Entry userEntryRequests;
    private static Entry userEntryCreateDrp;
    private static DirectRequestProcess userDrpRequests;

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
                        .name("Test Category")
                        .year(2000)
                        .label(Category.CategoryLabel.INTERNAL)
                        .minScore(0)
                        .maxScore(5)
                        .researcherId(userId.toString())
                        .build().toDto());

        File file = ResourceUtils.getFile("classpath:loremipsum.pdf");
        Document.Dto document = new Document.Dto(Optional.empty(), Optional.of(FileUtils.readFileToByteArray(file)),
                Optional.of("loremipsum"));


        // create entry, drp, and 120 Request (40 each state, random open slots 0-10) for currently signed-in user
        // to list the requests
        userEntryRequests = entryService.create(
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
                        .openSlots(ThreadLocalRandom.current().nextInt(0, 11))
                        .build().toDto()).toDto());

        for (int i = 0; i < 120; i++) {
            var r = directRequestService.create(
                    DirectRequest.builder()
                            .state(i < 40 ? DirectRequest.RequestState.PENDING
                                    : (i < 80 ? DirectRequest.RequestState.ACCEPTED
                                    : DirectRequest.RequestState.DECLINED))
                            .reviewerId(UUID.randomUUID().toString())
                            .directRequestProcessId(userDrpRequests.getId())
                            .build().toDto());
            userRequestsOthers.add(r);
        }

        // create entry, drp of user with 9 requests (each state 3 times) - to delete a request
        var userEntryDelete = entryService.create(Entry.builder()
                .researcherId(userId.toString())
                .name("User Entry Delete")
                .authors("Alan Turing")
                .documentId(documentService.create(document).getId())
                .categoryId(category.getId())
                .build().toDto());
        entries.add(userEntryDelete);

        var userEntryDeleteDrp = drpService.create(
                DirectRequestProcess.builder()
                        .entryId(userEntryDelete.getId())
                        .openSlots(ThreadLocalRandom.current().nextInt(0, 11))
                        .build().toDto());
        drpService.create(userDrpRequests.toDto());

        for (int i = 0; i < 9; i++) {
            var r = directRequestService.create(
                    DirectRequest.builder()
                            .state(i < 3 ? DirectRequest.RequestState.PENDING
                                    : (i < 6 ? DirectRequest.RequestState.ACCEPTED
                                    : DirectRequest.RequestState.DECLINED))
                            .reviewerId(UUID.randomUUID().toString())
                            .directRequestProcessId(userEntryDeleteDrp.getId())
                            .build().toDto()
            );
        }


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


        // create three entries, drps with open slot but no requests for currently signed-in user - to claim open slots
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

            var p = drpService.create(
                    DirectRequestProcess.builder()
                            .entryId(e.getId())
                            .openSlots(10)
                            .build().toDto());
        }


        // create nine entries, drps with one requests each (first three drp has pending, second three accepted,
        // third three declined) - to patch direct requests
        for (int i = 0; i < 9; i++) {

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

            var r = directRequestService.create(
                    DirectRequest.builder()
                            .state(i < 3 ? DirectRequest.RequestState.PENDING
                                    : (i < 6 ? DirectRequest.RequestState.ACCEPTED
                                    : DirectRequest.RequestState.DECLINED))
                            .reviewerId(userId.toString())
                            .directRequestProcessId(p.getId())
                            .build().toDto()
            );
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
        DirectRequest request = userRequestsOthers.get(
                ThreadLocalRandom.current().nextInt(0, userRequestsOthers.size()));
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
    void deleteDirectRequest() throws Exception {

    }

    @Test
    @Order(1)
    void listDirectRequestsByEntry() throws Exception {

    }

    @Test
    @Order(2)
    void postDirectRequest() throws Exception {

    }

    @Test
    @Order(2)
    void patchDirectRequest() throws Exception {

    }

    @Test
    @Order(1)
    void listRequestsByResearcher() throws Exception {

    }

    @Test
    @Order(1)
    void getSpecificRequests() throws Exception {

    }

    @Test
    @Order(2)
    void claimOpenSlot() throws Exception {

    }
}