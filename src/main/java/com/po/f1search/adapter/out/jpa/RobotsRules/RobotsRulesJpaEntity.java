package com.po.f1search.adapter.out.jpa.RobotsRules;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "data")
public class RobotsRulesJpaEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column
    private Boolean isUaF1SearchAllowed;



    public RobotsRulesJpaEntity() {}
}
