package com.po.f1search.adapter.out.jpa.RobotsRules;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Entity
@Table(name = "robots_rules")
public class RobotsRulesJpaEntity {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(name = "domain_id", nullable = false)
    private UUID domainId;

    @Column(name = "ua_f1search_allowed")
    private Boolean uaF1SearchAllowed;

    @Column(name = "is_sitemap")
    private Boolean isSitemap;

    @Column(name = "disallowed_paths", columnDefinition = "TEXT")
    private String disallowedPaths;

    @Column(name = "allowed_paths", columnDefinition = "TEXT")
    private String allowedPaths;

    @Column(name = "sitemap_link", columnDefinition = "TEXT")
    private String sitemapLink;

    public RobotsRulesJpaEntity() {}

    public UUID getId() {
        return id;
    }

    public UUID getDomainId() { return domainId; }

    public void setDomainId(UUID domainId) { this.domainId = domainId; }

    public Boolean getUaF1SearchAllowed() {
        return uaF1SearchAllowed;
    }

    public void setUaF1SearchAllowed(Boolean uaF1SearchAllowed) {
        this.uaF1SearchAllowed = uaF1SearchAllowed;
    }

    public Boolean getIsSitemap() {
        return isSitemap;
    }

    public void setIsSitemap(Boolean isSitemap) {
        this.isSitemap = isSitemap;
    }

    public String getDisallowedPaths() {
        return disallowedPaths;
    }

    public void setDisallowedPaths(String disallowedPaths) {
        this.disallowedPaths = disallowedPaths;
    }

    public String getAllowedPaths() {
        return allowedPaths;
    }

    public void setAllowedPaths(String allowedPaths) {
        this.allowedPaths = allowedPaths;
    }

    public String getSitemapLink() {
        return sitemapLink;
    }

    public void setSitemapLink(String sitemapLink) {
        this.sitemapLink = sitemapLink;
    }
}
