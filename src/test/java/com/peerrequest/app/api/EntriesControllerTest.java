package com.peerrequest.app.api;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.util.AssertionErrors.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.peerrequest.app.PeerRequestBackend;
import com.peerrequest.app.data.Category;
import com.peerrequest.app.data.Document;
import com.peerrequest.app.data.Entry;
import com.peerrequest.app.services.CategoryService;
import com.peerrequest.app.services.DocumentService;
import com.peerrequest.app.services.EntryService;
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
public class EntriesControllerTest {

    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    JavaMailSender mailSender;

    @Autowired
    private MockMvc mockMvc;
    private static Category internalCategory;
    private static Category externalCategory;
    private static Document.Dto document;
    private static final List<Entry> internalEntries = new ArrayList<>();
    private static UUID userId = UUID.randomUUID();

    private static MockHttpSession session;

    private static final List<Entry> totalUserEntries = new ArrayList<>();
    private static final int internalUserEntriesSize = 10;
    @BeforeAll
    static void setUp(@Autowired CategoryService categoryService, @Autowired EntryService entryService,
                             @Autowired DocumentService documentService, @Autowired MockMvc mockMvc) throws Exception {
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
        internalCategory = categoryService.create(
                Category.builder()
                        .name("Test internal Category Entry")
                        .year(2000)
                        .label(Category.CategoryLabel.INTERNAL)
                        .minScore(0)
                        .maxScore(5).scoreStepSize(1)
                        .researcherId(userId.toString())
                        .build().toDto());
        externalCategory = categoryService.create(
                Category.builder()
                        .name("Test external Category Entry")
                        .year(2000)
                        .label(Category.CategoryLabel.EXTERNAL)
                        .minScore(0)
                        .maxScore(5).scoreStepSize(1)
                        .researcherId(UUID.randomUUID().toString())
                        .build().toDto());
        File file = ResourceUtils.getFile("classpath:loremipsum.pdf");
        document = new Document.Dto(Optional.empty(), Optional.of(FileUtils.readFileToByteArray(file)),
                Optional.of("loremipsum"));

        int internalEntriesSize = 210;
        // first 10 entries will be created by the current test user
        // 105 with authors, 105 without
        for (int i = 1; i <= internalEntriesSize; i++) {
            var e = Entry.builder()
                    .researcherId(i <= internalUserEntriesSize ? userId.toString() : UUID.randomUUID().toString())
                    .name("Test internal Entry " + i)
                    .authors(i % 2 == 1 ? null : "Alan Turing")
                    .documentId(documentService.create(document).getId())
                    .categoryId(internalCategory.getId())
                    .build();
            var entry = entryService.create(e.toDto());
            internalEntries.add(entry);
            if (i <= internalUserEntriesSize) {
                totalUserEntries.add(entry);
            }
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test()
    @Order(1)
    void listEntries() throws Exception {
        var action = mockMvc.perform(
                        get("/api/categories/" + internalCategory.getId() + "/entries")
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(EntriesController.maxPageSize))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(3))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(EntriesController.maxPageSize)));

        List<Entry> list = internalEntries.stream().limit(EntriesController.maxPageSize).toList();
        for (int i = 0; i < list.size(); i++) {
            Entry e = list.get(i);

            action.andExpect(jsonPath("$.content[" + i + "].id").value(e.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].researcher_id").value(e.getResearcherId()));
            action.andExpect(jsonPath("$.content[" + i + "].name").value(e.getName()));
            action.andExpect(jsonPath("$.content[" + i + "].authors").value(e.getAuthors()));
            action.andExpect(jsonPath("$.content[" + i + "].document_id").value(e.getDocumentId()));
            action.andExpect(jsonPath("$.content[" + i + "].category_id").value(e.getCategoryId()));
        }
    }

    @Test()
    @Order(1)
    void listEntriesFailWithLimit() throws Exception {
        mockMvc.perform(
                get("/api/categories/" + internalCategory.getId() + "/entries")
                        .session(session)
                        .secure(true)
                        .param("limit", String.valueOf(0)))
                .andExpect(status().isBadRequest());
    }

    @Test()
    @Order(1)
    void listEntriesWithLimit() throws Exception {
        int limit = 5;
        var action = mockMvc.perform(
                        get("/api/categories/" + internalCategory.getId() + "/entries")
                                .session(session)
                                .secure(true)
                                .param("limit", String.valueOf(limit)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(limit))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(internalEntries.size() / limit))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(limit)));

        List<Entry> list = internalEntries.stream().limit(limit).toList();
        for (int i = 0; i < list.size(); i++) {
            Entry e = list.get(i);

            action.andExpect(jsonPath("$.content[" + i + "].id").value(e.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].researcher_id").value(e.getResearcherId()));
            action.andExpect(jsonPath("$.content[" + i + "].name").value(e.getName()));
            action.andExpect(jsonPath("$.content[" + i + "].authors").value(e.getAuthors()));
            action.andExpect(jsonPath("$.content[" + i + "].document_id").value(e.getDocumentId()));
            action.andExpect(jsonPath("$.content[" + i + "].category_id").value(e.getCategoryId()));
        }
    }
    @Test
    @Order(1)
    void getEntry() throws Exception {
        Entry e = totalUserEntries.get(ThreadLocalRandom.current().nextInt(0, totalUserEntries.size()));

        var action = mockMvc.perform(
                        get("/api/categories/" + internalCategory.getId() + "/entries/" + e.getId())
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk());

        action.andExpect(jsonPath("$.id").value(e.getId()));
        action.andExpect(jsonPath("$.researcher_id").value(e.getResearcherId()));
        action.andExpect(jsonPath("$.name").value(e.getName()));
        action.andExpect(jsonPath("$.authors").value(e.getAuthors()));
        action.andExpect(jsonPath("$.document_id").value(e.getDocumentId()));
        action.andExpect(jsonPath("$.category_id").value(e.getCategoryId()));
    }

    @Test
    @Order(1)
    void getPaper(@Autowired DocumentService documentService) throws Exception {
        Entry e = totalUserEntries.get(ThreadLocalRandom.current().nextInt(0, totalUserEntries.size()));
        Document d = documentService.get(e.getDocumentId()).get();
        var action = mockMvc.perform(
                        get("/api/categories/" + internalCategory.getId() + "/entries/" + e.getId() + "/paper")
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk());

        action.andExpect(content().bytes(d.getFile()));
    }

    @Test
    @Order(2)
    void createEntries() throws Exception {
        String authors = "Alan Turing";
        String name = "Test Entry Post";

        MockMultipartFile document = new MockMultipartFile("file", "loremipsum.pdf",
                "application/pdf", FileUtils.readFileToByteArray(ResourceUtils.getFile("classpath:loremipsum.pdf")));
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/categories/" + internalCategory.getId() + "/entries")
                        .file(document)
                        .param("authors", authors)
                        .param("name", name)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").isNotEmpty())
                .andExpect(jsonPath("$.researcher_id").value(userId.toString()))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.authors").value(authors))
                .andExpect(jsonPath("$.document_id").isNotEmpty())
                .andExpect(jsonPath("$.category_id").value(internalCategory.getId()));
    }

    @Test
    @Order(2)
    void createEntriesFailCategoryId() throws Exception {
        String authors = "Alan Turing";
        String name = "Test Entry Post";
        MockMultipartFile document = new MockMultipartFile("file", "loremipsum.pdf",
                "application/pdf", FileUtils.readFileToByteArray(ResourceUtils.getFile("classpath:loremipsum.pdf")));
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/categories/" + -1L + "/entries")
                        .file(document)
                        .param("authors", authors)
                        .param("name", name)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .session(session)
                        .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    void createEntriesFailExternalNotResearcher() throws Exception {
        String authors = "Alan Turing";
        String name = "Test Entry Post";
        MockMultipartFile document = new MockMultipartFile("file", "loremipsum.pdf",
                "application/pdf", FileUtils.readFileToByteArray(ResourceUtils.getFile("classpath:loremipsum.pdf")));
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/categories/" + externalCategory.getId() + "/entries")
                        .file(document)
                        .param("authors", authors)
                        .param("name", name)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .session(session)
                        .secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    void createEntriesFailNoName() throws Exception {
        String authors = "Alan Turing";
        MockMultipartFile document = new MockMultipartFile("file", "loremipsum.pdf",
                "application/pdf", FileUtils.readFileToByteArray(ResourceUtils.getFile("classpath:loremipsum.pdf")));
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/categories/" + internalCategory.getId() + "/entries")
                        .file(document)
                        .param("authors", authors)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void createEntriesFailDocumentIdSet() throws Exception {
        String name = "Test Entry Post";
        String documentId = "documentId";
        MockMultipartFile document = new MockMultipartFile("file", "loremipsum.pdf",
                "application/pdf", FileUtils.readFileToByteArray(ResourceUtils.getFile("classpath:loremipsum.pdf")));
        mockMvc.perform(MockMvcRequestBuilders
                        .multipart("/api/categories/" + internalCategory.getId() + "/entries")
                        .file(document)
                        .param("name", name)
                        .param("documentId", documentId)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }



    @Test
    @Order(2)
    void patchEntries() throws Exception {
        Entry e = internalEntries.get(ThreadLocalRandom.current().nextInt(0, internalUserEntriesSize));
        String newName = "new name";
        String newAuthors = "new authors";
        JSONObject patch = new JSONObject();
        patch.put("id", e.getId());
        patch.put("name", newName);
        patch.put("authors", newAuthors);

        var action = mockMvc.perform(
                        patch("/api/categories/" + internalCategory.getId() + "/entries")
                                .content(patch.toString())
                                .contentType(MediaType.APPLICATION_JSON)
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk());

        action.andExpect(jsonPath("$.id").value(e.getId()));
        action.andExpect(jsonPath("$.researcher_id").value(e.getResearcherId()));
        action.andExpect(jsonPath("$.name").value(newName));
        action.andExpect(jsonPath("$.authors").value(newAuthors));
        action.andExpect(jsonPath("$.document_id").value(e.getDocumentId()));
        action.andExpect(jsonPath("$.category_id").value(e.getCategoryId()));
    }

    @Test
    @Order(2)
    void patchEntriesFailNoId() throws Exception {
        JSONObject patch = new JSONObject();
        patch.put("name", "new name");
        patch.put("authors", "new authors");

        mockMvc.perform(
                patch("/api/categories/" + internalCategory.getId() + "/entries")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchEntriesFailResearcherIdSet() throws Exception {
        JSONObject patch = new JSONObject();
        patch.put("researcherId", "reseracherId");
        patch.put("name", "new name");
        patch.put("authors", "new authors");

        mockMvc.perform(
                patch("/api/categories/" + internalCategory.getId() + "/entries")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchEntriesFailDocumentIdSet() throws Exception {
        JSONObject patch = new JSONObject();
        patch.put("documentId", "documentId");
        patch.put("name", "new name");
        patch.put("authors", "new authors");

        mockMvc.perform(
                patch("/api/categories/" + internalCategory.getId() + "/entries")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchEntriesFailBadEntryId() throws Exception {
        JSONObject patch = new JSONObject();
        patch.put("Id", "entryId");
        patch.put("name", "new name");
        patch.put("authors", "new authors");

        mockMvc.perform(
                patch("/api/categories/" + internalCategory.getId() + "/entries")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isBadRequest());
    }

    @Test
    @Order(2)
    void patchEntriesFailNotResearcher() throws Exception {
        JSONObject patch = new JSONObject();
        patch.put("id", internalEntries.get(internalEntries.size() - 1).getId());
        patch.put("name", "new name");
        patch.put("authors", "new authors");

        mockMvc.perform(
                patch("/api/categories/" + internalCategory.getId() + "/entries")
                        .content(patch.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .session(session)
                        .secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(2)
    void deleteEntry(@Autowired EntryService entryService,
                     @Autowired DocumentService documentService) throws Exception {
        File file = ResourceUtils.getFile("classpath:loremipsum.pdf");
        var documentId = documentService.create(new Document.Dto(Optional.empty(),
                Optional.of(FileUtils.readFileToByteArray(file)), Optional.of("loremipsum"))).getId();
        Entry e = entryService.create(new Entry.Dto(Optional.empty(), Optional.of(userId.toString()), "Test delete",
                Optional.empty(), Optional.of(documentId), Optional.of(internalCategory.getId())));

        mockMvc.perform(
                delete("/api/categories/" + internalCategory.getId() + "/entries/" + e.getId())
                        .session(session)
                        .secure(true))
                .andExpect(status().isOk());

        assertTrue("entry was not deleted", entryService.get(e.getId()).isEmpty());
    }

    @Test
    @Order(2)
    void deleteEntryFailBadEntryId() throws Exception {
        mockMvc.perform(
                delete("/api/categories/" + internalCategory.getId() + "/entries/" + -1L)
                        .session(session)
                        .secure(true))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(2)
    void deleteEntryFailNotResearcher() throws Exception {
        long entryId = internalEntries.get(internalEntries.size() - 1).getId();

        mockMvc.perform(
                delete("/api/categories/" + internalCategory.getId() + "/entries/" + entryId)
                        .session(session)
                        .secure(true))
                .andExpect(status().isForbidden());
    }

    @Test
    @Order(1)
    void listEntriesByResearcher() throws Exception {
        var action = mockMvc.perform(
                        get("/api/entries")
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(EntriesController.maxPageSize))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(1))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(totalUserEntries.size())));

        for (int i = 0; i < totalUserEntries.size(); i++) {
            Entry e = totalUserEntries.get(i);

            action.andExpect(jsonPath("$.content[" + i + "].id").value(e.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].researcher_id").value(e.getResearcherId()));
            action.andExpect(jsonPath("$.content[" + i + "].name").value(e.getName()));
            action.andExpect(jsonPath("$.content[" + i + "].authors").value(e.getAuthors()));
            action.andExpect(jsonPath("$.content[" + i + "].document_id").value(e.getDocumentId()));
            action.andExpect(jsonPath("$.content[" + i + "].category_id").value(e.getCategoryId()));
        }
    }

    @Test
    @Order(1)
    void listEntriesByResearchefr() throws Exception {
        var action = mockMvc.perform(
                        get("/api/entries")
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(EntriesController.maxPageSize))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(1))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(totalUserEntries.size())));
    }
}
