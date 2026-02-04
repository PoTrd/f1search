package com.po.f1search.adapter.out.web;

import com.po.f1search.application.ports.out.WebRepository;
import com.po.f1search.model.RobotsRules.RobotsRules;
import com.po.f1search.model.WebResource.WebResource;
import com.po.f1search.model.WebResource.WebRessourceState;
import com.po.f1search.model.utils.HtmlContent;
import com.po.f1search.model.utils.Url;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

@Component
public class WebAdapter implements WebRepository {

    private final HttpClient httpClient;

    public WebAdapter(HttpClient httpClient) {
        this.httpClient = httpClient;
    }

    @Override
    public WebResource fetchPage(Url url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.value()))
                .timeout(Duration.ofSeconds(10))
                .header("User-Agent", "F1SearchBot/1.0 - ")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            _validateResponse(response, url);
            return _parse(response.body(), url);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Failed to fetch page: " + url.value(), e);
        }
    }

    @Override
    public RobotsRules fetchRobotsRules(Url url) {
        Url robotsUrl = new Url(url.value().endsWith("/") ? url.value() + "robots.txt" : url.value() + "/robots.txt");
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(robotsUrl.value()))
                .timeout(Duration.ofSeconds(10))
                .header("User-Agent", "F1SearchBot/1.0 - ")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            _validateResponse(response, robotsUrl);
            return RobotsRules.parseFromTxt(response.body());
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Failed to get robots.txt for URL: " + url.value(), e);
        }
    }

    private void _validateResponse(HttpResponse<String> response, Url url) {
        if (response.statusCode() == 403) {
            throw new IllegalStateException(
                    "Access denied (403) for URL " + url.value() + ". The website may be blocking automated requests."
            );
        }

        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IllegalStateException(
                    "HTTP error " + response.statusCode() + " for URL " + url.value()
            );
        }
    }

    private WebResource _parse(String html, Url url) {
        Document document = Jsoup.parse(html, url.value());

        String title = document.title();
        String description = _extractMeta(document, "description");
        String keywords = _extractMeta(document, "keywords");
        List<String> lstKeywords = _parseKeywords(keywords);

        Url[] outgoingLinks = _extractLinks(document);

        return new WebResource(
                null,
                url,
                new HtmlContent(document.text()),
                title,
                description,
                lstKeywords,
                List.of(outgoingLinks),
                WebRessourceState.RAW
        );
    }

    private String _extractMeta(Document document, String name) {
        Element meta = document.selectFirst("meta[name=" + name + "]");
        return meta != null ? meta.attr("content") : null;
    }

    private List<String> _parseKeywords(String keywords) {
        List<String> lstKeywords = new ArrayList<>();
        if (keywords != null && !keywords.isBlank()) {
            String[] keywordsArray = keywords.split(",");
            for (String keyword : keywordsArray) {
                String trimmedKeyword = keyword.trim();
                if (!trimmedKeyword.isEmpty()) {
                    lstKeywords.add(trimmedKeyword);
                }
            }
        }
        return lstKeywords;
    }

    private Url[] _extractLinks(Document document) {
        List<Url> urls = document.select("a[href]")
                .stream()
                .map(link -> link.absUrl("href"))
                .map(String::trim)
                .filter(href -> !href.isBlank())
                .map(this::_removeFragment)
                .filter(href -> !href.isBlank())
                .map(Url::new)
                .toList();

        return urls.toArray(new Url[0]);
    }

    private String _removeFragment(String raw) {
        int hash = raw.indexOf('#');
        if (hash >= 0) raw = raw.substring(0, hash);

        try {
            URI uri = new URI(raw);
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), null).toString();
        } catch (Exception e) {
            return raw;
        }
    }
}
