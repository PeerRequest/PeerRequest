package com.peerrequest.app.api;

import com.peerrequest.app.PeerRequestBackend;
import com.peerrequest.app.data.Category;
import com.peerrequest.app.data.Document;
import com.peerrequest.app.data.Entry;
import com.peerrequest.app.services.CategoryService;
import com.peerrequest.app.services.DocumentService;
import com.peerrequest.app.services.EntryService;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
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
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {"spring.mail.from=mail", "spring.load=false"})
@AutoConfigureMockMvc
@ContextConfiguration(classes = PeerRequestBackend.class)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_CLASS)
public class EntriesControllerTest {

    @MockBean
    ClientRegistrationRepository clientRegistrationRepository;

    @MockBean
    JavaMailSender mailSender;

    @Autowired
    private MockMvc mockMvc;
    private static Category category;
    private static List<Entry> entries;
    private static UUID userId = UUID.randomUUID();

    private static MockHttpSession session;

    @BeforeAll
    public static void setUp(@Autowired CategoryService categoryService, @Autowired EntryService entryService,
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
        category = categoryService.create(
                Category.builder()
                .name("Test Category")
                .year(2000)
                .label(Category.CategoryLabel.INTERNAL)
                .minScore(0)
                .maxScore(5)
                .researcherId(userId.toString())
                .build().toDto());
        int entriesSize = 200;
        String[] documentIds = new String[entriesSize];
        for(int i = 0; i < entriesSize; i++) {
            File file = ResourceUtils.getFile("classpath:loremipsum.pdf");
            byte[] fileBytes = FileUtils.readFileToByteArray(file);
            Document document = new Document(null, fileBytes, "Test Paper " + i);
            documentIds[i] = documentService.create(document.toDto()).getId();
        }
        // first 10 entries will be created by the current test user
        // 100 with authors, 100 without
        entries = new ArrayList<>();
        for (int i = 1; i <= entriesSize; i++) {
            var c = Entry.builder()
                    .researcherId((i <= 10 ? userId : UUID.randomUUID()).toString())
                    .name("Test Category " + i)
                    .authors(i > 5 && i <= entriesSize/2 + 5 ? null : "Alan Turing")
                    .documentId(documentIds[i-1])
                    .categoryId(category.getId())
                    .build();
            entries.add(entryService.create(c.toDto()));
        }
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void listCategories() throws Exception {
        var action = mockMvc.perform(
                        get("/api/categories/" + category.getId() + "/entries")
                                .session(session)
                                .secure(true))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page_size").value(100))
                .andExpect(jsonPath("$.current_page").value(1))
                .andExpect(jsonPath("$.last_page").value(2))
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(100)));

        List<Entry> list = entries.stream().limit(100).toList();
        for (int i = 0; i < list.size(); i++) {
            Entry e = list.get(i);

            action.andExpect(jsonPath("$.content[" + i + "].id").value(e.getId()));
            action.andExpect(jsonPath("$.content[" + i + "].researcher_id").value(e.getResearcherId()));
            action.andExpect(jsonPath("$.content[" + i + "].name").value(e.getName()));
            action.andExpect(jsonPath("$.content[" + i + "].document_id").value(e.getDocumentId()));
            action.andExpect(jsonPath("$.content[" + i + "].category_id").value(e.getCategoryId()));
        }
    }
}
