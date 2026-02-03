package com.po.f1search.adapter.out.web;

import com.po.f1search.application.ports.out.WebRepository;
import com.po.f1search.model.RobotsRules.RobotsRules;
import com.po.f1search.model.WebResource.*;
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
import java.util.Date;
import java.util.List;

@Component
public class WebAdapter implements WebRepository {

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(10))
            .followRedirects(HttpClient.Redirect.NORMAL)
            .build();

    @Override
    public WebResource fetchPage(Url url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url.value()))
                .timeout(Duration.ofSeconds(10))
                .header("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/131.0.0.0 Safari/537.36")
                .header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
                .header("Accept-Language", "en-US,en;q=0.5")
                .GET()
                .build();
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() == 403) {
                throw new IllegalStateException(
                        "Access denied (403) for URL " + url + ". The website may be blocking automated requests."
                );
            }

            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                throw new IllegalStateException(
                        "HTTP error " + response.statusCode() + " for URL " + url
                );
            }

        String html = response.body();
            Document document = Jsoup.parse(html, url.value());

            String title = document.title();
            String description = extractMeta(document, "description");
            String keywords = extractMeta(document, "keywords");
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

            Url[] outgoingLinks = extractLinks(document);

            return new WebResource(
                    null,
                    url,
                    new HtmlContent(Jsoup.parse(html).text()),
                    title,
                    description,
                    lstKeywords,
                    List.of(outgoingLinks)

            );

        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Failed to fetch page: " + url, e);
        }
    }

    private String extractMeta(Document document, String name) {
        Element meta = document.selectFirst("meta[name=" + name + "]");
        return meta != null ? meta.attr("content") : null;
    }

    private Url[] extractLinks(Document document) {
        List<Url> urls = document.select("a[href]")
                .stream()
                .map(link -> link.absUrl("href"))
                .map(String::trim)
                .filter(href -> !href.isBlank())
                .map(this::removeFragment)
                .filter(href -> !href.isBlank())
                .map(Url::new)
                .toList();

        return urls.toArray(new Url[0]);
    }

    private String removeFragment(String raw) {
        int hash = raw.indexOf('#');
        if (hash >= 0) raw = raw.substring(0, hash);

        try {
            URI uri = new URI(raw);
            return new URI(uri.getScheme(), uri.getAuthority(), uri.getPath(), uri.getQuery(), null).toString();
        } catch (Exception e) {
            return raw;
        }
    }

    @Override
    public RobotsRules fetchRobotsRules(Url url) {
        try {
            Url robotsUrl = new Url(url.value().endsWith("/") ? url.value() + "robots.txt" : url.value() + "/robots.txt");
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(robotsUrl.value()))
                    .timeout(Duration.ofSeconds(10))
                    .header("User-Agent", "Mozilla/5.0 (compatible; F1SearchBot/1.0; +http://www.example.com/bot)")
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                throw new IOException("Failed to fetch robots.txt: " + robotsUrl + " with status code " + response.statusCode());
            }
            String content = response.body();
            return RobotsRules.parseFromTxt(content);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Failed to get robots.txt for URL: " + url, e);
        }
    }
}
