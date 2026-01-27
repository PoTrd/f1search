package com.po.f1search.application.ports.out.persistence;

import com.po.f1search.model.RobotsRules.RobotsRules;
import com.po.f1search.model.WebDomain.DomainId;
import com.po.f1search.model.WebDomain.WebDomain;

import java.util.List;
import java.util.UUID;

public interface RobotsRulesRepository {
    void save(DomainId domainId, RobotsRules robotsRules);
    RobotsRules getById(UUID id);
    RobotsRules getByDomainId(DomainId domainId);
    List<RobotsRules> getAll();
}
