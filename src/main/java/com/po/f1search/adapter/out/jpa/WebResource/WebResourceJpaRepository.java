package com.po.f1search.adapter.out.jpa.WebResource;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface WebResourceJpaRepository extends JpaRepository<WebResourceJpaEntity, UUID> {
}
