package com.po.f1search.model.CrawlTask;

import com.po.f1search.model.utils.Url;

import java.util.UUID;

public record CrawlTask(
        UUID id,
        Url url,
        CrawlingState state
) {
}
