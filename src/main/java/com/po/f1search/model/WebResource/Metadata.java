package com.po.f1search.model.WebResource;

import java.util.List;

public record   Metadata(
    String title,
    String description,
    List<String> keywords
) {
}
