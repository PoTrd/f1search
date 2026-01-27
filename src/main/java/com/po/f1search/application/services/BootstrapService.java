package com.po.f1search.application.services;

import com.po.f1search.application.ports.in.BootstrapUseCase;
import com.po.f1search.application.ports.out.WebRepository;
import com.po.f1search.application.ports.out.persistence.*;
import com.po.f1search.config.CrawlConfig;
import com.po.f1search.model.CrawlTask.CrawlTask;
import com.po.f1search.model.CrawlTask.CrawlingState;
import com.po.f1search.model.RobotsRules.RobotsRules;
import com.po.f1search.model.WebDomain.DomainId;
import com.po.f1search.model.WebResource.WebResource;
import com.po.f1search.model.utils.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.util.List;

@Service
public class BootstrapService implements BootstrapUseCase {

    private static final Logger log = LoggerFactory.getLogger(BootstrapService.class);

    private final CrawlConfig crawlconfig;

    private final WebRepository webRepository;
    private final WebResourceRepository webResourceRepository;
    private final RobotsRulesRepository robotsRulesRepository;
    private final CrawlQueueRepository crawlQueueRepository;
    private final WebDomainRepository webDomainRepository;

    public BootstrapService(
            CrawlConfig crawlConfig,
            WebRepository webRepository,
            WebResourceRepository webResourceRepository,
            RobotsRulesRepository robotsRulesRepository,
            CrawlQueueRepository crawlQueueRepository,
            WebDomainRepository webDomainRepository
    ) {
        this.crawlconfig = crawlConfig;
        this.webRepository = webRepository;
        this.webResourceRepository = webResourceRepository;
        this.robotsRulesRepository = robotsRulesRepository;
        this.crawlQueueRepository = crawlQueueRepository;
        this.webDomainRepository = webDomainRepository;
    }

    @Override
    public void init() {
        log.info("Crawling process started.");
        if (!this.crawlconfig.isWhiteListingEnabled()) {
//            log.warn("Whitelist is disabled. The application will not run. Please enable the whitelist to proceed.");
            return;
        }
        log.info("Whitelist OK - Allowed domains: {}", String.join(", ", this.crawlconfig.getWhitelistedDomains()));
        this.processNextTask();
    }

    private void processNextTask() {
        CrawlTask task = crawlQueueRepository.getNextTask();
        boolean isTaskAvailable = task != null && task.state() == CrawlingState.PENDING;
        if (!isTaskAvailable) {
            log.info("No more tasks in the crawl queue.");
            return;
        }
        boolean isUrlWhitelisted = crawlconfig.isDomainWhitelisted(task.url());
        if (!isUrlWhitelisted) {
            log.warn("URL {} is not whitelisted. Skipping.", this.extractDomainUrl(task.url()).value());
            crawlQueueRepository.updateTaskState(task.id(), CrawlingState.NOT_ALLOWED);
            processNextTask();
            return;
        }

        try {
            crawlQueueRepository.updateTaskState(task.id(), CrawlingState.IN_PROGRESS);
            executeCrawl(task);
            crawlQueueRepository.updateTaskState(task.id(), CrawlingState.COMPLETED);
        } catch (Exception e) {
            log.error("Error during crawling URL {}: {}", task.url().value(), e.getMessage());
            crawlQueueRepository.updateTaskState(task.id(), CrawlingState.FAILED);
        }
    }

    private void executeCrawl(CrawlTask task) {
        Url domainUrl = extractDomainUrl(task.url());
        DomainId domainId = getOrCreateDomain(domainUrl);
        RobotsRules rules = getOrFetchRobotsRules(domainId, domainUrl);

        if (!isAllowedByRobots(rules, task.url())) {
            throw new IllegalStateException("Crawling disallowed by robots.txt for URL: " + task.url().value());
        }

        WebResource content = webRepository.fetchPage(task.url());
        webResourceRepository.save(domainId, content);
        List<Url> newLinks = content.lstLinks();
        for (Url link : newLinks) {
            if (!crawlQueueRepository.isInQueue(link)) {
                crawlQueueRepository.addToQueue(link);
            }
        }
        log.info("Crawled and saved content for URL): {}", task.url().value());
    }

    private DomainId getOrCreateDomain(Url domainUrl) {
        boolean exists = this.webDomainRepository.domainExists(domainUrl);
        if (!exists) {
            //ask the user to confirm adding the new domain
            DomainId newDomainId = this.webDomainRepository.addDomain(domainUrl);
            log.info("New domain added {}", domainUrl.value());
            return newDomainId;
        }
        return this.webDomainRepository.getDomainByUrl(domainUrl).id();
    }

    private RobotsRules getOrFetchRobotsRules(DomainId domainId, Url domainUrl) {
        RobotsRules existingRules = robotsRulesRepository.getByDomainId(domainId);
        if (existingRules != null) {
            return existingRules;
        }

        RobotsRules newRules = webRepository.fetchRobotsRules(domainUrl);
        robotsRulesRepository.save(domainId, newRules);
        return newRules;
    }

    private boolean isAllowedByRobots(RobotsRules rules, Url targetUrl) {
        String path = targetUrl.value();
        boolean isDisallowed = rules.disallowedPaths().stream().anyMatch(path::contains);
        boolean isAllowed = rules.allowedPaths().stream().anyMatch(path::contains);

        return !isDisallowed || isAllowed;
    }

    private Url extractDomainUrl(Url url) {
        try {
            URI uri = new URI(url.value());
            String domain = uri.getScheme() + "://" + uri.getHost();
            return new Url(domain);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL: " + url.value());
        }
    }
}