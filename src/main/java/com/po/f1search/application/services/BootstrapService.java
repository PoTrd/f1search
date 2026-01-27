package com.po.f1search.application.services;

import com.po.f1search.application.ports.in.BootstrapUseCase;
import com.po.f1search.application.ports.out.WebRepository;
import com.po.f1search.application.ports.out.persistence.*;
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
        log.info("Initialisation du crawling F1...");
        this.processNextTask();
    }

    private void processNextTask() {
        CrawlTask task = crawlQueueRepository.getNextTask();
        if (task == null) {
            log.info("Aucune tâche en attente.");
            return;
        }

        try {
            crawlQueueRepository.updateTaskState(task.id(), CrawlingState.IN_PROGRESS);
            executeCrawl(task);
            crawlQueueRepository.updateTaskState(task.id(), CrawlingState.COMPLETED);
        } catch (Exception e) {
            log.error("Échec du crawl pour {}: {}", task.url().value(), e.getMessage());
            crawlQueueRepository.updateTaskState(task.id(), CrawlingState.FAILED);
        }
    }

    private void executeCrawl(CrawlTask task) {
        Url domainUrl = extractDomainUrl(task.url());
        DomainId domainId = getOrCreateDomain(domainUrl);
        RobotsRules rules = getOrFetchRobotsRules(domainId, domainUrl);

        if (!isAllowedByRobots(rules, task.url())) {
            throw new IllegalStateException("Accès refusé par robots.txt pour : " + task.url().value());
        }

        WebResource content = webRepository.fetchPage(task.url());
        webResourceRepository.save(domainId, content);
        log.info("Données F1 sauvegardées avec succès pour : {}", task.url().value());
    }

    private DomainId getOrCreateDomain(Url domainUrl) {
        boolean exists = this.webDomainRepository.domainExists(domainUrl);
        if (!exists) {
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
            throw new IllegalArgumentException("Format d'URL invalide : " + url.value());
        }
    }
}