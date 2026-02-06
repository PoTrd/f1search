package com.po.f1search.application.services;

import com.po.f1search.application.ports.in.SearchUseCase;
import com.po.f1search.application.ports.out.SearchRepository;
import com.po.f1search.model.SearchResult.SearchResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class SearchService implements SearchUseCase {

    private static final Logger log = LoggerFactory.getLogger(SearchService.class);

    private final SearchRepository searchRepository;

    public SearchService(
            SearchRepository searchRepository
    ) {
        this.searchRepository = searchRepository;
    }

    @Override
    public List<SearchResult> search(String query) {

        List<String> tokens = _parseQuery(query);

        if (tokens.isEmpty()) {
            return null;
        }
        List<SearchResult> searchResults = searchRepository.findResourceByTerms(tokens, 0L, 10L);
        log.info(
                "Search query: '{}', tokens: {}, results found: {}",
                query,
                tokens,
                searchResults.size()
        );
        return searchResults;
    }


    private List<String> _parseQuery(String query) {
        if (query == null || query.isBlank()) {
            return Collections.emptyList();
        }

        return Arrays.stream(query.split("\\W+"))
                .filter(token -> !token.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.toList());
    }
}
