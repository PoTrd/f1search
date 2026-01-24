package com.po.f1search.adapter.out.jpa.WebResource;

import com.po.f1search.application.ports.out.persistence.WebResourceRepository;
import com.po.f1search.model.utils.HtmlContent;
import com.po.f1search.model.WebResource.Metadata;
import com.po.f1search.model.utils.Url;
import com.po.f1search.model.WebResource.WebResource;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

@Component
public class WebResourceJpaAdapter implements WebResourceRepository {

    private final WebResourceJpaRepository jpaRepository;

    public WebResourceJpaAdapter(WebResourceJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    private WebResourceJpaEntity _toJpaEntity(WebResource webResource) {
        WebResourceJpaEntity jpaEntity = new WebResourceJpaEntity();

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

    private WebResource _toDomainModel(WebResourceJpaEntity jpaEntity) {
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
        WebResourceJpaEntity jpaEntity = _toJpaEntity(webResource);
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
        WebResourceJpaEntity jpaEntity = jpaRepository.findById(id).orElse(null);
        if (jpaEntity == null) {
            return null;
        }
        return _toDomainModel(jpaEntity);
    }

    @Override
    public List<WebResource> getAll() {
        List<WebResourceJpaEntity> jpaEntities = jpaRepository.findAll();
        return jpaEntities.stream()
                .map(this::_toDomainModel)
                .toList();
    }
}
