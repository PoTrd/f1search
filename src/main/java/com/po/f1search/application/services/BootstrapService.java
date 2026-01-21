package com.po.f1search.application.services;

import com.po.f1search.application.ports.in.BootstrapUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class BootstrapService implements BootstrapUseCase {

    private static final Logger log = LoggerFactory.getLogger(BootstrapService.class);

    @Override
    public void init() {
        log.info("Application initialization started");
        this._crawl();
    }


    private void _crawl() {
        log.info("Crawling data...");
    }
}
