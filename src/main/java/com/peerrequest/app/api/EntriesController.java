package com.peerrequest.app.api;

import com.peerrequest.app.data.Category;
import com.peerrequest.app.data.Document;
import com.peerrequest.app.data.Entry;
import com.peerrequest.app.data.Paged;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
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
    Paged<List<Entry.Dto>> listEntries(@RequestParam("limit") Optional<Integer> limit,
                                       @RequestParam("page") Optional<Integer> page,
                                       @PathVariable("category_id") Long categoryId) {
        if (limit.isPresent()) {
            if (limit.get() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be greater than 0");
            }
            limit = Optional.of(Math.min(limit.get(), maxPageSize));
        }

        Entry.Dto filterEntry = new Entry.Dto(
            null, null, null, null, null, Optional.of(categoryId));

        var entryPage = this.entryService.list(page.map(p -> p - 1).orElse(0), limit.orElse(maxPageSize),
            filterEntry);
        return new Paged<>(
            entryPage.getSize(),
            entryPage.getNumber() + 1,
            entryPage.getTotalPages(),
            entryPage.stream().map(Entry::toDto).toList());
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
    ResponseEntity<byte[]> getPaper(@PathVariable(name = "entry_id") Long id,
                                    @AuthenticationPrincipal OAuth2User user) {
        var option = this.entryService.get(id);
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        // TODO: Allow request if user is reviewing the paper
        if (!user.getAttribute("sub").toString().equals(option.get().getResearcherId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "only the reviewer and the researcher may view the paper");
        }

        var document = this.documentService.get(option.get().getDocumentId());

        HttpHeaders headers = new HttpHeaders();

        headers.setContentType(MediaType.parseMediaType("application/pdf"));
        String filename = document.get().getName();

        headers.add("Content-Disposition", "inline; filename=" + filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(document.get().getFile(), headers, HttpStatus.OK);
    }

    @PostMapping("/categories/{category_id}/entries")
    Entry.Dto createEntries(@ModelAttribute("entry") Entry.Dto dto, @RequestParam("file") MultipartFile file,
                            @AuthenticationPrincipal OAuth2User user, @PathVariable("category_id") Long categoryId) {
        var category = this.categoryService.get(categoryId);
        if (category.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category does not exist");
        }

        if (category.get().getLabel() == Category.CategoryLabel.EXTERNAL
            && !category.get().getResearcherId().equals(user.getAttribute("sub"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                "only the owner may add an entry to an external category");
        }

        if (dto.name() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "name is required");
        }

        if (dto.documentId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "document_id must not be set");
        }

        String fileName = file.getOriginalFilename();
        String documentId;

        try {
            byte[] fileBytes = file.getBytes();
            Document stored = new Document(null, fileBytes, fileName);

            var document = this.documentService.create(stored.toDto());
            documentId = document.getId();
        } catch (Exception e) {
            throw new RuntimeException("Something went wrong");
        }

        var entry = Entry.fromDto(dto, user.getAttribute("sub"), categoryId, documentId);
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
                "only the owner may update the entry");
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
                "only the owner may delete the entry");
        }

        var deleted = this.entryService.delete(entryId);
        return deleted.map(Entry::toDto);
    }
}
