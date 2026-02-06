package com.po.f1search.adapter.out.jpa.Index;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "term_index")
public class IndexJpaEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "term_id", nullable = false)
    private UUID term_id;

    @Column(name = "resource_id", nullable = false)
    private UUID resource_id;

    @Column(name = "occurrences", nullable = false)
    private Long occurrence;

    @Column(name = "score", nullable = false)
    private Double score;

    public IndexJpaEntity() {}

    public UUID getId() { return id; }

    public UUID getTermId() { return term_id; }
    public void setTermId(UUID term_id) { this.term_id = term_id; }

    public UUID getResourceId() { return resource_id; }
    public void setResourceId(UUID resource_id) { this.resource_id = resource_id; }

    public Long getOccurrence() { return occurrence; }
    public void setOccurrence(Long occurrence) { this.occurrence = occurrence; }

    public Double getScore() { return score; }
    public void setScore(Double score) { this.score = score; }
}

