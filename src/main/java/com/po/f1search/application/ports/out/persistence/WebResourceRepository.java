package com.po.f1search.application.ports.out.persistence;

import com.po.f1search.model.WebResource.WebResource;

import java.util.List;
import java.util.UUID;

public interface WebResourceRepository {
    void save(WebResource webResource);
    WebResource getById(UUID id);
    List<WebResource> getAll();
}
