package com.peerrequest.app.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

/**
 * Represents a Review.
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "review", uniqueConstraints = {
    @UniqueConstraint(name = "uc_entry-id_reviewer-id", columnNames = {"entry_id", "reviewer_id"})
})
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @Getter
    private Long id;
    @Column(name = "reviewer_id", nullable = false, length = 40)
    @Getter
    private String reviewerId;
    @Column(name = "entry_id", nullable = false)
    @Getter
    private Long entryId;
    @Column(name = "review_document_id")
    @Getter
    @Setter
    private String reviewDocumentId;
    @Enumerated(EnumType.STRING)
    @Column(name = "confidence_level", nullable = false)
    @Getter
    @Setter
    private ConfidenceLevel confidenceLevel;
    @Column(name = "summary", length = 2048)
    @Getter
    @Setter
    private String summary;
    @Column(name = "main_weaknesses", length = 2048)
    @Getter
    @Setter
    private String mainWeakness;
    @Column(name = "main_strengths", length = 2048)
    @Getter
    @Setter
    private String mainStrengths;
    @Column(name = "questions_for_authors", length = 2048)
    @Getter
    @Setter
    private String questionsForAuthors;
    @Column(name = "answers_from_authors", length = 2048)
    @Getter
    @Setter
    private String answersFromAuthors;
    @Column(name = "other_comments", length = 2048)
    @Getter
    @Setter
    private String otherComments;
    @Column(name = "score")
    @Getter
    @Setter
    private Float score;

    protected Review() {

    }

    /**
     * Build a Review from a DTO.
     *
     * @param dto DTO
     * @return Review represented by the DTO
     */
    public static Review fromDto(Dto dto) {
        return fromDto(dto, dto.reviewerId().get(), dto.entryId().get(), dto.reviewDocumentId().orElse(null));
    }

    /**
     * Build a Review from a DTO with a specific reviewer ID and a specific Entry ID.
     *
     * @param dto DTO
     * @return Review represented by the DTO
     */
    public static Review fromDto(Dto dto, String reviewerId, Long entryId, String documentId) {
        return Review.builder()
            .id(dto.id().orElse(null))
            .reviewerId(reviewerId)
            .entryId(entryId)
            .reviewDocumentId(documentId)
            .confidenceLevel(dto.confidenceLevel())
            .summary(dto.summary())
            .mainWeakness(dto.mainWeaknesses())
            .mainStrengths(dto.mainStrengths())
            .questionsForAuthors(dto.questionsForAuthors())
            .answersFromAuthors(dto.answersFromAuthors())
            .otherComments(dto.otherComments())
            .score(dto.score())
            .build();
    }

    /**
     * Creates a DTO for the current Review.
     *
     * @return DTO
     */
    public Dto toDto() {
        return new Dto(
            getId() == null ? Optional.empty() : Optional.of(getId()),
            Optional.of(getReviewerId()),
            Optional.of(getEntryId()),
            getReviewDocumentId() == null ? Optional.empty() : Optional.of(getReviewDocumentId()),
            getConfidenceLevel(),
            getSummary(),
            getMainWeakness(),
            getMainStrengths(),
            getQuestionsForAuthors(),
            getAnswersFromAuthors(),
            getOtherComments(),
            getScore());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Review review = (Review) o;
        return id != null && Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }


    /**
     * Represents the confidence level of a reviewer on the subject of the entry.
     */
    public enum ConfidenceLevel {
        /**
         * Confidence level when the reviewer is not confident in the subject.
         */
        LOW,
        /**
         * Confidence level when the reviewer is semi confident in the subject.
         */
        MEDIUM,
        /**
         * Confidence level when the reviewer is confident in the subject.
         */
        HIGH
    }

    /**
     * A DTO for the {@link com.peerrequest.app.data.Review} entity.
     */
    public record Dto(
        @JsonProperty("id") Optional<Long> id,
        @JsonProperty("reviewer_id") Optional<String> reviewerId,
        @JsonProperty("entry_id") Optional<Long> entryId,
        @JsonProperty("review_document_id") Optional<String> reviewDocumentId,
        @JsonProperty("confidence_level") Review.ConfidenceLevel confidenceLevel,
        @JsonProperty("summary") String summary,
        @JsonProperty("main_weaknesses") String mainWeaknesses,
        @JsonProperty("main_strengths") String mainStrengths,
        @JsonProperty("questions_for_authors") String questionsForAuthors,
        @JsonProperty("answers_from_authors") String answersFromAuthors,
        @JsonProperty("other_comments") String otherComments,
        @JsonProperty("score") Float score)
        implements Serializable {
    }

}