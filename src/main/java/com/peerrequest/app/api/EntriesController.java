package com.peerrequest.app.api;

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
    List<Entry.Dto> listEntries(@RequestParam Optional<Integer> limit, @RequestParam Optional<Long> after) {
        if (limit.isPresent()) {
            if (limit.get() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be greater than 0");
            }
            limit = Optional.of(Math.min(limit.get(), maxPageSize));
        }

        // TODO: Implement after EntryService is finished
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/categories/{category_id}/entries/{entry_id}")
    Entry.Dto getEntry(@PathVariable(name = "entry_id") Long id) {

        // TODO: Implement after EntryService is finished
        throw new RuntimeException("Not implemented yet");
    }

    @GetMapping("/categories/{category_id}/entries/{entry_id}/paper")
    void getPaper(@PathVariable(name = "entry_id") Long id) {
        // TODO: Implement after DocumentService is finished
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/categories/{category_id}/entries")
    Entry.Dto createEntries(@RequestBody List<Entry.Dto> dto,
                                  @AuthenticationPrincipal OAuth2User user) {
        // TODO: Implement after EntryService is finished
        throw new RuntimeException("Not implemented yet");
    }

    @PatchMapping("/categories/{category_id}/entries/{entry_id}")
    Entry.Dto patchEntries (@RequestBody Entry.Dto dto) {
        // TODO: Implement after EntryService is finished
        throw new RuntimeException("Not implemented yet");
    }
}
