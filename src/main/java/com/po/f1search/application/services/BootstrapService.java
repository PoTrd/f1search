package com.po.f1search.application.services;

import com.po.f1search.application.ports.in.BootstrapUseCase;
import com.po.f1search.application.ports.out.WebRepository;
import com.po.f1search.application.ports.out.persistence.WebResourceRepository;
import com.po.f1search.model.utils.Url;
import com.po.f1search.model.WebResource.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BootstrapService implements BootstrapUseCase {

    private static final Logger log = LoggerFactory.getLogger(BootstrapService.class);
    private final WebRepository webRepository;
    private final WebResourceRepository webResourceRepository;

    public BootstrapService(
            WebRepository webRepository,
            WebResourceRepository webResourceRepository
    ) {
        this.webRepository = webRepository;
        this.webResourceRepository = webResourceRepository;
    }

    @Override
    public void init() {
        log.info("Application initialization started");
        this._crawl();
    }

    private void _crawl() {
        log.info("Crawling data...");
        try {
            WebResource resource = this.webRepository.fetchPage(new Url("https://fr.motorsport.com/f1/"));
            log.info("Fetched Data: {}", resource);
            this.webResourceRepository.save(resource);
            log.info("Stored crawled resource: {}", resource.url().value());
        } catch (IllegalStateException e) {
            log.warn("Bootstrap crawl failed: {}", e.getMessage());
        }
    }
}
