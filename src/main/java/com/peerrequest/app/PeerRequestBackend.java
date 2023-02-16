package com.peerrequest.app;

import com.peerrequest.app.data.Category;
import com.peerrequest.app.data.repos.CategoryRepository;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Main class for the PeerRequest Backend.
 */
@SpringBootApplication
public class  PeerRequestBackend {

    private static boolean loadMockData = true;

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(PeerRequestBackend.class, args);

        mockData(context);
    }

    private static void mockData(ConfigurableApplicationContext context) {

        CategoryRepository categoryRepository = context.getBean(CategoryRepository.class);
        if (!loadMockData || categoryRepository.findAll().iterator().next() != null) {
            return;
        }

        Category michiCategory = new Category(
            1L,
            "4db0881d-e621-4577-85f6-37f72f560a92",
            "Michis Conference",
            2023,
            Category.CategoryLabel.INTERNAL,
            null,
            1,
            5,
            1);
        categoryRepository.save(michiCategory);
        var test1 = categoryRepository.findById(1L);
    }
}
