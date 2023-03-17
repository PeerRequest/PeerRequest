package com.peerrequest.app.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * Represents a Message.
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "message")
public class Message {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @Getter
    private Long id;

    @Column(name = "review_id", nullable = false)
    @Getter
    private Long reviewId;

    @Column(name = "creator_id", nullable = false, length = 40)
    @Getter
    private String creatorId;

    @Column(name = "timestamp", nullable = false)
    @Getter
    private ZonedDateTime timeStamp;

    @Column(name = "content", nullable = false)
    @Getter
    @Setter
    private String content;

    protected Message() {
    }

    /**
     * Build a Message from a DTO.
     *
     * @param dto DTO
     * @return Message represented by the DTO
     */
    public static Message fromDto(Dto dto) {
        return fromDto(dto, dto.reviewId.get(), dto.creatorId.get());
    }

    /**
     * Build a Message from a DTO with a specific review ID and a specific creator ID.
     *
     * @param dto DTO
     * @return Review represented by the DTO
     */
    public static Message fromDto(Dto dto, Long reviewId, String creatorId) {
        return Message.builder()
            .id(dto.id().orElse(null))
            .reviewId(reviewId)
            .creatorId(creatorId)
            .timeStamp(dto.timestamp())
            .content(dto.content())
            .build();
    }

    /**
     * Creates a DTO for the current Message.
     *
     * @return DTO
     */
    public Dto toDto() {
        return new Dto(
            getId() == null ? Optional.empty() : Optional.of(getId()),
            Optional.of(getReviewId()),
            Optional.of(getCreatorId()),
            getTimeStamp(),
            getContent());
    }

    /**
     * A DTO for the {@link com.peerrequest.app.data.Message} entity.
     */
    public record Dto(
        @JsonProperty("id") Optional<Long> id,
        @JsonProperty("review_id") Optional<Long> reviewId,
        @JsonProperty("creator_id") Optional<String> creatorId,
        @JsonProperty("timestamp") ZonedDateTime timestamp,
        @JsonProperty("content") String content)
        implements Serializable {
    }
}