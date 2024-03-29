package com.peerrequest.app.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

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

    @Column(name = "authors")
    @Getter
    @Setter
    private String authors;

    @Column(name = "researcher_id", nullable = false, length = 40)
    @Getter
    private String researcherId;

    @Column(name = "category_id", nullable = false)
    @Getter
    private Long categoryId;

    protected Entry() {
    }

    /**
     * Build an Entry from a DTO.
     *
     * @param dto DTO
     * @return entry represented by the DTO
     */
    public static Entry fromDto(Dto dto) {
        return fromDto(dto, dto.researcherId().get(), dto.categoryId().get(), dto.documentId().get());
    }

    /**
     * Build an Entry from a DTO with a specific researcher ID.
     *
     * @param dto DTO
     * @return Entry represented by the DTO
     */
    public static Entry fromDto(Dto dto, String researcherId, Long categoryId, String documentId) {
        return Entry.builder()
                .id(dto.id().orElse(null))
                .name(dto.name())
                .researcherId(researcherId)
                .authors(dto.authors().orElse(null))
                .documentId(documentId)
                .categoryId(categoryId)
                .build();
    }

    /**
     * Creates a DTO for the current category.
     *
     * @return DTO
     */
    public Dto toDto() {
        return new Dto(
                getId() == null ? Optional.empty() : Optional.of(getId()),
                Optional.of(getResearcherId()),
                getName(),
                getAuthors() == null ? Optional.empty() : Optional.of(getAuthors()),
                Optional.of(getDocumentId()),
                Optional.ofNullable(getCategoryId())
        );
    }

    /**
     * A DTO for the {@link com.peerrequest.app.data.Entry} entity.
     */
    public record Dto(
            @JsonProperty("id") Optional<Long> id,
            @JsonProperty("researcher_id") Optional<String> researcherId,
            @JsonProperty("name") String name,
            @JsonProperty ("authors") Optional<String> authors,
            @JsonProperty("document_id") Optional<String> documentId,
            @JsonProperty("category_id") Optional<Long> categoryId)
            implements Serializable {
    }
}