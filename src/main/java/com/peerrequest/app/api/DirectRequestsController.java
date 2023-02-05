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
    public DirectRequestProcess.Dto getDirectRequestProcess(@PathVariable("entry_id") final Long entryId) {
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
    public DirectRequestProcess.Dto patchDirectRequestProcess(@PathVariable("entry_id") final Long entryId,
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

    /**
     * Gets a directRequests.
     *
     * @param requestId request to get
     *
     * @return directRequest with requestId
     */
    @GetMapping(value = "/categories/{category_id}/entries/{entry_id}/process/requests/{request_id}",
            produces = "application/json")
    public DirectRequest.Dto getDirectRequest(@PathVariable("entry_id") final Long entryId,
                                              @PathVariable("request_id") final Long requestId,
                                              @AuthenticationPrincipal OAuth2User user) {
        if (this.entryService.get(entryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        var option = this.directRequestService.get(requestId);
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "direct request process does not exist");
        }

        String researcherId = this.entryService.get(entryId).get().getResearcherId();
        String reviewerId = option.get().getReviewerId();
        if (!user.getAttribute("sub").toString().equals(researcherId)
                || user.getAttribute("sub").toString().equals(reviewerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the researcher or reviewer may get the request");
        }
        return option.get().toDto();
    }

    /**
     * Creates a new directRequest.
     *
     * @param entryId entry of the directRequestProcess
     * @param dto request to add
     *
     * @return the added request
     */
    @PostMapping(value = "/categories/{category_id}/entries/{entry_id}/process/requests",
            produces = "application/json")
    public DirectRequest.Dto createDirectRequest(@PathVariable("entry_id") final Long entryId,
                                                 @RequestBody final DirectRequest.Dto dto,
                                                 @AuthenticationPrincipal OAuth2User user) {
        if (this.entryService.get(entryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        String researcherId = this.entryService.get(entryId).get().getResearcherId();

        if (!user.getAttribute("sub").toString().equals(researcherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the user that created the entry may create a request");
        }

        var directRequestProcess = this.directRequestProcessService.get(entryId);
        if (directRequestProcess.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "direct request process does not exist");
        }

        if (dto.id().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id must not not be set");
        }

        if (dto.directRequestProcessId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "process id must not not be set");
        }

        if (dto.state().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "state must not not be set");
        }

        if (dto.reviewerId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "reviewer must be set");
        }

        var directRequest = DirectRequest.fromDto(dto, dto.reviewerId().get(), DirectRequest.RequestState.PENDING);
        return this.directRequestService.create(directRequest.toDto()).toDto();
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
    public DirectRequest.Dto patchDirectRequest(@PathVariable final long categoryId,
                                            @PathVariable final long entryId,
                                            @PathVariable final long requestId,
                                            @RequestBody final DirectRequest.Dto updater) {
        // TODO: implement
        throw new RuntimeException("Not implemented");
    }

    /**
     * Claims an open slot.
     *
     * @param entryId entry of the directRequestProcess
     * @param user user who claims open slot
     *
     * @return directRequest with state=ACCEPTED
     */
    @PostMapping(value = "/categories/{category_id}/entries/{entry_id}/process/claim")
    public DirectRequest.Dto claimOpenSlot(@PathVariable("entry_id") final long entryId,
                                       @AuthenticationPrincipal OAuth2User user) {
        if (this.entryService.get(entryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        var directRequestProcess = this.directRequestProcessService.get(entryId);
        if (directRequestProcess.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "direct request process does not exist");
        }

        String researcherId = this.entryService.get(entryId).get().getResearcherId();
        if (user.getAttribute("sub").toString().equals(researcherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "researcher of the entry can not be the reviewer");
        }

        if (directRequestProcess.get().toDto().openSlots().get() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "no empty slots available");
        }

        DirectRequestProcess updatedDirectRequestProcess = new DirectRequestProcess(directRequestProcess.get().getId(),
                directRequestProcess.get().getEntryId(), directRequestProcess.get().getOpenSlots() - 1);
        directRequestProcessService.update(directRequestProcess.get().getId(), updatedDirectRequestProcess.toDto());

        var reviewerId = user.getAttribute("sub").toString();
        DirectRequest directRequestObject = new DirectRequest(null, DirectRequest.RequestState.ACCEPTED,
                reviewerId, directRequestProcess.get().getId());
        return this.directRequestService.create(directRequestObject.toDto()).toDto();
    }
}