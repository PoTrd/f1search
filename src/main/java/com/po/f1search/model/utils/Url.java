package com.po.f1search.model.utils;

public record Url(String value) {
    public Url {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("URL cannot be null or blank");
        }

//        if (!value.matches("^(http|https)://.*$")) {
//            throw new IllegalArgumentException("Invalid URL format");
//        }
    }
}
