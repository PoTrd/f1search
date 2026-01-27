package com.po.f1search.adapter.out.jpa.RobotsRules;

import com.po.f1search.application.ports.out.persistence.RobotsRulesRepository;
import com.po.f1search.model.RobotsRules.RobotsRules;
import com.po.f1search.model.WebDomain.DomainId;
import com.po.f1search.model.utils.Url;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RobotsRulesJpaAdapter implements RobotsRulesRepository {

    private final RobotsRulesJpaRepository jpaRepository;

    public RobotsRulesJpaAdapter(RobotsRulesJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public void save(DomainId domainId, RobotsRules robotsRules) {
        RobotsRulesJpaEntity jpaEntity = _toJpaEntity(domainId, robotsRules);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public RobotsRules getById(UUID id) {
        RobotsRulesJpaEntity jpaEntity = jpaRepository.findById(id).orElse(null);
        if (jpaEntity == null) {
            return null;
        }
        return _toDomainModel(jpaEntity);
    }

    @Override
    public RobotsRules getByDomainId(DomainId domainId) {
        return jpaRepository.findAll().stream()
                .filter(entity -> entity.getDomainId().equals(domainId.value()))
                .findFirst()
                .map(this::_toDomainModel)
                .orElse(null);
    }

    @Override
    public List<RobotsRules> getAll() {
        return jpaRepository.findAll().stream()
                .map(this::_toDomainModel)
                .toList();
    }

    private RobotsRulesJpaEntity _toJpaEntity(DomainId domainId, RobotsRules robotsRules) {
        RobotsRulesJpaEntity jpaEntity = new RobotsRulesJpaEntity();
        jpaEntity.setDomainId(domainId.value());
        jpaEntity.setUaF1SearchAllowed(robotsRules.uaF1SearchAllowed());
        jpaEntity.setIsSitemap(robotsRules.isSitemap());
        jpaEntity.setDisallowedPaths(clean(joinList(robotsRules.disallowedPaths())));
        jpaEntity.setAllowedPaths(clean(joinList(robotsRules.allowedPaths())));
        jpaEntity.setSitemapLink(clean(robotsRules.sitemapLink() == null ? null : robotsRules.sitemapLink().value()));
        return jpaEntity;
    }

    private RobotsRules _toDomainModel(RobotsRulesJpaEntity jpaEntity) {
        List<String> disallowedPaths = splitList(jpaEntity.getDisallowedPaths());
        List<String> allowedPaths = splitList(jpaEntity.getAllowedPaths());
        Url sitemapLink = jpaEntity.getSitemapLink() == null || jpaEntity.getSitemapLink().isBlank()
                ? null
                : new Url(jpaEntity.getSitemapLink());

        return new RobotsRules(
                jpaEntity.getId(),
                jpaEntity.getUaF1SearchAllowed(),
                jpaEntity.getIsSitemap(),
                disallowedPaths,
                allowedPaths,
                sitemapLink
        );
    }

    private String joinList(List<String> values) {
        if (values == null || values.isEmpty()) {
            return "";
        }
        return String.join(",", values);
    }

    private List<String> splitList(String raw) {
        if (raw == null || raw.isBlank()) {
            return List.of();
        }
        return List.of(raw.split(","));
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        return value.replace("\u0000", "");
    }
}
