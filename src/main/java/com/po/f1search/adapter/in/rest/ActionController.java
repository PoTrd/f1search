package com.po.f1search.adapter.in.rest;

import com.po.f1search.application.ports.in.IndexUseCase;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/actions")
@Component
public class ActionController {

    private final IndexUseCase indexUseCase;

    public ActionController(IndexUseCase startIndexUseCase) {
        this.indexUseCase = startIndexUseCase;
    }

    @PostMapping("/indexing/start")
    public ResponseEntity<Void> startIndexing() {
        indexUseCase.indexing();
        return ResponseEntity.accepted().build();
    }
}
