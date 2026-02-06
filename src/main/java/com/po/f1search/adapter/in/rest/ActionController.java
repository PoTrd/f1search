package com.po.f1search.adapter.in.rest;

import com.po.f1search.application.ports.in.StartIndexUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actions")
@Component
public class ActionController {

    private final StartIndexUseCase startIndexUseCase;

    public ActionController(StartIndexUseCase startIndexUseCase) {
        this.startIndexUseCase = startIndexUseCase;
    }

    @PostMapping("/indexing/start")
    public ResponseEntity<Void> startIndexing() {
        startIndexUseCase.indexing();
        return ResponseEntity.accepted().build();
    }
}
