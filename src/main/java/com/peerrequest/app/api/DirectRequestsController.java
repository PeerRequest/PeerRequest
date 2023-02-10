package com.peerrequest.app.api;

import com.peerrequest.app.data.*;
import java.util.List;
import java.util.Optional;
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

    private final int maxPageSize = 100;

    /**
     * Gets a directRequestProcess.
     *
     * @param entryId entry of the directRequestProcess
     *
     * @return directRequestProcess of entry
     */
    @GetMapping(value = "/categories/{category_id}/entries/{entry_id}/process")
    public DirectRequestProcess.Dto getDirectRequestProcess(@PathVariable("entry_id") final Long entryId) {
        var option = this.directRequestProcessService.getByEntry(entryId);
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
    @PostMapping(value = "/categories/{category_id}/entries/{entry_id}/process")
    public DirectRequestProcess.Dto createDirectRequestProcess(@PathVariable("entry_id") Long entryId,
                                                        @RequestBody DirectRequestProcess.Dto dto,
                                                        @AuthenticationPrincipal OAuth2User user) {
        if (this.entryService.get(entryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        if (this.directRequestProcessService.getByEntry(entryId).isPresent()) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "process does already exist");
        }

        if (dto.openSlots().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "open slots is required");
        }

        String researcherId = this.entryService.get(entryId).get().getResearcherId();

        if (!user.getAttribute("sub").toString().equals(researcherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the user that created the entry may create a request process");
        }

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
    @PatchMapping(value = "/categories/{category_id}/entries/{entry_id}/process")
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
    @GetMapping(value = "/categories/{category_id}/entries/{entry_id}/process/requests/{request_id}")
    public DirectRequest.Dto getDirectRequest(@PathVariable("entry_id") final Long entryId,
                                              @PathVariable("request_id") final Long requestId,
                                              @AuthenticationPrincipal OAuth2User user) {
        if (this.entryService.get(entryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        var option = this.directRequestService.get(requestId);
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "direct request does not exist");
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

    @DeleteMapping(value = "/categories/{category_id}/entries/{entry_id}/process/requests/{request_id}")
    Optional<DirectRequest.Dto> deleteDirectRequest(@PathVariable("entry_id") final Long entryId,
                                                    @PathVariable("request_id") final Long requestId,
                                                    @AuthenticationPrincipal OAuth2User user) {
        var entry = this.entryService.get(entryId);
        if (entry.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        if (user.getAttribute("sub").toString().equals(entry.get().getResearcherId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the researcher may delete an request");
        }

        var request = this.directRequestService.get(requestId);

        if (request.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "direct request does not exist");
        }
        if (!request.get().getState().equals(DirectRequest.RequestState.PENDING)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "a request can only be deleted if it is pending");
        }

        this.directRequestService.delete(requestId);
    }

    /**
     * Gets the list of all requests of an entry.
     *
     * @return directRequest with requestId
     */
    @GetMapping(value = "/categories/{category_id}/entries/{entry_id}/process/requests")
    public Paged<List<DirectRequest.Dto>> listDirectRequestsByEntry(@RequestParam Optional<Integer> limit,
                                                                    @RequestParam Optional<Integer> page,
                                                                    @PathVariable("entry_id") final Long entryId,
                                                                    @AuthenticationPrincipal OAuth2User user) {
        if (this.entryService.get(entryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        String researcherId = this.entryService.get(entryId).get().getResearcherId();

        if (!user.getAttribute("sub").toString().equals(researcherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Only the user that created the entry may get all requests of the entry");
        }

        if (limit.isPresent()) {
            if (limit.get() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be greater than 0");
            }
            limit = Optional.of(Math.min(limit.get(), maxPageSize));
        }

        var option = this.directRequestProcessService.getByEntry(entryId);
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "direct request process does not exist");
        }

        DirectRequest filterDirectRequest = new DirectRequest(null, null, null, option.get().getId());

        var directRequestPage = this.directRequestService.list(page.map(p -> p - 1).orElse(0),
                limit.orElse(maxPageSize), filterDirectRequest.toDto());
        return new Paged<>(
            directRequestPage.getSize(),
            directRequestPage.getNumber() + 1,
            directRequestPage.getTotalPages(),
            directRequestPage.stream().map(DirectRequest::toDto).toList());
    }


    /**
     * Creates a directRequest with pending state.
     *
     * @param request dto with reviewer id
     * @return directRequest with id
     */
    @PostMapping(value = "/categories/{category_id}/entries/{entry_id}/process/requests")
    public DirectRequest.Dto postDirectRequest(@RequestBody final DirectRequest.Dto request,
                                               @PathVariable("entry_id") Long entryId,
                                               @AuthenticationPrincipal OAuth2User user) {

        if (this.entryService.get(entryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        String researcherId = this.entryService.get(entryId).get().getResearcherId();

        if (!user.getAttribute("sub").toString().equals(researcherId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only owner may post a request");
        }

        var process = this.directRequestProcessService.getByEntry(entryId);

        if (process.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "request process does not exist");
        }

        if (request.id().isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "id must not be set");
        }

        if (request.state().isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "state must not be set");
        }

        if (request.directRequestProcessId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "process id must not be set");
        }

        if (request.reviewerId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "reviewer id must be set");
        }

        var directRequest = new DirectRequest.Dto(Optional.empty(), Optional.of(DirectRequest.RequestState.PENDING),
                request.reviewerId(), process.get().toDto().id());

        return this.directRequestService.create(directRequest).toDto();
    }

    /**
     * Patches a directRequest.
     *
     * @param updater updater containing a DirectRequest.State
     *
     * @return the updated request
     */
    @PatchMapping(value = "/categories/{category_id}/entries/{entry_id}/process/requests")
    public DirectRequest.Dto patchDirectRequest(@RequestBody final DirectRequest.Dto updater,
                                                @AuthenticationPrincipal OAuth2User user) {
        if (updater.id().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id must be set");
        }

        var request = this.directRequestService.get(updater.id().get());
        if (request.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "request does not exist");
        }

        String reviewerId = request.get().getReviewerId();
        if (!user.getAttribute("sub").toString().equals(reviewerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the reviewer may change the state");
        }

        if (request.get().getState() == DirectRequest.RequestState.DECLINED
            || request.get().getState() == DirectRequest.RequestState.ACCEPTED) {
            throw new ResponseStatusException(HttpStatus.UNPROCESSABLE_ENTITY, "request answer is already set");
        }

        if (updater.directRequestProcessId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "process id must not be set");
        }

        if (updater.reviewerId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "reviewer id must not be set");
        }

        if (updater.state().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "state must be set");
        }

        if (updater.state().get() == DirectRequest.RequestState.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "state can not be set to pending");
        }

        if (updater.state().get() == DirectRequest.RequestState.ACCEPTED) {

            Long entryId = this.directRequestProcessService.get(request.get().getDirectRequestProcessId()).get()
                    .getEntryId();

            Review.Dto review = new Review.Dto(Optional.empty(), Optional.of(reviewerId), Optional.of(entryId),
                    Optional.empty(), Review.ConfidenceLevel.LOW, null, null, null, null, null, null, null);

            this.reviewService.create(review);

            // TODO: Send notification to researcher
        }

        DirectRequest updatedDirectRequest = new DirectRequest(request.get().getId(),
                updater.state().get(), reviewerId, request.get().getDirectRequestProcessId());

        return directRequestService.update(request.get().getId(), updatedDirectRequest.toDto()).get().toDto();
    }

    @GetMapping("/requests")
    Paged<List<DirectRequest.Dto>> listRequestsByResearcher(@RequestParam("limit") Optional<Integer> limit,
                                                    @RequestParam("page") Optional<Integer> page,
                                                    @AuthenticationPrincipal OAuth2User user) {
        if (limit.isPresent()) {
            if (limit.get() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be greater than 0");
            }
            limit = Optional.of(Math.min(limit.get(), maxPageSize));
        }

        DirectRequest filterDirectRequest = new DirectRequest(null, null, user.getAttribute("sub"), null);

        var requestPage = this.directRequestService.list(page.orElse(0), limit.orElse(maxPageSize),
                filterDirectRequest.toDto());

        return new Paged<>(
                requestPage.getSize(),
                requestPage.getNumber() + 1,
                requestPage.getTotalPages(),
                requestPage.stream().map(DirectRequest::toDto).toList());
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
    public DirectRequest.Dto claimOpenSlot(@PathVariable("entry_id") final Long entryId,
                                       @AuthenticationPrincipal OAuth2User user) {
        if (this.entryService.get(entryId).isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }

        var directRequestProcess = this.directRequestProcessService.getByEntry(entryId);
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

        Review.Dto review = new Review.Dto(Optional.empty(), Optional.of(reviewerId), Optional.of(entryId),
                Optional.empty(), Review.ConfidenceLevel.LOW, null, null, null, null, null, null, null);

        this.reviewService.create(review);

        return this.directRequestService.create(directRequestObject.toDto()).toDto();
    }

    @DeleteMapping("/{category_id}/entries/{entry_id}/process/requests/{request_id}")
    DirectRequest.Dto deleteDirectRequest(@PathVariable("request_id") Long id,
                                                 @AuthenticationPrincipal OAuth2User user) {
        var option = this.directRequestService.get(id);
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "category does not exist");
        }
        var directRequestProcess = this.directRequestProcessService.get(option.get().getDirectRequestProcessId());
        if (directRequestProcess.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "process does not exist");
        }

        var researcherId = this.entryService.get(directRequestProcess.get().getEntryId()).get().getResearcherId();

        if (!researcherId.equals(user.getAttribute("sub"))) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the researcher may delete the request");
        }

        var deleted = this.directRequestService.delete(id);
        return deleted.map(DirectRequest::toDto).get();
    }
}