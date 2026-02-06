package com.po.f1search.adapter.out.jpa.Index;

import com.po.f1search.application.ports.out.persistence.IndexRepository;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class IndexJpaAdapter implements IndexRepository {

    private final IndexJpaRepository jpaRepository;

    public IndexJpaAdapter(IndexJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void saveIndexEntry(UUID termId, UUID resourceId, Long occurrences, Double score) {
        IndexJpaEntity entity = new IndexJpaEntity();
        entity.setTermId(termId);
        entity.setResourceId(resourceId);
        entity.setOccurrence(occurrences);
        entity.setScore(score);
        jpaRepository.save(entity);
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }

    @Override
    public void saveIndexEntry(String termValue, UUID id, double tfIdf) {

    }
}
