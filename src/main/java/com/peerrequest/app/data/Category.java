package com.peerrequest.app.data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;
import org.hibernate.annotations.TimeZoneStorage;
import org.hibernate.annotations.TimeZoneStorageType;

/**
 * Represents a Category.
 */
@Builder
@AllArgsConstructor
@Entity
@Table(name = "category", uniqueConstraints = {
    @UniqueConstraint(name = "uc_category_name_label", columnNames = {"name", "label"})
})
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    @Column(name = "id", nullable = false)
    @Getter
    private Long id;
    @Column(name = "researcher_id", nullable = false, length = 40)
    @Getter
    private String researcherId;
    @Column(name = "name", nullable = false)
    @Getter
    @Setter
    private String name;
    @Enumerated(EnumType.STRING)
    @Column(name = "label", nullable = false)
    @Getter
    @Setter
    private CategoryLabel label;
    @Column(name = "deadline", nullable = false)
    @TimeZoneStorage(TimeZoneStorageType.NORMALIZE_UTC)
    @Getter
    @Setter
    private ZonedDateTime deadline;
    @Column(name = "min_score", nullable = false)
    @Getter
    @Setter
    private float minScore;
    @Column(name = "nax_score", nullable = false)
    @Getter
    @Setter
    private float maxScore;

    protected Category() {
    }

    static Category fromDTO(Dto dto) {
        return Category.builder()
            .id(dto.id())
            .name(dto.name())
            .researcherId(dto.researcherId())
            .label(dto.label())
            .deadline(dto.deadline())
            .minScore(dto.minScore())
            .maxScore(dto.maxScore())
            .build();
    }

    Dto toDTO() {
        return new Dto(getId(), getResearcherId(), getName(), getLabel(), getDeadline(), getMinScore(), getMaxScore());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) {
            return false;
        }
        Category category = (Category) o;
        return id != null && Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return this.id.hashCode();
    }

    /**
     * Label of a category.
     */
    public enum CategoryLabel {
        /**
         * If a category is INTERNAL, no bidding process can be started. Every user can add entries to this category.
         */
        INTERNAL,

        /**
         * If a category is EXTERNAL, only the researcher who created the process can add entries to this category.
         */
        EXTERNAL
    }

    /**
     * A DTO for the {@link com.peerrequest.app.data.Category} entity.
     */

    public record Dto(Long id, String researcherId, String name, Category.CategoryLabel label,
                      ZonedDateTime deadline,
                      float minScore, float maxScore)
        implements Serializable {
    }
}