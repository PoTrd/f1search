package com.po.f1search.adapter.out.jpa.WebDomain;

import com.po.f1search.application.ports.out.persistence.WebDomainRepository;
import com.po.f1search.model.WebDomain.DomainId;
import com.po.f1search.model.WebDomain.WebDomain;
import com.po.f1search.model.utils.Url;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class WebDomainJpaAdapter implements WebDomainRepository {

    private final WebDomainJpaRepository jpaRepository;

    public WebDomainJpaAdapter(WebDomainJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public DomainId addDomain(Url domain_url) {
        WebDomainJpaEntity jpaEntity = new WebDomainJpaEntity();
        jpaEntity.setDomainUrl(domain_url.value());
        jpaRepository.save(jpaEntity);
        return new DomainId(jpaEntity.getId());
    }

    @Override
    public WebDomain getDomainById(UUID domain_id) {
        WebDomainJpaEntity jpaEntity = jpaRepository.findById(domain_id).orElse(null);
        if (jpaEntity == null) {
            return null;
        }
        return toDomainModel(jpaEntity);
    }

    @Override
    public WebDomain getDomainByUrl(Url domain_url) {
        return jpaRepository.findAll().stream()
                .filter(entity -> entity.getDomainUrl().equals(domain_url.value()))
                .findFirst()
                .map(this::toDomainModel)
                .orElse(null);
    }

    @Override
    public Boolean domainExists(Url domain_url) {
        return jpaRepository.findAll().stream()
                .anyMatch(entity -> entity.getDomainUrl().equals(domain_url.value()));
    }

    private WebDomain toDomainModel(WebDomainJpaEntity jpaEntity) {
        return new WebDomain(
                new DomainId(jpaEntity.getId()),
                new Url(jpaEntity.getDomainUrl())
        );
    }

    private WebDomainJpaEntity toJpaEntity(WebDomain webDomain) {
        WebDomainJpaEntity jpaEntity = new WebDomainJpaEntity();
        jpaEntity.setDomainUrl(webDomain.domainUrl().value());
        return jpaEntity;
    }
}
