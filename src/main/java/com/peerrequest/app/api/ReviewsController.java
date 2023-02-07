package com.peerrequest.app.api;

import com.peerrequest.app.data.Entry;
import com.peerrequest.app.data.Message;
import com.peerrequest.app.data.Paged;
import com.peerrequest.app.data.Review;
import java.util.List;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

/**
 * The controller class for reviews and the linked message board to encapsulate the functionalities of the corresponding
 * service and provide them as HTTP-endpoints.
 */
@RestController
@ApiControllerPrefix
public class ReviewsController extends ServiceBasedController {

    private final int maxPageSize = 100;

    @GetMapping("/categories/{categoryId}/entries/{entryId}/reviews")
    Paged<List<Review.Dto>> listReviews(@AuthenticationPrincipal OAuth2User user,
                                 @RequestParam Optional<Integer> limit,
                                 @RequestParam Optional<Integer> page,
                                 @PathVariable Long entryId) {
        checkAuthResearcher(this.entryService.get(entryId), user);

        if (limit.isPresent()) {
            if (limit.get() <= 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be greater than 0");
            }
            limit = Optional.of(Math.min(limit.get(), maxPageSize));
        }

        Review.Dto filterReview = new Review.Dto(null, null, Optional.of(entryId),
            null, null, null, null, null, null, null, null, null);

        var reviewPage = this.reviewService.list(page.map(p -> p - 1).orElse(0), limit.orElse(maxPageSize),
            filterReview);

        return new Paged<>(
            reviewPage.getSize(),
            reviewPage.getNumber() + 1,
            reviewPage.getTotalPages(),
            this.reviewService.list(page.map(p -> p - 1).orElse(0), limit.orElse(maxPageSize), filterReview)
                    .stream()
                    .map(Review::toDto).toList());
    }

    @GetMapping("/categories/{categoryId}/entries/{entryId}/reviews/{reviewId}")
    Review.Dto getReview(@AuthenticationPrincipal OAuth2User user,
                         @PathVariable Long entryId, @PathVariable Long reviewId) {

        var review = this.reviewService.get(reviewId);
        checkAuthReviewerOrResearcher(review, this.entryService.get(entryId), user);

        return review.get().toDto();
    }

    //delete for review is missing on purpose, no such function

    @PatchMapping("/categories/{categoryId}/entries/{entryId}/reviews")
    Review.Dto patchReview(@RequestBody Review.Dto dto,
                           @AuthenticationPrincipal OAuth2User user, @PathVariable Long entryId) {
        if (dto.id().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");
        }

        try  {
            checkAuthReviewer(this.reviewService.get(dto.id().get()), user);
            if (dto.answersFromAuthors() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "you may not change answers_from_authors");
            }
        } catch (ResponseStatusException r) {
            checkAuthResearcher(this.entryService.get(entryId), user);
            if (dto.confidenceLevel() != null
                || dto.summary() != null
                || dto.mainWeaknesses() != null
                || dto.mainStrengths() != null
                || dto.questionsForAuthors() != null
                || dto.otherComments() != null
                || dto.score() != null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "you may only change answers_from_authors");
            }
        }

        if (dto.reviewerId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you may not change the reviewer_id");
        }

        if (dto.entryId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you may not change the entry_id");
        }

        if (dto.reviewDocumentId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you may not change the document_id");
        }

        var option = this.reviewService.update(dto.id().get(), dto);
        if (option.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "review does not exist");
        }

        return option.get().toDto();
    }

    //post for review is missing on purpose, happens in a context of a request; delete is also not part of our design

    @GetMapping("/categories/{categoryId}/entries/{entryId}/reviews/{reviewId}/document")
    void getReviewDocument(@AuthenticationPrincipal OAuth2User user,
                           @PathVariable Long entryId,
                           @PathVariable Long reviewId) {
        var review = this.reviewService.get(reviewId);
        var entry = this.entryService.get(entryId);
        checkAuthReviewerOrResearcher(review, entry, user);

        //todo: Implement after DocumentService is finished
        throw new RuntimeException("Not implemented yet");
    }

    @PostMapping("/categories/{categoryId}/entries/{entryId}/reviews/{reviewId}/document")
    void postReviewDocument(@AuthenticationPrincipal OAuth2User user,
                           @PathVariable Long reviewId) {
        var review = this.reviewService.get(reviewId);
        checkAuthReviewer(review, user);

        // old document has to be deleted
        //todo: Implement after DocumentService is finished
        throw new RuntimeException("Not implemented yet");
    }

    @DeleteMapping("/categories/{categoryId}/entries/{entryId}/reviews/{reviewId}/document")
    void deleteReviewDocument(@AuthenticationPrincipal OAuth2User user,
                              @PathVariable Long reviewId) {
        var review = this.reviewService.get(reviewId);
        checkAuthReviewer(review, user);

        //todo: Implement after DocumentService is finished
        throw  new RuntimeException("Not implemented yet");
    }

    @GetMapping("/categories/{categoryId}/entries/{entryId}/reviews/{reviewId}/messages")
    List<Message.Dto> listMessages(@AuthenticationPrincipal OAuth2User user,
                                   @RequestParam Optional<Integer> limit,
                                   @RequestParam Optional<Long> after,
                                   @PathVariable Long entryId,
                                   @PathVariable Long reviewId) {
        checkAuthReviewerOrResearcher(this.reviewService.get(reviewId), this.entryService.get(entryId), user);

        if (limit.isPresent()) {
            if (limit.get() < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "limit must be greater than 0");
            }
            limit = Optional.of(Math.min(limit.get(), maxPageSize));
        }

        Message.Dto messageFilter = new Message.Dto(Optional.empty(),
            Optional.of(reviewId), Optional.empty(), null, null);

        return this.reviewService.listMessages(after.orElse(null), limit.orElse(maxPageSize), messageFilter)
            .stream().map(Message::toDto).toList();
    }

    @DeleteMapping("/categories/{categoryId}/entries/{entryId}/reviews/{reviewId}/messages/{messageId}")
    Optional<Message.Dto> deleteMessage(@AuthenticationPrincipal OAuth2User user,
                                        @PathVariable Long messageId) {
        var message = this.reviewService.getMessage(messageId);
        checkAuthMessage(message, user);
        var option = this.reviewService.deleteMessage(messageId);
        return option.map(Message::toDto);
    }

    @PostMapping("/categories/{categoryId}/entries/{entryId}/reviews/{reviewId}/messages")
    Message.Dto createMessage(@RequestBody Message.Dto dto,
                              @AuthenticationPrincipal OAuth2User user,
                              @PathVariable Long entryId,
                              @PathVariable Long reviewId) {
        var review = this.reviewService.get(reviewId);
        var entry = this.entryService.get(entryId);
        checkAuthReviewerOrResearcher(review, entry, user);

        if (dto.id().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id must not be set");
        }

        if (dto.reviewId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "review_id must not be set");
        }

        if (dto.creatorId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "creator_id must not be set");
        }

        if (dto.timestamp() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "timestamp must be set");
        }

        if (dto.content() == null || dto.content().equals("")) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "content must be set and not empty");
        }

        var message = Message.fromDto(dto, reviewId, user.getAttribute("sub"));
        return this.reviewService.createMessage(message.toDto()).toDto();
    }

    @PatchMapping("/categories/{categoryId}/entries/{entryId}/reviews/{reviewId}/messages")
    Message.Dto patchMessage(@AuthenticationPrincipal OAuth2User user,
                             @RequestBody Message.Dto dto) {
        if (dto.id().isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "id is required");
        }

        if (dto.reviewId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you may not change the review_id");
        }

        if (dto.creatorId().isPresent()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you may not change the creator_id");
        }

        if (dto.timestamp() != null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "you may not change the timestamp");
        }

        var option = this.reviewService.updateMessage(dto.id().get(), dto);
        checkAuthMessage(option, user);

        return option.get().toDto();
    }

    private void checkAuthReviewer(Optional<Review> review, OAuth2User user) {
        if (review.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "review does not exist");
        }
        if (!review.get().getReviewerId().equals(user.getAttribute("sub"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "you may not have the permission to patch this review");
        }
    }

    private void checkAuthResearcher(Optional<Entry> entry, OAuth2User user) {
        if (entry.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }
        if (!entry.get().getResearcherId().equals(user.getAttribute("sub"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "you may not have the permission to access this reviews");
        }
    }

    private void checkAuthReviewerOrResearcher(Optional<Review> review, Optional<Entry> entry, OAuth2User user) {
        if (review.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "review does not exist");
        }
        if (entry.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "entry does not exist");
        }
        if (!review.get().getReviewerId().equals(user.getAttribute("sub"))
            && !entry.get().getResearcherId().equals(user.getAttribute("sub"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "you may not have the permission to access this review");
        }
    }

    private void checkAuthMessage(Optional<Message> message , OAuth2User user) {
        if (message.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "message does not exist");
        }
        if (!message.get().getCreatorId().equals(user.getAttribute("sub"))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "you may not have the permission to delete this message");
        }
    }
}
