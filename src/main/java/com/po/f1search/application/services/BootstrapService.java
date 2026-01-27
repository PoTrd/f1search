package com.po.f1search.application.services;

import com.po.f1search.application.ports.in.BootstrapUseCase;
import com.po.f1search.application.ports.out.WebRepository;
import com.po.f1search.application.ports.out.persistence.CrawlQueueRepository;
import com.po.f1search.application.ports.out.persistence.RobotsRulesRepository;
import com.po.f1search.application.ports.out.persistence.WebDomainRepository;
import com.po.f1search.application.ports.out.persistence.WebResourceRepository;
import com.po.f1search.model.CrawlTask.CrawlTask;
import com.po.f1search.model.CrawlTask.CrawlingState;
import com.po.f1search.model.RobotsRules.RobotsRules;
import com.po.f1search.model.WebDomain.DomainId;
import com.po.f1search.model.WebResource.WebResource;
import com.po.f1search.model.utils.Url;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BootstrapService implements BootstrapUseCase {

    private static final Logger log = LoggerFactory.getLogger(BootstrapService.class);
    private final WebRepository webRepository;
    private final WebResourceRepository webResourceRepository;
    private final RobotsRulesRepository robotsRulesRepository;
    private final CrawlQueueRepository crawlQueueRepository;
    private final WebDomainRepository webDomainRepository;

    public BootstrapService(
            WebRepository webRepository,
            WebResourceRepository webResourceRepository,
            RobotsRulesRepository robotsRulesRepository,
            CrawlQueueRepository crawlQueueRepository,
            WebDomainRepository webDomainRepository
    ) {
        this.webRepository = webRepository;
        this.webResourceRepository = webResourceRepository;
        this.robotsRulesRepository = robotsRulesRepository;
        this.crawlQueueRepository = crawlQueueRepository;
        this.webDomainRepository = webDomainRepository;
    }

    @Override
    public void init() {
        log.info("Application initialization started");
        this._crawl();
    }

    private void _crawl() {
        log.info("Crawling data...");
        try {
            CrawlTask task = this.crawlQueueRepository.getNextTask();
            if (task == null) {
                throw new IllegalStateException("No crawl task found in the queue, please add at least one URL to start crawling.");
            }
            this.crawlQueueRepository.updateTaskState(task.id(), CrawlingState.IN_PROGRESS);
            Url domainUrl = this._parseDomainFromUrl(task.url());
            Boolean isDomainExists = this.webDomainRepository.domainExists(domainUrl);
            if (!isDomainExists) {
                DomainId domainId = this.webDomainRepository.addDomain(domainUrl);
                RobotsRules robotsRules = this.webRepository.fetchRobotsRules(domainUrl);
                this.robotsRulesRepository.save(domainId, robotsRules);
            }
            DomainId domainId = this.webDomainRepository.getDomainByUrl(domainUrl).id();
            RobotsRules robotsRules = this.robotsRulesRepository.getByDomainId(domainId);
            if (robotsRules == null) {
                robotsRules = webRepository.fetchRobotsRules(domainUrl);
                robotsRulesRepository.save(domainId, robotsRules);
            }
            List<String> disallowedPaths = robotsRules.disallowedPaths();
            List<String> allowedPaths = robotsRules.allowedPaths();
            boolean fetchAllowed = disallowedPaths.stream().noneMatch(path -> task.url().value().contains(path)) ||
                    allowedPaths.stream().anyMatch(path -> task.url().value().contains(path));
            if (!fetchAllowed) {
                throw new IllegalStateException("Crawling disallowed by robots.txt for URL: " + task.url().value());
            }
            try {
                WebResource data = this.webRepository.fetchPage(task.url());
                this.webResourceRepository.save(domainId, data);
                this.crawlQueueRepository.updateTaskState(task.id(), CrawlingState.COMPLETED);
                log.info("Crawled and saved data for URL: {}", task.url().value());
            }  catch (Exception e) {
                this.crawlQueueRepository.updateTaskState(task.id(), CrawlingState.FAILED);
                log.warn("Failed to crawl URL {}: {}", task.url().value(), e.getMessage());
            }

        } catch (IllegalStateException e) {
            log.warn("Bootstrap crawl failed: {}", e.getMessage());
        }
    }

    private Url _parseDomainFromUrl(Url url) {
        String value = url.value();
        String domain;
        try {
            String withoutProtocol = value.replaceFirst("^(http://|https://)", "");
            int slashIndex = withoutProtocol.indexOf('/');
            if (slashIndex != -1) {
                domain = withoutProtocol.substring(0, slashIndex);
            } else {
                domain = withoutProtocol;
            }
            return new Url("http://" + domain);
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid URL format: " + value);
        }
    }
}
