package com.po.f1search.adapter.out.jpa.WebResource;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "data")
public class WebResourceJpaEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "domain_id", nullable = false)
    private UUID domainId;

    @Column(nullable = false)
    private String url;

    @Column(name = "html_content", columnDefinition = "TEXT")
    private String htmlContent;

    @Column(columnDefinition = "TEXT")
    private String metadata;

    @Column(name = "link_list", columnDefinition = "TEXT")
    private String linkList;

    private Double score;

    private String state;

    public WebResourceJpaEntity() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getDomainId() { return domainId; }

    public void setDomainId(UUID domainId) { this.domainId = domainId; }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getHtmlContent() {
        return htmlContent;
    }

    public void setHtmlContent(String htmlContent) {
        this.htmlContent = htmlContent;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public String getLinkList() {
        return linkList;
    }

    public void setLinkList(String linkList) {
        this.linkList = linkList;
    }

    public Double getScore() {
        return score;
    }

    public void setScore(Double score) {
        this.score = score;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }
}
