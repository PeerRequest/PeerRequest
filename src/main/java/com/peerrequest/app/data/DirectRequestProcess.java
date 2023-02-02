package com.peerrequest.app.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;

@Builder
@AllArgsConstructor
@Entity
@Table(name = "direct_request_process")
public class DirectRequestProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @Getter
    private Long id;
    @Column(name = "entry_id", nullable = false)
    @Getter
    private Long entryId;
    @Column(name = "open_slots", nullable = false)
    @Getter
    @Setter
    private Integer openSlots;

    protected DirectRequestProcess() {
    }

    /**
     * Build a DirectRequestProcess from a DTO.
     *
     * @param dto DTO
     * @return DirectRequestProcess represented by the DTO
     */
    public static DirectRequestProcess fromDto(Dto dto) {
        return fromDto(dto, dto.entryId().get());
    }

    /**
     * Build a DirectRequestProcess from a DTO with a specific researcher ID.
     *
     * @param dto DTO
     * @return category represented by the DTO
     */
    public static DirectRequestProcess fromDto(DirectRequestProcess.Dto dto, Long entryId) {
        return DirectRequestProcess.builder()
                .id(dto.id().orElse(null))
                .entryId(entryId)
                .openSlots(dto.openSlots().get())
                .build();
    }

    /**
     * Creates a DTO for the current category.
     *
     * @return DTO
     */
    public DirectRequestProcess.Dto toDto() {
        return new DirectRequestProcess.Dto(getId() == null ? Optional.empty() : Optional.of(getId()),
                getEntryId() == null ? Optional.empty() : Optional.of(getEntryId()), Optional.of(getOpenSlots()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        DirectRequestProcess directRequestProcess = (DirectRequestProcess) o;
        return id != null && Objects.equals(id, directRequestProcess.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    /**
     * A DTO for the {@link com.peerrequest.app.data.DirectRequestProcess} entity.
     */
    public record Dto(
            @JsonProperty("id") Optional<Long> id,
            @JsonProperty("entry_id") Optional<Long> entryId,
            @JsonProperty("open_slots") Optional<Integer> openSlots
        ) implements Serializable {
    }
}