package com.po.f1search.application.services;

import com.po.f1search.application.ports.in.StartIndexUseCase;
import com.po.f1search.application.ports.out.persistence.WebResourceRepository;
import com.po.f1search.model.WebResource.WebResource;
import com.po.f1search.model.utils.HtmlContent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
public class StartIndexService implements StartIndexUseCase {

    private static final Logger log = LoggerFactory.getLogger(StartIndexService.class);

    private final WebResourceRepository webResourceRepository;

    public StartIndexService(
            WebResourceRepository webResourceRepository
    ) {
        this.webResourceRepository = webResourceRepository;
    }

    @Override
    public void startIndexing() {
        Stream<HtmlContent> lstHtmlContent = this.webResourceRepository.getAll().stream().map(
                WebResource::htmlContent
        );
        List<String> allTerms = lstHtmlContent.flatMap(
                htmlContent -> _extractLstTerms(htmlContent.value()).stream()
        ).toList();
        List<String> allUniqueTerms = allTerms.stream().distinct().toList();
        log.info("Indexing completed. Total terms indexed: {}", allUniqueTerms.size());
        log.info("Random sample of indexed terms: {}", allUniqueTerms.stream().limit(100).toList());
    }

    List<String> _extractLstTerms(String text) {
        return List.of(text.toLowerCase().split("\\W+"));
    }
}
