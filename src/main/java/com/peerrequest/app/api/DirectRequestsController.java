package com.peerrequest.app.api;

import com.peerrequest.app.data.DirectRequest;
import com.peerrequest.app.data.DirectRequestProcess;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


/**
 * The RequestsController handles all directRequests interaction with the backend.
 */
@RestController
@ApiControllerPrefix
public class DirectRequestsController extends ServiceBasedController {

    /**
     * Gets a directRequestProcess.
     *
     * @param entryId entry of the directRequestProcess
     *
     * @return directRequestProcess of entry
     */
    @GetMapping(value = "/categories/{categoryId}/entries/{entryId}/process", produces = "application/json")
    public DirectRequestProcess.Dto getDirectRequestProcess(@PathVariable final long entryId) {
        var option = this.directRequestProcessService.get(entryId);
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "direct request process does not exist");
        }

        return option.get().toDto();
    }

    /**
     * Creates a directRequestProcess.
     *
     * @param entryId entry of the directRequestProcess
     * @param dto containing the open slots
     *
     * @return the added directRequestProcess
     */
    @PostMapping(value = "/categories/{categoryId}/entries/{entryId}/process", produces = "application/json")
    public DirectRequestProcess createDirectRequestProcess(@PathVariable Long entryId,
                                                        @RequestBody DirectRequestProcess.Dto dto,
                                                        @AuthenticationPrincipal OAuth2User user) {
        if (dto.openSlots().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "open slots is required");
        }

        String researcherId = this.entryService.get(entryId).get().getResearcherId();

        if (!user.getAttribute("sub").toString().equals(researcherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the user that created the entry may create a request process");
        }
        // TODO: check if there is an existent Bidding process with the entry
        var directRequestProcessDto = DirectRequestProcess.fromDto(dto, entryId);
        return this.directRequestProcessService.create(directRequestProcessDto.toDto());
    }

    /**
     * Patches a directRequestProcess.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the directRequestProcess
     * @param updater updater containing the amount of openSlots (int)
     *
     * @return updated directRequestProcess
     */
    @PatchMapping(value = "/categories/{categoryId}/entries/{entryId}/process", produces = "application/json")
    public DirectRequestProcess patchDirectRequestProcess(@PathVariable final long categoryId,
                                                          @PathVariable final long entryId,
                                                          @RequestBody final DirectRequest.Dto updater) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    // DirectRequest

    /**
     * Gets a directRequests.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the directRequestProcess
     * @param requestId request to get
     *
     * @return directRequest with requestId
     */
    @GetMapping(value = "/categories/{categoryId}/entries/{entryId}/process/requests/{requestId}",
            produces = "application/json")
    public DirectRequest getDirectRequest(@PathVariable final long categoryId,
                                          @PathVariable final long entryId,
                                          @PathVariable final long requestId) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    /**
     * Patches a directRequest.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the DirectRequestProcess
     * @param requestId DirectRequest to patch
     * @param updater updater containing a DirectRequest.State
     *
     * @return the updated request
     */
    @PatchMapping(value = "/categories/{categoryId}/entries/{entryId}/process/requests/{requestId}",
            produces = "application/json")
    public DirectRequest patchDirectRequest(@PathVariable final long categoryId,
                                            @PathVariable final long entryId,
                                            @PathVariable final long requestId,
                                            @RequestBody final DirectRequest.Dto updater) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    /**
     * Creates a new directRequest.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the directRequestProcess
     * @param request request to add
     *
     * @return the added request
     */
    @PutMapping(value = "/categories/{categoryId}/entries/{entryId}/process/requests",
            produces = "application/json")
    public DirectRequest putDirectRequest(@PathVariable final long categoryId,
                                          @PathVariable final long entryId,
                                          @RequestBody final DirectRequest request) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }


    /**
     * Claims an open slot.
     *
     * @param categoryId category of the entry
     * @param entryId entry of the directRequestProcess
     * @param userId user who claims open slot
     *
     * @return directRequest with state=ACCEPTED
     */
    @PostMapping(value = "/categories/{categoryId}/entries/{entryId}/process/claim")
    public DirectRequest claimOpenSlot(@PathVariable final long categoryId,
                                       @PathVariable final long entryId,
                                       @RequestBody final String userId) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

}