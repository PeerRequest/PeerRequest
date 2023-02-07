package com.peerrequest.app.api;

import com.peerrequest.app.data.Category;
import com.peerrequest.app.data.Entry;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * The controller class for entries to encapsulate the functionalities of the corresponding
 * service and provide them as HTTP-endpoints.
 */
@RestController
@ApiControllerPrefix
public class EntriesController extends ServiceBasedController {

    private final int maxPageSize = 100;

    @GetMapping("/categories/{category_id}/entries")
    List<Entry.Dto> listEntries(@RequestParam Optional<Integer> limit,
                                @RequestParam Optional<Long> after,
                                @PathVariable("category_id") Long categoryId) {
        if (limit.isPresent()) {
            if (limit.get() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be greater than 0");
            }
            limit = Optional.of(Math.min(limit.get(), maxPageSize));
        }

        Entry.Dto filterEntry = new Entry.Dto(
                null, null, null, null, Optional.of(categoryId), null);

        return this.entryService.list(after.orElse(null), limit.orElse(maxPageSize), filterEntry)
                .stream().map(Entry::toDto).toList();
    }

    @GetMapping("/categories/{category_id}/entries/{entry_id}")
    Entry.Dto getEntry(@PathVariable(name = "entry_id") Long id) {
        var option = this.entryService.get(id);
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        return option.get().toDto();
    }

    @GetMapping("/categories/{category_id}/entries/{entry_id}/paper")
    void getPaper(@PathVariable(name = "entry_id") Long id) {
        // TODO: Implement after DocumentService is finished
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/categories/{category_id}/entries")
    Entry.Dto createEntries(@RequestBody Entry.Dto dto,
                            @AuthenticationPrincipal OAuth2User user, @PathVariable("category_id") Long categoryId) {
        var category = this.categoryService.get(categoryId);
        if (category.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category does not exist");
        }

        if (category.get().getLabel() == Category.CategoryLabel.EXTERNAL
                && !category.get().getResearcherId().equals(user.getAttribute("sub"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "only the owner may add an entry to external category");
        }

        if (dto.name() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }

        if (dto.documentId() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "document_id is required");
        }

        var entry = Entry.fromDto(dto, user.getAttribute("sub"), categoryId);
        return this.entryService.create(entry.toDto()).toDto();
    }

    @PatchMapping("/categories/{category_id}/entries")
    Entry.Dto patchEntries(@RequestBody Entry.Dto dto, @AuthenticationPrincipal OAuth2User user) {
        if (dto.id().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");
        }

        if (dto.researcherId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you may not change the researcher_id");
        }

        if (dto.documentId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you may not change the document_id");
        }

        var patchEntry = this.entryService.get(dto.id().get());

        if (patchEntry.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        if (!patchEntry.get().getResearcherId().equals(user.getAttribute("sub"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the user that created the entry may patch it");
        }

        var option = this.entryService.update(dto.id().get(), dto);

        return option.get().toDto();
    }

    @DeleteMapping("/categories/{category_id}/entries/{entry_id}")
    Optional<Entry.Dto> deleteEntry(@PathVariable("entry_id") Long entryId, @AuthenticationPrincipal OAuth2User user) {
        var option = this.entryService.get(entryId);
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        var researcherId = user.getAttribute("sub").toString();
        if (!option.get().getResearcherId().equals(researcherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the user that created the entry may delete it");
        }

        var deleted = this.entryService.delete(entryId);
        return deleted.map(Entry::toDto);
    }
}
