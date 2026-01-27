package com.po.f1search.config;

import com.po.f1search.model.utils.Url;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.util.List;

@Component
public class CrawlConfig {

    @Value("${f1search.crawler.whitelist}")
    private List<String> whitelistedDomains;

    public boolean isWhiteListingEnabled() {
        return whitelistedDomains != null
                && whitelistedDomains.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .anyMatch(this::_isValidDomain);
    }

    private boolean _isValidDomain(String value) {
        try {
            String normalized = value.startsWith("http://") || value.startsWith("https://")
                    ? value
                    : "http://" + value;

            URI uri = new URI(normalized);

            return uri.getHost() != null && uri.getHost().contains(".");
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isDomainWhitelisted(Url url) {
        String targetUrl = url.value().trim().toLowerCase();

        return whitelistedDomains.stream()
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(String::toLowerCase)
                .anyMatch(targetUrl::startsWith);
    }

    public List<String> getWhitelistedDomains() {
        return whitelistedDomains;
    }
}
