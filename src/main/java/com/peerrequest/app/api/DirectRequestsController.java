package com.peerrequest.app.api;

import com.peerrequest.app.data.*;
import com.peerrequest.app.services.messages.EntryMessageTemplates;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.util.Pair;
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
        var directRequestProcess = this.directRequestProcessService.getByEntry(entryId);
        if (directRequestProcess.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "direct request process does not exist");
        }
        var option = this.directRequestProcessService.update(
                directRequestProcess.get().getId(), dto);

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

        if (!user.getAttribute("sub").toString().equals(entry.get().getResearcherId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the researcher may delete an request");
        }

        var request = this.directRequestService.get(requestId);

        if (request.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "direct request does not exist");
        }
        if (!request.get().getState().equals(DirectRequest.RequestState.PENDING)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "a request can only be deleted if it is pending");
        }

        this.notificationService.sendEntryNotification(user.getAttribute("sub").toString(),
            request.get().getReviewerId(), entryId, EntryMessageTemplates.REQUEST_WITHDRAWN);

        var deleted =  this.directRequestService.delete(requestId);
        return deleted.map(DirectRequest::toDto);
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

        var directRequestProcess = this.directRequestProcessService.getByEntry(entryId);

        if (directRequestProcess.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "request process does not exist");
        }

        if (request.id().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id must not be set");
        }

        if (request.state().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "state must not be set");
        }

        if (request.directRequestProcessId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "process id must not be set");
        }

        if (request.reviewerId().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "reviewer id must be set");
        }

        if (request.reviewerId().get().equals(researcherId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "user can not review their own entry");
        }

        for (var reviewer :
                this.directRequestService.listByDirectRequestProcessId(directRequestProcess.get().getId())) {
            if (reviewer.getReviewerId().equals(user.getAttribute("sub"))) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "user is already requested for this entry");
            }
        }

        var directRequest = new DirectRequest.Dto(Optional.empty(), Optional.of(DirectRequest.RequestState.PENDING),
                request.reviewerId(), directRequestProcess.get().toDto().id());

        this.notificationService.sendEntryNotification(user.getAttribute("sub").toString(), request.reviewerId().get(),
            entryId, EntryMessageTemplates.DIRECT_REQUEST);

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
    public DirectRequest.Dto patchDirectRequest(@PathVariable("entry_id") final Long entryId,
                                                @RequestBody final DirectRequest.Dto updater,
                                                @AuthenticationPrincipal OAuth2User user) {
        if (updater.id().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id must not be set");
        }

        var directRequestProcess = this.directRequestProcessService.getByEntry(entryId);

        if (directRequestProcess.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "request process does not exist");
        }

        Optional<DirectRequest> request = Optional.empty();

        for (var potentialRequest :
                this.directRequestService.listByDirectRequestProcessId(directRequestProcess.get().getId())) {
            if (potentialRequest.getReviewerId().equals(user.getAttribute("sub"))) {
                request = Optional.of(potentialRequest);
            }
        }

        if (request.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "request does not exist");
        }

        String reviewerId = request.get().getReviewerId();
        if (!user.getAttribute("sub").toString().equals(reviewerId)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "only the reviewer may change the state");
        }

        if (request.get().getState() != DirectRequest.RequestState.PENDING) {
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

        var researcherId = this.entryService.get(entryId).get().getResearcherId();

        if (updater.state().get() == DirectRequest.RequestState.ACCEPTED) {

            Review.Dto review = new Review.Dto(Optional.empty(), Optional.of(reviewerId), Optional.of(entryId),
                    Optional.empty(), Review.ConfidenceLevel.LOW, null, null, null, null, null, null, null);


            this.reviewService.create(review);

            this.notificationService.sendEntryNotification(reviewerId, researcherId, entryId,
                EntryMessageTemplates.REQUEST_ACCEPTED);
        }

        if (updater.state().get() == DirectRequest.RequestState.DECLINED) {
            this.notificationService.sendEntryNotification(reviewerId, researcherId, entryId,
                EntryMessageTemplates.REQUEST_DECLINED);
        }

        DirectRequest updatedDirectRequest = new DirectRequest(request.get().getId(),
                updater.state().get(), reviewerId, request.get().getDirectRequestProcessId());

        return directRequestService.update(request.get().getId(), updatedDirectRequest.toDto()).get().toDto();
    }

    @GetMapping("/requests")
    Paged<List<Pair<DirectRequest.Dto, Entry.Dto>>> listRequestsByResearcher(
            @RequestParam("limit") Optional<Integer> limit,
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

        List<Pair<DirectRequest.Dto, Entry.Dto>> pairList = new ArrayList<>();

        for (var request : requestPage) {
            var entryId = this.directRequestProcessService.get(request.getDirectRequestProcessId()).get().getEntryId();
            var entry = this.entryService.get(entryId);

            Pair<DirectRequest.Dto, Entry.Dto> pair = Pair.of(request.toDto(), entry.get().toDto());

            pairList.add(pair);
        }

        var pairPage = new PageImpl<>(pairList);

        return new Paged<>(
                pairPage.getSize(),
                pairPage.getNumber() + 1,
                pairPage.getTotalPages(),
                pairList);
    }

    /**
     * Returns all requests of the user of the given entry ids where the request state is not declined.
     *
     * @param entryIds entry ids, awaits a json array of entry ids e.g. '[1,4,7,2,3,6,8,9]'
     * @param user user who is signed in
     * @return specified requests
     */
    @PostMapping("/requests")
    List<DirectRequest.Dto> getSpecificRequests(@RequestBody List<Long> entryIds,
                                                       @AuthenticationPrincipal OAuth2User user) {
        return entryIds.stream()
                .map(this.directRequestProcessService::getByEntry)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .flatMap(process -> this.directRequestService
                        .listByDirectRequestProcessId(process.getId()).stream()
                        .filter(r -> r.getState() != DirectRequest.RequestState.DECLINED
                                && r.getReviewerId().equals(user.getAttribute("sub"))))
                .map(DirectRequest::toDto)
                .toList();
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

        DirectRequest request = null;

        for (var reviewer :
                this.directRequestService.listByDirectRequestProcessId(directRequestProcess.get().getId())) {
            if (reviewer.getReviewerId().equals(user.getAttribute("sub"))) {
                if (reviewer.getState().equals(DirectRequest.RequestState.ACCEPTED)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "user is already reviewer for this entry");
                }
                if (reviewer.getState().equals(DirectRequest.RequestState.PENDING)) {
                    throw new ResponseStatusException(HttpStatus.CONFLICT, "user is already requested for this entry");
                }
                request = reviewer;
                break;
            }
        }

        if (directRequestProcess.get().toDto().openSlots().get() <= 0) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "no empty slots available");
        }

        DirectRequestProcess updatedDirectRequestProcess = new DirectRequestProcess(directRequestProcess.get().getId(),
                directRequestProcess.get().getEntryId(), directRequestProcess.get().getOpenSlots() - 1);
        directRequestProcessService.update(directRequestProcess.get().getId(), updatedDirectRequestProcess.toDto());

        var reviewerId = user.getAttribute("sub").toString();

        Review.Dto review = new Review.Dto(Optional.empty(), Optional.of(reviewerId), Optional.of(entryId),
            Optional.empty(), Review.ConfidenceLevel.LOW, null, null, null, null, null, null, null);
        this.reviewService.create(review);

        this.notificationService.sendEntryNotification(reviewerId, researcherId, entryId,
            EntryMessageTemplates.OPEN_SLOT_CLAIMED);

        if (request != null) {
            DirectRequest.Dto patchRequest = new DirectRequest.Dto(Optional.empty(),
                Optional.of(DirectRequest.RequestState.ACCEPTED), Optional.empty(), Optional.empty());
            this.directRequestService.update(request.getId(), patchRequest);
            return request.toDto();
        } else {
            DirectRequest directRequestObject = new DirectRequest(null, DirectRequest.RequestState.ACCEPTED,
                reviewerId, directRequestProcess.get().getId());
            return this.directRequestService.create(directRequestObject.toDto()).toDto();
        }
    }
}