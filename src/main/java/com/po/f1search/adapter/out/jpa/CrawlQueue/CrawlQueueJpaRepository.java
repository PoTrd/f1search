package com.po.f1search.adapter.out.jpa.CrawlQueue;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CrawlQueueJpaRepository extends JpaRepository<CrawlQueueJpaEntity, UUID> {
}
