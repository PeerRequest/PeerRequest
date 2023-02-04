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
    @GetMapping(value = "/categories/{category_id}/entries/{entry_id}/process", produces = "application/json")
    public DirectRequestProcess.Dto getDirectRequestProcess(@PathVariable("entry_id") final long entryId) {
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
    @PostMapping(value = "/categories/{category_id}/entries/{entry_id}/process", produces = "application/json")
    public DirectRequestProcess.Dto createDirectRequestProcess(@PathVariable("entry_id") Long entryId,
                                                        @RequestBody DirectRequestProcess.Dto dto,
                                                        @AuthenticationPrincipal OAuth2User user) {
        if (this.entryService.get(entryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

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
        return this.directRequestProcessService.create(directRequestProcessDto.toDto()).toDto();
    }

    /**
     * Patches a directRequestProcess.
     *
     * @param entryId entry of the directRequestProcess
     * @param dto updater containing the amount of openSlots (int)
     *
     * @return updated directRequestProcess
     */
    @PutMapping(value = "/categories/{category_id}/entries/{entry_id}/process", produces = "application/json")
    public DirectRequestProcess.Dto patchDirectRequestProcess(@PathVariable("entry_id") final long entryId,
                                                          @RequestBody final DirectRequestProcess.Dto dto,
                                                          @AuthenticationPrincipal OAuth2User user) {
        if (this.entryService.get(entryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        String researcherId = this.entryService.get(entryId).get().getResearcherId();

        if (!user.getAttribute("sub").toString().equals(researcherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the user that created the entry may create a request process");
        }

        if (dto.openSlots().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "open slots must be given");
        }

        if (dto.openSlots().get() < 0) {
            throw new ResponseStatusException(HttpStatus.I_AM_A_TEAPOT, "open slots value must be greater than 0");
        }

        var option = this.directRequestProcessService.update(entryId, dto);
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "direct request process does not exist");
        }

        return option.get().toDto();
    }

    // DirectRequest

    /**
     * Gets a directRequests.
     *
     * @param requestId request to get
     *
     * @return directRequest with requestId
     */
    @GetMapping(value = "/categories/{category_id}/entries/{entry_id}/process/requests/{request_id}",
            produces = "application/json")
    public DirectRequest.Dto getDirectRequest(@PathVariable("request_id") final long requestId) {
            var option = this.directRequestService.get(requestId);
            if (option.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "direct request process does not exist");
            }

            return option.get().toDto();
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
    @PatchMapping(value = "/categories/{category_id}/entries/{entry_id}/process/requests/{request_id}",
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
    @PutMapping(value = "/categories/{category_id}/entries/{entry_id}/process/requests",
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
    @PostMapping(value = "/categories/{category_id}/entries/{entry_id}/process/claim")
    public DirectRequest claimOpenSlot(@PathVariable final long categoryId,
                                       @PathVariable final long entryId,
                                       @RequestBody final String userId) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

}