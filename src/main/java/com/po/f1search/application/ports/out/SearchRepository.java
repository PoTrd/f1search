package com.po.f1search.application.ports.out;

import com.po.f1search.model.SearchResult.SearchResult;

import java.util.List;

public interface SearchRepository {
    List<SearchResult> findResourceByTerms(List<String> lstTermsValues, Long offset, Long limit);
}
