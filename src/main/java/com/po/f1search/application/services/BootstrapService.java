package com.po.f1search.application.services;

import com.po.f1search.application.ports.in.BootstrapUseCase;
import com.po.f1search.application.ports.in.CrawlUseCase;
import com.po.f1search.config.GeneralConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BootstrapService implements BootstrapUseCase {

    private static final Logger log = LoggerFactory.getLogger(BootstrapService.class);

    private final GeneralConfig generalConfig;
    private final CrawlUseCase crawlUseCase;

    public BootstrapService(
            GeneralConfig generalConfig,
            CrawlUseCase crawlUseCase
    ) {
        this.generalConfig = generalConfig;
        this.crawlUseCase = crawlUseCase;
    }

    @Override
    public void init() {
        if (!this.generalConfig.isCrawlOnBootstrap()) {
            log.info("Crawler is disabled on bootstrap. Skipping crawling process.");
            return;
        }

        this.crawlUseCase.crawl();
    }
}
