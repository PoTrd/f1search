package com.po.f1search.model.SearchResult;

import com.po.f1search.model.utils.Url;

public record SearchResult(
        Url url,
        String title,
        String description,
        Double score
) {
}
