package com.peerrequest.app;

import com.peerrequest.app.data.*;
import com.peerrequest.app.data.repos.*;
import com.peerrequest.app.services.DocumentService;
import com.peerrequest.app.services.UserService;
import java.io.File;
import org.apache.commons.io.FileUtils;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.data.domain.Pageable;
import org.springframework.util.ResourceUtils;

/**
 * Main class for the PeerRequest Backend.
 */
@SpringBootApplication
public class  PeerRequestBackend {

    private static boolean loadMockData = false;

    /**
     * Main.
     *
     * @param args arguments
     */
    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(PeerRequestBackend.class, args);

        if (loadMockData) {
            mockData(context);
        }
    }

    private static void mockData(ConfigurableApplicationContext context) {


        CategoryRepository categoryRepository = context.getBean(CategoryRepository.class);
        if (categoryRepository.findAll().iterator().hasNext()) {
            return;
        }
        UserService userService = context.getBean(UserService.class);

        var users = userService.list(
            Pageable.ofSize(100)
                .withPage(0));
        String[] names = new String[6];
        String[] userIds = new String[6];
        int ind = 0;

        for (var user : users.stream().toList()) {
            if (user.firstName().equals("Helma")) {
                names[ind] = "Helma";
                userIds[ind] = user.getId();
                ind++;
                break;
            }
        }

        for (var user : users.stream().toList()) {
            if (user.firstName().equals("Veronika")) {
                names[ind] = "Veronika";
                userIds[ind] = user.getId();
                ind++;
            }
            if (user.firstName().equals("Rainer")) {
                names[ind] = "Ludger";
                userIds[ind] = user.getId();
                ind++;
            }
            if (user.firstName().equals("Nikola")) {
                names[ind] = "Nikola";
                userIds[ind] = user.getId();
                ind++;
            }
            if (user.firstName().equals("Jason")) {
                names[ind] = "Jason";
                userIds[ind] = user.getId();
                ind++;
            }
            if (user.firstName().equals("Michael")) {
                names[ind] = "Michael";
                userIds[ind] = user.getId();
                ind++;
            }
        }

        EntryRepository entryRepository = context.getBean(EntryRepository.class);
        DocumentService documentService = context.getBean(DocumentService.class);
        DirectRequestProcessRepository directRequestProcessRepository
            = context.getBean(DirectRequestProcessRepository.class);
        DirectRequestRepository directRequestRepository = context.getBean(DirectRequestRepository.class);
        ReviewRepository reviewRepository = context.getBean(ReviewRepository.class);

        String documentId = "";
        try {
            File file = ResourceUtils.getFile("classpath:loremipsum.pdf");
            byte[] fileBytes = FileUtils.readFileToByteArray(file);
            Document stored = new Document(null, fileBytes, "Mock Paper");

            var document = documentService.create(stored.toDto());
            documentId = document.getId();
        } catch (Exception ignored) {
            System.exit(1000);
        }

        for (int i = 1; i < names.length; i++) {
            long id = i;
            Category category = new Category(
                id, userIds[i], names[i] + "s Conference", 2023, Category.CategoryLabel.INTERNAL, null, 1, 5, 1);
            categoryRepository.save(category);


            Entry entryOne = new Entry(3 * id - 2, documentId, names[i] + " is Reviewer", null, userIds[0], id);
            Entry entryTwo = new Entry(3 * id - 1, documentId, names[i] + " is Researcher", null, userIds[i], id);
            Entry entryThree = new Entry(3 * id, documentId, names[i] + " is requested", null, userIds[0], id);
            entryRepository.save(entryOne);
            entryRepository.save(entryTwo);
            entryRepository.save(entryThree);

            DirectRequestProcess directRequestProcessOne = new DirectRequestProcess(3 * id - 2, 3 * id - 2, 3);
            DirectRequestProcess directRequestProcessTwo = new DirectRequestProcess(3 * id - 1, 3 * id - 1, 3);
            DirectRequestProcess directRequestProcessThree = new DirectRequestProcess(3 * id, 3 * id, 3);
            directRequestProcessRepository.save(directRequestProcessOne);
            directRequestProcessRepository.save(directRequestProcessTwo);
            directRequestProcessRepository.save(directRequestProcessThree);

            DirectRequest directRequestOne
                = new DirectRequest(3 * id - 2, DirectRequest.RequestState.ACCEPTED, userIds[i], 3 * id - 2);
            DirectRequest directRequestTwo
                = new DirectRequest(3 * id - 1, DirectRequest.RequestState.ACCEPTED, userIds[0], 3 * id - 1);
            DirectRequest directRequestThree
                = new DirectRequest(3 * id, DirectRequest.RequestState.PENDING, userIds[i], 3 * id);
            directRequestRepository.save(directRequestOne);
            directRequestRepository.save(directRequestTwo);
            directRequestRepository.save(directRequestThree);

            Review reviewOne = new Review(2 * id - 1,
                userIds[i], 3 * id - 2, null, Review.ConfidenceLevel.LOW, null, null, null, null, null, null, 1F);
            Review reviewTwo = new Review(2 * id,
                userIds[0], 3 * id - 1, null, Review.ConfidenceLevel.LOW, null, null, null, null, null, null, 1F);
            reviewRepository.save(reviewOne);
            reviewRepository.save(reviewTwo);
        }
    }
}
