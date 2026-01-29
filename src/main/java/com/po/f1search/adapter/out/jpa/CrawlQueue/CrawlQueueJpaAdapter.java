package com.po.f1search.adapter.out.jpa.CrawlQueue;

import com.po.f1search.application.ports.out.persistence.CrawlQueueRepository;
import com.po.f1search.model.CrawlTask.CrawlTask;
import com.po.f1search.model.CrawlTask.CrawlingState;
import com.po.f1search.model.utils.Url;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class CrawlQueueJpaAdapter implements CrawlQueueRepository {

    private final CrawlQueueJpaRepository jpaRepository;

    public CrawlQueueJpaAdapter(CrawlQueueJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void addToQueue(Url url) {
        CrawlQueueJpaEntity jpaEntity = toJpaEntity(url);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public CrawlTask getNextTask() {
        CrawlQueueJpaEntity jpaEntity = jpaRepository.findAll().stream()
                .filter(entity -> entity.getState().equals(CrawlingState.PENDING.toString()))
                .findFirst()
                .orElse(null);
        if (jpaEntity == null) {
            return null;
        }
        return toDomainModel(jpaEntity);
    }

    @Override
    public CrawlTask getTaskByUrl(Url url) {
        CrawlQueueJpaEntity jpaEntity = jpaRepository.findAll().stream()
                .filter(entity -> entity.getUrl().equals(url.value()))
                .findFirst()
                .orElse(null);
        if (jpaEntity == null) {
            return null;
        }
        return toDomainModel(jpaEntity);
    }

    @Override
    public Boolean isInQueue(Url url) {
        return jpaRepository.findAll().stream()
                .anyMatch(entity -> entity.getUrl().equals(url.value()));
    }

    @Override
    public void removeFromQueue(UUID id) {
        jpaRepository.deleteById(id);
    }

    @Override
    public void updateTaskState(UUID id, CrawlingState newState) {
        CrawlQueueJpaEntity jpaEntity = jpaRepository.findById(id).orElse(null);
        if (jpaEntity == null) {
            return;
        }
        jpaEntity.setState(newState.toString());
        jpaRepository.save(jpaEntity);
    }

    @Override
    public int getPendingTaskCount() {
        return (int) jpaRepository.findAll().stream()
                .filter(entity -> entity.getState().equals(CrawlingState.PENDING.toString()))
                .count();
    }

    private CrawlQueueJpaEntity toJpaEntity(Url url) {
        CrawlQueueJpaEntity jpaEntity = new CrawlQueueJpaEntity();
        jpaEntity.setUrl(url.value());
        jpaEntity.setState(CrawlingState.PENDING.toString());
        return jpaEntity;
    }

    private CrawlTask toDomainModel(CrawlQueueJpaEntity jpaEntity) {
        return new CrawlTask(
                jpaEntity.getId(),
                new Url(jpaEntity.getUrl()),
                CrawlingState.valueOf(jpaEntity.getState())
        );
    }
}
