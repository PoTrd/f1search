package com.po.f1search.adapter.out.jpa.Terms;

import com.po.f1search.application.ports.out.persistence.TermsRepository;
import com.po.f1search.model.Term.Term;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@Component
public class TermsJpaAdapter implements TermsRepository {

    private final TermsJpaRepository jpaRepository;

    public TermsJpaAdapter(TermsJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    private Term _toDomainModel(TermsJpaEntity jpaEntity) {
        return new Term(
                jpaEntity.getId(),
                jpaEntity.getTerm(),
                jpaEntity.getDf()
        );
    }

    private TermsJpaEntity _toJpaEntity(Term term) {
        TermsJpaEntity jpaEntity = new TermsJpaEntity();
        jpaEntity.setTerm(term.value());
        jpaEntity.setDf(term.df());
        return jpaEntity;
    }


    @Override
    public void saveTerm(Term term) {
        TermsJpaEntity jpaEntity = _toJpaEntity(term);
        jpaRepository.save(jpaEntity);
    }

    @Override
    public List<Term> saveLstTerms(List<Term> terms) {
        List<TermsJpaEntity> jpaEntities = terms.stream()
                .map(this::_toJpaEntity)
                .toList();
        jpaRepository.saveAll(jpaEntities);
        return jpaEntities.stream()
                .map(this::_toDomainModel)
                .toList();
    }

    @Override
    public List<Term> getAllTerms() {
        List<TermsJpaEntity> jpaEntities = jpaRepository.findAll();
        return jpaEntities.stream()
                .map(this::_toDomainModel)
                .toList();
    }

    @Override
    public void deleteAll() {
        jpaRepository.deleteAll();
    }
}
