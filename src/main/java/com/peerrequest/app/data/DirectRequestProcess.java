package com.peerrequest.app.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Check;


/**
 * Represents a Direct Request Process.
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "direct_request_process")
@Check(constraints = "open_slots >= 0")
public class DirectRequestProcess {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @Getter
    private Long id;
    @Column(name = "entry_id", nullable = false, updatable = false, unique = true)
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