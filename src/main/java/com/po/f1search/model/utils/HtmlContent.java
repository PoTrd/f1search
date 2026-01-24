package com.po.f1search.model.utils;

public record HtmlContent(String value) {
    public HtmlContent {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("HTML content cannot be null or blank");
        }
    }
}
