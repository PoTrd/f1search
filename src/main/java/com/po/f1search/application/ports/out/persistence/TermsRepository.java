package com.po.f1search.application.ports.out.persistence;

import com.po.f1search.model.Term.Term;

import java.util.List;
import java.util.UUID;

public interface TermsRepository {
    void saveTerm(Term term);
    List<Term> saveLstTerms(List<Term> terms);
    List<Term> getAllTerms();
    List<UUID> getTermIdsByValue(String termValue);
    void deleteAll();
}
