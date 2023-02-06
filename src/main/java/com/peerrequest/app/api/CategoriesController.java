package com.peerrequest.app.api;

import com.peerrequest.app.data.Category;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
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

    @GetMapping("/categories/{id}")
    Category.Dto getCategory(@PathVariable Long id) {
        var option = this.categoryService.get(id);
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category does not exist");
        }

        return option.get().toDto();
    }

    @DeleteMapping("/categories/{id}")
    Optional<Category.Dto> deleteCategory(@PathVariable Long id, @AuthenticationPrincipal OAuth2User user) {
        var option = this.categoryService.get(id);
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category does not exist");
        }
        if (option.get().getLabel() == Category.CategoryLabel.EXTERNAL
            && !option.get().getResearcherId().equals(user.getAttribute("sub"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the owner may delete an external category");
        }

        var deleted = this.categoryService.delete(id);
        return deleted.map(Category::toDto);
    }

    @PostMapping("/categories")
    Category.Dto createCategories(@RequestBody Category.Dto dto,
                                  @AuthenticationPrincipal OAuth2User user) {
        if (dto.id().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id must not not be set");
        }

        if (dto.researcherId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "researcher_id must not not be set");
        }

        var category = Category.fromDto(dto, user.getAttribute("sub"));
        return this.categoryService.create(category.toDto()).toDto();
    }

    @PatchMapping("/categories")
    Category.Dto patchCategories(@RequestBody Category.Dto dto, @AuthenticationPrincipal OAuth2User user) {
        if (dto.id().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");
        }

        if (dto.researcherId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you may not change the researcher_id");
        }

        var option = this.categoryService.get(dto.id().get());
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category does not exist");
        }
        if (option.get().getLabel() == Category.CategoryLabel.EXTERNAL
            && !option.get().getResearcherId().equals(user.getAttribute("sub"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the owner may alter an external category");
        }

        var patched = this.categoryService.update(dto.id().get(), dto);
        if (patched.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category does not exist");
        }

        return patched.get().toDto();
    }
}
