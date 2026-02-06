package com.po.f1search.application.ports.out.persistence;

import com.po.f1search.model.Term.Term;

import java.util.List;

public interface TermsRepository {
    void saveTerm(Term term);
    List<Term> saveLstTerms(List<Term> terms);
    List<Term> getAllTerms();
    void deleteAll();
}
