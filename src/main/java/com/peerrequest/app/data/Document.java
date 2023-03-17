package com.peerrequest.app.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.hibernate.annotations.GenericGenerator;

/**
 * Represents a document.
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "document")
public class Document {
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

    protected Document() {
    }

    /**
     * Build a DocumentDTO from a DTO.
     *
     * @param dto DTO
     * @return DocumentDTO represented by the DTO
     */
    public static Document fromDto(Dto dto) {
        return Document.builder().id(dto.id().orElse(null)).file(dto.file().get()).name(dto.fileName().get())
                .build();
    }

    /**
     * Creates a DTO for the current category.
     *
     * @return DTO
     */
    public Document.Dto toDto() {
        return new Dto(getId() == null ? Optional.empty() : Optional.of(getId()),
                getFile() == null ? Optional.empty() : Optional.of(getFile()),
                getName() == null ? Optional.empty() : Optional.of(getName()));
    }

    /**
     * A DTO for the {@link Document} entity.
     */
    public record Dto(@JsonProperty("id") Optional<String> id, @JsonProperty("file") Optional<byte[]> file,
                      @JsonProperty("file_name") Optional<String> fileName) implements Serializable {
    }
}