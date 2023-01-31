package com.peerrequest.app.api;

import com.peerrequest.app.data.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;


/**
 * The controller class for categories to encapsulate the functionalities of the corresponding
 * service and provide them as HTTP-endpoints.
 */
@RestController
@ApiControllerPrefix
public class CategoriesController extends ServiceBasedController {
    private final int maxPageSize = 100;

    @GetMapping("/categories")
    List<Category.Dto> listCategories(@RequestParam Optional<Integer> limit, @RequestParam Optional<Long> after) {
        if (limit.isPresent()) {
            if (limit.get() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be greater than 0");
            }
            limit = Optional.of(Math.min(limit.get(), maxPageSize));
        }

        return this.categoryService.list(after.orElse(null), limit.orElse(maxPageSize), null).stream()
            .map(Category::toDto).toList();
    }
}
