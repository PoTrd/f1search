package com.po.f1search.model;

import com.po.f1search.model.utils.Url;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record RobotsRules(
        UUID id,
        Boolean uaF1SearchAllowed,
        Boolean isSitemap,
        List<String> disallowedPaths,
        List<String> allowedPaths,
        Url sitemapLink
) {

    public RobotsRules() {
        this(
                UUID.randomUUID(),
                null,
                null,
                new ArrayList<>(),
                new ArrayList<>(),
                null
        );
    }

    public RobotsRules parse(String content) {
        Boolean uaF1SearchAllowed = null;
        Boolean isSitemap = null;
        List<String> disallowedPaths = new ArrayList<>();
        List<String> allowedPaths = new ArrayList<>();
        Url sitemapLink = null;

        String[] lines = content.split("\n");
        String currentUserAgent = null;

        for (String line : lines) {
            line = line.trim();
            boolean isLineEmpty = line.isEmpty() || line.startsWith("#");
            if (isLineEmpty) {
                continue;
            }

            String[] parts = line.split(":", 2);
            boolean isLineWeird = parts.length != 2;
            if (isLineWeird) {
                continue;
            }

            String directive = parts[0].trim().toLowerCase();
            String value = parts[1].trim();

            switch (directive) {
                case "user-agent" -> currentUserAgent = value.toLowerCase();
                case "disallow" -> {
                    if (currentUserAgent != null && (currentUserAgent.equals("*") || currentUserAgent.equals("f1search"))) {
                        disallowedPaths.add(value);
                    }
                }
                case "allow" -> {
                    if (currentUserAgent != null && (currentUserAgent.equals("*") || currentUserAgent.equals("f1search"))) {
                        allowedPaths.add(value);
                    }
                }
                case "sitemap" -> {
                    isSitemap = true;
                    sitemapLink = new Url(value);
                }
            }
        }

        return new RobotsRules(
                UUID.randomUUID(),
                uaF1SearchAllowed,
                isSitemap,
                disallowedPaths,
                allowedPaths,
                sitemapLink
        );
    }
}


