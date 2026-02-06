package com.po.f1search.adapter.out.jpa.Terms;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "terms")
public class TermsJpaEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "term", nullable = false)
    private String term;

    @Column(name = "df",nullable = false)
    private Long df;

    public TermsJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public String getTerm() {
        return term;
    }
    public void setTerm(String term) {
        this.term = term;
    }

    public Long getDf() { return df; }
    public void setDf(Long df) { this.df = df; }
}
