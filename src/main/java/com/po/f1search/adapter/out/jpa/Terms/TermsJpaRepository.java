package com.po.f1search.adapter.out.jpa.Terms;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TermsJpaRepository extends JpaRepository<TermsJpaEntity, UUID> {
}
