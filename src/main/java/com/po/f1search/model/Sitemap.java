package com.po.f1search.model;

import com.po.f1search.model.utils.Url;

public record Sitemap(
    Url url,
    Long lastModified
) {
}
