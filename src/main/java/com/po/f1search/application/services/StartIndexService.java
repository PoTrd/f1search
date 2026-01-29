package com.po.f1search.application.services;

import com.po.f1search.application.ports.in.StartIndexUseCase;
import com.po.f1search.application.ports.out.persistence.WebResourceRepository;
import org.springframework.stereotype.Service;

@Service
public class StartIndexService implements StartIndexUseCase {

    private final WebResourceRepository webResourceRepository;

    public StartIndexService(
            WebResourceRepository webResourceRepository
    ) {
        this.webResourceRepository = webResourceRepository;
    }

    @Override
    public void startIndexing() {

    }
}
