package com.po.f1search.adapter.out.jpa.WebDomain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface WebDomainJpaRepository extends JpaRepository<WebDomainJpaEntity, UUID> {
}
