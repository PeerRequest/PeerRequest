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


@Builder
@AllArgsConstructor
@Entity
@Table(name = "request")
public class Request {
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

    @Column(name = "entry_id", nullable = false)
    @Getter
    private Long entryId;

    protected Request() {
    }

    public static Request fromDto(Dto dto) {
        return fromDto(dto, dto.reviewerId().get());
    }

    public static Request fromDto(Dto dto, String researcherId) {
        // TODO: implement

        return Request.builder()
                .id(dto.id().orElse(null))
                .build();
    }

    /**
     * Creates a DTO for the current request.
     *
     * @return DTO
     */
    public Dto toDto() {
        // TODO: implement
        return new Dto(getId() == null ? Optional.empty() : Optional.of(getId()), getState(),
                Optional.of(getReviewerId()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Request request = (Request) o;
        return id != null && Objects.equals(id, request.id);
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
         * Request state when a user accepted a request.
         */
        ACCEPTED,
        /**
         * Request state when a user declined a request.
         */
        DECLINED,
        /**
         * Request state when a user neither accepted nor declined a request.
         */
        PENDING
    }

    public record Dto(
            @JsonProperty("id") Optional<Long> id,
            @JsonProperty("state") RequestState state,
            @JsonProperty("reviewer_id") Optional<String> reviewerId) implements Serializable {
    }
}