package com.po.f1search.application.ports.out.persistence;

import com.po.f1search.model.WebDomain.DomainId;
import com.po.f1search.model.WebDomain.WebDomain;
import com.po.f1search.model.utils.Url;

import java.util.UUID;

public interface WebDomainRepository {
    DomainId addDomain(Url domain_url);
    WebDomain getDomainById(UUID domain_id);
    WebDomain getDomainByUrl(Url domain_url);
    Boolean domainExists(Url domain_url);
}
