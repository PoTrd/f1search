package com.po.f1search.adapter.in.rest.Search;

import java.util.List;

public record SearchResponseDto(
        String query,
        List<SearchResultItem> results
) {
    public record SearchResultItem(
            String title,
            String description,
            String url
    ) {}
}
