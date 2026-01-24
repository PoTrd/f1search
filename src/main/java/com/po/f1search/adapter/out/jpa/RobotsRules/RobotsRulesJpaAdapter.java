package com.po.f1search.adapter.out.jpa.RobotsRules;

import com.po.f1search.application.ports.out.persistence.WebResourceRepository;
import com.po.f1search.model.WebResource.Metadata;
import com.po.f1search.model.WebResource.WebResource;
import com.po.f1search.model.utils.HtmlContent;
import com.po.f1search.model.utils.Url;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class RobotsRulesJpaAdapter implements WebResourceRepository {

    private final RobotsRulesJpaRepository jpaRepository;

    public RobotsRulesJpaAdapter(RobotsRulesJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    private RobotsRulesJpaEntity _toJpaEntity(WebResource webResource) {
        RobotsRulesJpaEntity jpaEntity = new RobotsRulesJpaEntity();

        jpaEntity.setUrl(clean(webResource.url().value()));
        jpaEntity.setHtmlContent(clean(webResource.htmlContent().value()));
        String metadataStr = String.join(";",
                nullToEmpty(webResource.metadata().title()),
                nullToEmpty(webResource.metadata().description()),
                String.join(",", webResource.metadata().keywords()));
        jpaEntity.setMetadata(clean(metadataStr));
        String linksStr = String.join(",",
                java.util.Arrays.stream(webResource.lstLinks())
                        .map(Url::value)
                        .toArray(String[]::new));
        jpaEntity.setLinkList(clean(linksStr));
        return jpaEntity;
    }

    private WebResource _toDomainModel(RobotsRulesJpaEntity jpaEntity) {
        String metadataRaw = jpaEntity.getMetadata() == null ? "" : jpaEntity.getMetadata();
        String[] metadataParts = metadataRaw.split(";");
        String title = metadataParts.length > 0 ? metadataParts[0] : "";
        String description = metadataParts.length > 1 ? metadataParts[1] : "";
        String[] keywords = metadataParts.length > 2 ? metadataParts[2].split(",") : new String[]{};

        String linkListRaw = jpaEntity.getLinkList() == null ? "" : jpaEntity.getLinkList();
        String[] linkParts = linkListRaw.isEmpty() ? new String[]{} : linkListRaw.split(",");
        Url[] lstLinks = java.util.Arrays.stream(linkParts)
                .filter(link -> !link.isBlank())
                .map(Url::new)
                .toArray(Url[]::new);

        return new WebResource(
                jpaEntity.getId(),
                new Url(jpaEntity.getUrl()),
                new HtmlContent(jpaEntity.getHtmlContent()),
                new Metadata(title, description, keywords),
                lstLinks
        );
    }

    @Override
    public void save(WebResource webResource) {
        RobotsRulesJpaEntity jpaEntity = _toJpaEntity(webResource);
            jpaRepository.save(jpaEntity);
    }

    private String clean(String value) {
        if (value == null) {
            return null;
        }
        return value.replace("\u0000", "");
    }

    private String nullToEmpty(String value) {
        return value == null ? "" : value;
    }

    @Override
    public WebResource getById(UUID id) {
        RobotsRulesJpaEntity jpaEntity = jpaRepository.findById(id).orElse(null);
        if (jpaEntity == null) {
            return null;
        }
        return _toDomainModel(jpaEntity);
    }

    @Override
    public List<WebResource> getAll() {
        List<RobotsRulesJpaEntity> jpaEntities = jpaRepository.findAll();
        return jpaEntities.stream()
                .map(this::_toDomainModel)
                .toList();
    }
}
