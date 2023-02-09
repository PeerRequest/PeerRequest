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
 * Represents a Direct Request.
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "direct_request", uniqueConstraints = {
    @UniqueConstraint(name = "uc_direct-request_reviewer-id_process-id",
        columnNames = {"reviewer_id", "direct_request_process_id"})
})
public class DirectRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @Getter
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "state", nullable = false)
    @Getter
    @Setter
    private RequestState state;

    @Column(name = "reviewer_id", nullable = false, length = 40)
    @Getter
    private String reviewerId;

    @Column(name = "direct_request_process_id", nullable = false)
    @Getter
    private Long directRequestProcessId;

    protected DirectRequest() {
    }

    /**
     * Build a Direct Request from a DTO.
     *
     * @param dto DTO
     * @return Direct Request represented by the DTO
     */
    public static DirectRequest fromDto(Dto dto) {
        return fromDto(dto, dto.reviewerId().get(), dto.state().get());
    }

    /**
     * Build a Direct Request from a DTO with a specific reviewerID and request state.
     *
     * @param dto DTO
     * @return Entry represented by the DTO
     */
    public static DirectRequest fromDto(Dto dto, String reviewerId, RequestState state) {
        return DirectRequest.builder()
                .id(dto.id().orElse(null))
                .state(state)
                .reviewerId(reviewerId)
                .directRequestProcessId(dto.directRequestProcessId().get())
                .build();
    }

    /**
     * Creates a DTO for the current request.
     *
     * @return DTO
     */
    public Dto toDto() {
        return new Dto(getId() == null ? Optional.empty() : Optional.of(getId()),
                Optional.of(getState()),
                Optional.of(getReviewerId()),
                Optional.of(getDirectRequestProcessId()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        DirectRequest directRequest = (DirectRequest) o;
        return id != null && Objects.equals(id, directRequest.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }


    /**
     * Represents the states of a request.
     */
    public enum RequestState {
        /**
         * DirectRequest state when a user accepted a request.
         */
        ACCEPTED,
        /**
         * DirectRequest state when a user declined a request.
         */
        DECLINED,
        /**
         * DirectRequest state when a user neither accepted nor declined a request.
         */
        PENDING
    }

    /**
     * A DTO for the {@link com.peerrequest.app.data.DirectRequest} entity.
     */
    public record Dto(
            @JsonProperty("id") Optional<Long> id,
            @JsonProperty("state") Optional<RequestState> state,
            @JsonProperty("reviewer_id") Optional<String> reviewerId,
            @JsonProperty("direct_request_process_id") Optional<Long> directRequestProcessId
    ) implements Serializable {
    }
}