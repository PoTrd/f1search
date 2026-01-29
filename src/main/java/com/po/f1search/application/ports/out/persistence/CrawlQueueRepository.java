package com.po.f1search.application.ports.out.persistence;

import com.po.f1search.model.CrawlTask.CrawlTask;
import com.po.f1search.model.CrawlTask.CrawlingState;
import com.po.f1search.model.utils.Url;

import java.util.UUID;

public interface CrawlQueueRepository {
    void addToQueue(Url url);
    CrawlTask getNextTask();
    CrawlTask getTaskByUrl(Url url);
    Boolean isInQueue(Url url);
    void removeFromQueue(UUID id);
    void updateTaskState(UUID id, CrawlingState newState);
    int getPendingTaskCount();
}
