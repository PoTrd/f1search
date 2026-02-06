package com.po.f1search.adapter.in.rest;

import com.po.f1search.application.ports.in.CrawlUseCase;
import com.po.f1search.application.ports.in.IndexUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actions")
@Component
public class ActionController {

    private final IndexUseCase indexUseCase;
    private final CrawlUseCase crawlUseCase;

    public ActionController(
            IndexUseCase startIndexUseCase,
            CrawlUseCase startCrawlUseCase
    ) {
        this.indexUseCase = startIndexUseCase;
        this.crawlUseCase = startCrawlUseCase;
    }

    @PostMapping("/indexing/start")
    public ResponseEntity<Void> startIndexing() {
        indexUseCase.indexing();
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/crawling/start")
    public ResponseEntity<Void> startCrawling() {
        crawlUseCase.crawl();
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/crawling/stop")
    public ResponseEntity<Void> stopCrawling() {
        crawlUseCase.stopCrawl();
        return ResponseEntity.accepted().build();
    }
}
