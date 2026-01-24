package com.po.f1search.model.WebResource;

import com.po.f1search.model.utils.HtmlContent;
import com.po.f1search.model.utils.Url;

import java.util.List;
import java.util.UUID;

public record WebResource(
        UUID id,
        Url url,
        HtmlContent htmlContent,
        Metadata metadata,
        List<Url> lstLinks
) {
}
