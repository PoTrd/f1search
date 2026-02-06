package com.po.f1search.application.services;

import com.po.f1search.application.ports.in.StartIndexUseCase;
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
public class StartIndexService implements StartIndexUseCase {

    private static final Logger log = LoggerFactory.getLogger(StartIndexService.class);

    private final WebResourceRepository webResourceRepository;
    private final TermsRepository termsRepository;
    private final IndexRepository indexRepository;

    public StartIndexService(
            WebResourceRepository webResourceRepository,
            TermsRepository termsRepository,
            IndexRepository indexRepository
    ) {
        this.webResourceRepository = webResourceRepository;
        this.termsRepository = termsRepository;
        this.indexRepository = indexRepository;
    }

    @Override
    public void startIndexing() {
        log.info("Starting indexing process...");

        this.indexRepository.deleteAll();
        this.termsRepository.deleteAll();

        // Extract terms and compute document frequencies
        List<WebResource> resources = webResourceRepository.getAll();
        Map<String, Long> termDfMap = resources.stream()
                .map(resource -> resource.htmlContent().value())
                .flatMap(content -> _extractUniqueTermsSet(content).stream())
                .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));

        List<Term> terms = termDfMap.entrySet().stream()
                .map(entry -> new Term(
                        entry.getKey(),
                        entry.getValue()
                ))
                .collect(Collectors.toList());

        //Batch save terms to the database withe a size of 500 terms by batch
        int batchSize = 500;
        List<Term> savedTerms = new ArrayList<>();
        for (int i = 0; i < terms.size(); i += batchSize) {
            int end = Math.min(i + batchSize, terms.size());
            List<Term> batch = terms.subList(i, end);
            savedTerms.addAll(termsRepository.saveLstTerms(batch));
        }

        // Build the inverted index with tf-idf scores
        int totalDocuments = resources.size();
        for (WebResource resource : resources) {
            String content = resource.htmlContent().value();
            String[] tokens = content.split("\\W+");
            Map<String, Long> termTfMap = Arrays.stream(tokens)
                    .filter(token -> !token.isBlank())
                    .map(String::toLowerCase)
                    .collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
            for (Map.Entry<String, Long> entry : termTfMap.entrySet()) {
                String termValue = entry.getKey();
                Long tf = entry.getValue();
                Long df = termDfMap.get(termValue);
                double idf = Math.log((double) totalDocuments / df);
                double tfIdf = tf * idf;
                indexRepository.saveIndexEntry(
                        // Find the term ID from the saved terms list
                        savedTerms.stream().filter(term -> term.value().equals(termValue))
                                .findFirst()
                                .orElseThrow(() -> new RuntimeException("Term not found: " + termValue)).id(),
                        resource.id(),
                        tf,
                        tfIdf
                );
            }
        }

        log.info("Indexing process completed.");
    }

    private Set<String> _extractUniqueTermsSet(String content) {
        return Arrays.stream(content.split("\\W+"))
                .filter(token -> !token.isBlank())
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }
}
