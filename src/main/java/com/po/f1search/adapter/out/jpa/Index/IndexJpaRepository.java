package com.po.f1search.adapter.out.jpa.Index;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface IndexJpaRepository extends JpaRepository<IndexJpaEntity, UUID> {
}
