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

/**
 * Represents an Entry.
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "entry")
public class Entry {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @Getter
    private Long id;

    @Column(name = "document_id", nullable = false)
    @Getter
    private String documentId;

    @Column(name = "name", nullable = false)
    @Getter
    @Setter
    private String name;

    @Column(name = "researcher_id", nullable = false, length = 40)
    @Getter
    private String researcherId;

    @Column(name = "category_id", nullable = false)
    @Getter
    private Long categoryId;

    protected Entry() {
    }

    /**
     * Build a Category from a DTO.
     *
     * @param dto DTO
     * @return entry represented by the DTO
     */
    public static Entry fromDto(Dto dto) {
        return fromDto(dto, dto.researcherId().get());
    }

    /**
     * Build an Entry from a DTO with a specific researcher ID.
     *
     * @param dto DTO
     * @return Entry represented by the DTO
     */
    public static Entry fromDto(Dto dto, String researcherId) {
        return Entry.builder()
                .id(dto.id().orElse(null))
                .name(dto.name())
                .researcherId(researcherId)
                .documentId(String.valueOf(dto.documentId()))
                .categoryId(dto.categoryId().get())
                .build();
    }

    /**
     * Creates a DTO for the current category.
     *
     * @return DTO
     */
    public Dto toDto() {
        return new Dto(getId() == null ? Optional.empty() : Optional.of(getId()), Optional.of(getResearcherId()),
                getName(), Optional.ofNullable(getDocumentId()), Optional.ofNullable(getCategoryId()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Entry entry = (Entry) o;
        return id != null && Objects.equals(id, entry.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    /**
     * A DTO for the {@link com.peerrequest.app.data.Entry} entity.
     */
    public record Dto(
            @JsonProperty("id") Optional<Long> id,
            @JsonProperty("researcher_id") Optional<String> researcherId,
            @JsonProperty("name") String name,
            @JsonProperty("document_id") Optional<String> documentId,
            @JsonProperty("category_id") Optional<Long> categoryId
            )
            implements Serializable {
    }
}
