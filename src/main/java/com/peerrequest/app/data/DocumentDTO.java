package com.peerrequest.app.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.GenericGenerator;

/**
 * Represents a document.
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "document")
public class DocumentDTO {
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", nullable = false, unique = true)
    @Getter
    private String id;
    @Lob
    @Column(name = "file")
    @Getter
    private byte[] file;
    @Column(name = "file_name")
    @Getter
    private String name;

    protected DocumentDTO() {}

    /**
     * Build a DocumentDTO from a DTO.
     *
     * @param dto DTO
     * @return DocumentDTO represented by the DTO
     */
    public static DocumentDTO fromDto(Dto dto) {
        return DocumentDTO.builder()
                .id(dto.id().orElse(null))
                .file(dto.file().get())
                .name(dto.fileName().get())
                .build();
    }

    /**
     * Creates a DTO for the current category.
     *
     * @return DTO
     */
    public DocumentDTO.Dto toDto() {
        return new Dto(getId() == null ? Optional.empty() : Optional.of(getId()),
                getFile() == null ? Optional.empty() : Optional.of(getFile()),
                getName() == null ? Optional.empty() : Optional.of(getName()));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        DocumentDTO document = (DocumentDTO) o;
        return id != null && Objects.equals(id, document.id);
    }
    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    /**
     * A DTO for the {@link com.peerrequest.app.data.DocumentDTO} entity.
     */
    public record Dto(
            @JsonProperty("id") Optional<String> id,
            @JsonProperty("file") Optional<byte[]> file,
            @JsonProperty("file_name") Optional<String> fileName)
            implements Serializable {
    }
}
