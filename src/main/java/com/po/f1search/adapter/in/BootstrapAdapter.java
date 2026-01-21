package com.po.f1search.adapter.in;

import com.po.f1search.application.ports.in.BootstrapUseCase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class BootstrapAdapter {

    private static final Logger log = LoggerFactory.getLogger(BootstrapAdapter.class);

    private final BootstrapUseCase bootstrapUseCase;

    public BootstrapAdapter(
            BootstrapUseCase bootstrapUseCase
    ) {
        this.bootstrapUseCase = bootstrapUseCase;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        log.info("Application READY → starting work");
        bootstrapUseCase.init();
    }
}
