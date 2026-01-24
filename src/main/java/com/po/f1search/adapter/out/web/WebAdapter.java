package com.po.f1search.adapter.out.web;

import com.po.f1search.application.ports.out.WebRepository;
import com.po.f1search.model.RobotsRules;
import com.po.f1search.model.Sitemap;
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
import java.util.List;
import java.util.UUID;

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
                    new HtmlContent(html),
                    new Metadata(title, description, lstKeywords),
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
                .filter(href -> !href.isBlank())
                .map(Url::new)
                .toList();

        return urls.toArray(new Url[0]);
    }

    @Override
    public RobotsRules getRobotsRules(Url url) {
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
            return new RobotsRules().parse(content);
        } catch (IOException | InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Failed to get robots.txt for URL: " + url, e);
        }
    }

    private Sitemap[] _GetSiteMaps(Url url) throws IOException, InterruptedException {
        HttpResponse<String> sitemap = httpClient.send(
                HttpRequest.newBuilder()
                        .uri(URI.create(url.value()))
                        .timeout(Duration.ofSeconds(10))
                        .header("User-Agent", "Mozilla/5.0 (compatible; F1SearchBot/1.0; +http://www.example.com/bot)")
                        .GET()
                        .build(),
                HttpResponse.BodyHandlers.ofString()
        );
        if (sitemap.statusCode() != 200) {
            throw new IOException("Failed to fetch sitemap: " + url + " with status code " + sitemap.statusCode());
        }
        Document document = Jsoup.parse(sitemap.body(), "", org.jsoup.parser.Parser.xmlParser());
        Element[] urlElements = document.select("url").toArray(new Element[0]);
        List<Sitemap> siteMaps = new ArrayList<>();
        for (Element urlElement : urlElements) {
            Element locElement = urlElement.selectFirst("loc");
            Element lastModElement = urlElement.selectFirst("lastmod");
            Url location = new Url(locElement.text());
            String lastModified = lastModElement != null ? lastModElement.text() : null;
            siteMaps.add(new Sitemap(location, lastModified != null ? Long.parseLong(lastModified) : null));
        }
        return siteMaps.toArray(new Sitemap[0]);
    }
}
