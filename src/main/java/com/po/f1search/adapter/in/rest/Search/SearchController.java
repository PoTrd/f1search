package com.po.f1search.adapter.in.rest.Search;

import com.po.f1search.application.ports.in.SearchUseCase;
import com.po.f1search.model.SearchResult.SearchResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/search")
public class SearchController {

    private final SearchUseCase searchUseCase;

    public SearchController(SearchUseCase searchUseCase) {
        this.searchUseCase = searchUseCase;
    }

    @GetMapping
    public SearchResponseDto search(@RequestParam("query") String query) {
        List<SearchResult> lstResults = this.searchUseCase.search(query);

        if (lstResults == null || lstResults.isEmpty()) {
            return new SearchResponseDto(query, List.of());
        }

        List<SearchResponseDto.SearchResultItem> items = lstResults.stream()
                .map(result -> new SearchResponseDto.SearchResultItem(
                        result.title(),
                        result.description(),
                        result.url().value()
                ))
                .toList();

        return new SearchResponseDto(query, items);
    }
}
