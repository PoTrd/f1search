package com.po.f1search.application.ports.in;

import com.po.f1search.model.SearchResult.SearchResult;

import java.util.List;

public interface SearchUseCase {
    List<SearchResult> search(String query);
}
