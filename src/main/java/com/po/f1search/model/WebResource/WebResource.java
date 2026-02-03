package com.po.f1search.model.WebResource;

import com.po.f1search.model.utils.HtmlContent;
import com.po.f1search.model.utils.Url;

import java.util.List;
import java.util.UUID;

public record WebResource(
        UUID id,
        Url url,
        HtmlContent htmlContent,
        String title,
        String description,
        List<String> keywords,
        List<Url> lstLinks
) {
}
