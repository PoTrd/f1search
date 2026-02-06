package com.po.f1search.application.services;

import com.po.f1search.application.ports.in.IndexUseCase;
import com.po.f1search.application.ports.out.persistence.IndexRepository;
import com.po.f1search.application.ports.out.persistence.TermsRepository;
import com.po.f1search.application.ports.out.persistence.WebResourceRepository;
import com.po.f1search.model.Term.Term;
import com.po.f1search.model.WebResource.WebResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class IndexService implements IndexUseCase {

    private static final Logger log = LoggerFactory.getLogger(IndexService.class);

    private final WebResourceRepository webResourceRepository;
    private final TermsRepository termsRepository;
    private final IndexRepository indexRepository;

    public IndexService(
            WebResourceRepository webResourceRepository,
            TermsRepository termsRepository,
            IndexRepository indexRepository
    ) {
        this.webResourceRepository = webResourceRepository;
        this.termsRepository = termsRepository;
        this.indexRepository = indexRepository;
    }

    @Override
    public void indexing() {
        log.info("Starting indexing process...");

        _handleCleanup();

        List<WebResource> resources = webResourceRepository.getAll();
        if (resources.isEmpty()) {
            log.warn("No resources found to index.");
            return;
        }

        Map<String, Long> termDfMap = _computeDocumentFrequencies(resources);
        Map<String, Term> savedTermsMap = _handleIndexBatchSave(termDfMap);

        _buildAndSaveInvertedIndex(resources, termDfMap, savedTermsMap);

        log.info("Indexing process completed successfully.");
    }

    private void _handleCleanup() {
        indexRepository.deleteAll();
        termsRepository.deleteAll();
    }

    private Map<String, Long> _computeDocumentFrequencies(List<WebResource> resources) {
        return resources.stream()
                .map(res -> res.htmlContent().value())
                .flatMap(content -> _extractUniqueTerms(content).stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private Map<String, Term> _handleIndexBatchSave(Map<String, Long> termDfMap) {
        List<Term> termsToSave = termDfMap.entrySet().stream()
                .map(e -> new Term(e.getKey(), e.getValue()))
                .toList();

        int batchSize = 500;
        List<Term> allSavedTerms = new ArrayList<>();

        for (int i = 0; i < termsToSave.size(); i += batchSize) {
            int end = Math.min(i + batchSize, termsToSave.size());
            allSavedTerms.addAll(termsRepository.saveLstTerms(termsToSave.subList(i, end)));
        }

        return allSavedTerms.stream()
                .collect(Collectors.toMap(Term::value, Function.identity()));
    }

    private void _buildAndSaveInvertedIndex(List<WebResource> resources,
                                            Map<String, Long> termDfMap,
                                            Map<String, Term> savedTermsMap) {
        int totalDocs = resources.size();

        for (WebResource resource : resources) {
            Map<String, Long> termTfMap = _computeTermFrequencies(resource.htmlContent().value());

            termTfMap.forEach((termValue, tf) -> {
                double idf = Math.log((double) totalDocs / termDfMap.get(termValue));
                double tfIdf = tf * idf;

                Term term = savedTermsMap.get(termValue);
                if (term != null) {
                    indexRepository.saveIndexEntry(term.id(), resource.id(), tf, tfIdf);
                }
            });
        }
    }

    private Map<String, Long> _computeTermFrequencies(String content) {
        return Arrays.stream(content.split("\\W+"))
                .filter(token -> !token.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
    }

    private Set<String> _extractUniqueTerms(String content) {
        return Arrays.stream(content.split("\\W+"))
                .filter(token -> !token.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }
}
