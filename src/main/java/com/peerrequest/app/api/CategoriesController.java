package com.peerrequest.app.api;

import com.peerrequest.app.model.Category;
import com.peerrequest.app.services.CategoryService;
import java.util.List;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * The controller class for categories to encapsulate the functionalities of the corresponding
 * service and provide them as HTTP-endpoints.
 */
@RestController
@ApiControllerPrefix
public class CategoriesController {

    private final CategoryService categoryService;

    public CategoriesController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }

    @GetMapping(value = "/categories", produces = "application/json")
    public List<Category> getCategories() {
        return categoryService.findAll();
    }

    @GetMapping(value = "/categories/{categoryId}", produces = "application/json")
    public Category getCategory(@PathVariable final Long categoryId) {
        return categoryService.getReferenceById(categoryId);
    }

    @PostMapping(value = "/categories", produces = "application/json")
    public Category createCategory(@RequestBody final Category category) {
        return categoryService.saveAndFlush(category);
    }

    @PatchMapping(value = "/categories/{categoryId}", consumes = "application/json", produces = "application/json")
    public Category patchCategory(@RequestBody final Category category) {
        throw new RuntimeException("Not Implemented");
    }

    @DeleteMapping(value = "categories/{categoryId}/", consumes = "application/json")
    public void deleteCategory(@PathVariable final int categoryId) {
        throw new RuntimeException("Not Implemented");
    }

}
