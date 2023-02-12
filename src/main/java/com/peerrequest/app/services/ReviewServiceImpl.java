package com.peerrequest.app.services;

import com.peerrequest.app.data.Message;
import com.peerrequest.app.data.Review;
import com.peerrequest.app.data.repos.MessageRepository;
import com.peerrequest.app.data.repos.ReviewRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

/**
 * Implementation of the review service.
 */
@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private ReviewRepository repo;

    @Autowired
    private MessageRepository messageRepo;

    @Autowired
    private DocumentService documentService;

    @Override
    public Review create(Review.Dto newEntity) {
        return repo.save(Review.fromDto(newEntity));
    }

    @Override
    public Page<Review> list(int page, int maxCount, Review.Dto filter) {
        if (filter.entryId().isPresent()) {
            return repo.findByEntryId(filter.entryId().get(), PageRequest.of(page, maxCount));
        }
        return repo.findAll(Pageable.ofSize(maxCount).withPage(page));
    }

    @Override
    public Optional<Review> get(Long cursor) {
        return repo.findById(cursor);
    }

    @Override
    public Optional<Review> update(Long cursor, Review.Dto newProps) {
        var optional = repo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var review = optional.get();

        if (newProps.reviewDocumentId().isPresent()) {
            review.setReviewDocumentId(newProps.reviewDocumentId().get());
        }
        if (newProps.confidenceLevel() != null) {
            review.setConfidenceLevel(newProps.confidenceLevel());
        }
        if (newProps.summary() != null) {
            review.setSummary(newProps.summary());
        }
        if (newProps.mainWeaknesses() != null) {
            review.setMainWeakness(newProps.mainWeaknesses());
        }
        if (newProps.mainStrengths() != null) {
            review.setMainStrengths(newProps.mainStrengths());
        }
        if (newProps.questionsForAuthors() != null) {
            review.setQuestionsForAuthors(newProps.questionsForAuthors());
        }
        if (newProps.answersFromAuthors() != null) {
            review.setAnswersFromAuthors(newProps.answersFromAuthors());
        }
        if (newProps.otherComments() != null) {
            review.setOtherComments(newProps.otherComments());
        }
        if (newProps.score() != null) {
            review.setScore(newProps.score());
        }
        return Optional.of(repo.save(review));
    }

    @Override
    public Optional<Review> delete(Long cursor) {
        var optional = repo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var review = optional.get();
        repo.delete(review);

        if (review.getReviewDocumentId() != null) {
            this.documentService.delete(review.getReviewDocumentId());
        }

        for (var message : this.messageRepo.findByReviewId(cursor)) {
            deleteMessage(message.getId());
        }

        return Optional.of(review);
    }

    @Override
    public Message createMessage(Message.Dto newEntity) {
        return messageRepo.save(Message.fromDto(newEntity));
    }


    @Override
    public Optional<Message> getMessage(Long cursor) {
        return messageRepo.findById(cursor);
    }

    @Override
    public Page<Message> listMessages(int page, int maxCount, Message.Dto filter) {
        if (filter.reviewId().isPresent()) {
            return messageRepo.findByReviewId(filter.reviewId().get(), PageRequest.of(page, maxCount));
        }
        return messageRepo.findAll(Pageable.ofSize(maxCount).withPage(page));
    }

    @Override
    public Optional<Message> updateMessage(Long cursor, Message.Dto newProps) {
        var optional = messageRepo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var message = optional.get();

        if (newProps.content() != null) {
            message.setContent(newProps.content());
        }
        return Optional.of(messageRepo.save(message));
    }

    @Override
    public Optional<Message> deleteMessage(Long cursor) {
        var optional = messageRepo.findById(cursor);
        if (optional.isEmpty()) {
            return Optional.empty();
        }

        var message = optional.get();
        messageRepo.delete(message);
        return Optional.of(message);
    }

    @Override
    public Page<Review> listByReviewerId(int page, int maxCount, String reviewerId) {
        return repo.findByReviewerId(reviewerId, Pageable.ofSize(maxCount).withPage(page));
    }

    @Override
    public List<Message> listMessageByReviewId(Long reviewId) {
        return messageRepo.findByReviewId(reviewId);
    }

    @Override
    public List<String> getReviewerIdsByEntryId(Long entryId) {
        return repo.getAllReviewerIdsByEntryId(entryId);
    }



    @Override
    public List<Review> listByEntryId(Long entryId) {
        return repo.findByEntryId(entryId);
    }
}
