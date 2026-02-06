package com.po.f1search.application.ports.out.persistence;

import java.util.UUID;

public interface IndexRepository {
    void saveIndexEntry(UUID termId, UUID resourceId, Long occurrences, Double score);
    void deleteAll();
    void saveIndexEntry(String termValue, UUID id, double tfIdf);
}
